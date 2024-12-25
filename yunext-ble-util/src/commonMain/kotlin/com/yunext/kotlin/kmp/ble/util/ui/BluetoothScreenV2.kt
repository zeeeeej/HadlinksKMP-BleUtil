package com.yunext.kotlin.kmp.ble.util.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.MutableWindowInsets
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.exclude
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.onConsumedWindowInsetsChanged
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContent
import androidx.compose.material.Scaffold
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import color
import com.yunext.kotlin.kmp.ble.util.domain.HDResult
import com.yunext.kotlin.kmp.ble.util.domain.projectOf
import com.yunext.kotlin.kmp.ble.util.impl.DefaultProject
import com.yunext.kotlin.kmp.ble.util.ui.main.MainScreen
import com.yunext.kotlin.kmp.ble.util.ui.main.MainVM
import com.yunext.kotlin.kmp.ble.util.ui.main.navigatorMainScreen
import com.yunext.kotlin.kmp.ble.util.ui.master.MasterScreen
import com.yunext.kotlin.kmp.ble.util.ui.master.navigatorMasterScreen
import com.yunext.kotlin.kmp.ble.util.ui.project.KEY_ProjectAdd
import com.yunext.kotlin.kmp.ble.util.ui.project.ProjectAddScreen
import com.yunext.kotlin.kmp.ble.util.ui.project.ProjectListScreen
import com.yunext.kotlin.kmp.ble.util.ui.project.navigatorProjectAddScreen
import com.yunext.kotlin.kmp.ble.util.ui.project.navigatorProjectListScreen
import com.yunext.kotlin.kmp.ble.util.ui.slave.SlaveScreen
import com.yunext.kotlin.kmp.ble.util.ui.slave.navigatorSlaveScreen
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class, ExperimentalLayoutApi::class, ExperimentalStdlibApi::class)
@Composable
fun BluetoothScreen(modifier: Modifier) {
    val rootNavController = rememberNavController()
    val coroutineScope = rememberCoroutineScope()
    val currentBackStackEntryAsState by rootNavController.currentBackStackEntryAsState()
    val vm: MainVM = viewModel(){
        MainVM()
    }
    val state by vm.state.collectAsStateWithLifecycle()
    val remainingInsets = remember { MutableWindowInsets() }
    val safeContent = WindowInsets.safeContent
    val rememberScaffoldState = rememberScaffoldState()
    Scaffold(
        scaffoldState = rememberScaffoldState,
        modifier = Modifier.navigationBarsPadding()
            .onConsumedWindowInsetsChanged { consumedWindowInsets ->
                remainingInsets.insets = safeContent.exclude(consumedWindowInsets)
            }) {
        //Text("${currentBackStackEntryAsState?.destination?.route}")
        NavHost(
            navController = rootNavController, startDestination = Screen.Main.name
        ) {
            composable(Screen.Main.name) {
                MainScreen(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(ZhongGuoSe.月白.color)
                        .padding(safeContent.asPaddingValues()),
                    navController = rootNavController,
                    project = state.project,
                    state = state,
                    onSlave = {
                        rootNavController.navigatorSlaveScreen(it)
                    },
                    onMaster = {
                        rootNavController.navigatorMasterScreen(it)
                    }, onRequestPermission = vm::requestPermission
                )
            }

            composable(Screen.Slave.name + "/{${KEY_ProjectAdd}}") { backStackEntry ->
                val source: DefaultProject? =
                    backStackEntry.arguments?.getString(KEY_ProjectAdd)?.run {
                        projectOf(this)
                    }
                check(source != null) {
                    "没有project信息"
                }
                SlaveScreen(controller = rootNavController, project = source, onBack = {
                    rootNavController.navigatorMainScreen()
                })
            }

            composable(Screen.Master.name + "/{${KEY_ProjectAdd}}") { backStackEntry ->
                val source: DefaultProject? =
                    backStackEntry.arguments?.getString(KEY_ProjectAdd)?.run {
                        projectOf(this)
                    }
                check(source != null) {
                    "没有project信息"
                }
                MasterScreen(controller = rootNavController, project = source, onBack = {
                    rootNavController.navigatorMainScreen()
                })
            }

            composable(Screen.ProjectAdd.name + "/{${KEY_ProjectAdd}}",arguments = listOf( navArgument(KEY_ProjectAdd) {
                type = NavType.StringType
            })) { backStackEntry ->
                val source: DefaultProject? =
                    backStackEntry.arguments?.getString(KEY_ProjectAdd)?.run {
                        projectOf(this)
                    }
                ProjectAddScreen(controller = rootNavController, onBack = {
                    rootNavController.navigatorProjectListScreen()
                }, project = source, onCommit = { dest ->
                    if (source == null) {
                        // 添加
                        coroutineScope.launch {
                            when (val result = vm.addSuspend(dest)) {
                                is HDResult.Fail -> rememberScaffoldState.snackbarHostState.showSnackbar(
                                    result.throwable.message ?: "添加失败"
                                )

                                is HDResult.Success -> {
                                    rootNavController.navigatorProjectListScreen()
                                }
                            }
                        }
                    } else {
                        // 修改
                        coroutineScope.launch {
                            when (val result = vm.editSuspend(dest)) {
                                is HDResult.Fail -> rememberScaffoldState.snackbarHostState.showSnackbar(
                                    result.throwable.message ?: "修改失败"
                                )

                                is HDResult.Success -> {
                                    rootNavController.navigatorProjectListScreen()
                                }
                            }
                        }
                    }
                })
            }

            composable(Screen.ProjectList.name) {
                ProjectListScreen(
                    controller = rootNavController, onBack = {
                        rootNavController.navigatorMainScreen()
                    }, projectList = state.projectList, project = state.project, onEdit = {
                        rootNavController.navigatorProjectAddScreen(it)
                    }, onSelected = { p ->
                        vm.selectProject(p)
                        rootNavController.navigatorMainScreen()
                    }
                )
            }
        }
    }
}