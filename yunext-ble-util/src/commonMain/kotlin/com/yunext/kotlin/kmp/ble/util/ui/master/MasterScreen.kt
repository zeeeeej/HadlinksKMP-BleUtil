package com.yunext.kotlin.kmp.ble.util.ui.master

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material.BottomDrawer
import androidx.compose.material.BottomDrawerValue.Closed
import androidx.compose.material.Button
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.rememberBottomDrawerState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import color
import com.yunext.kotlin.kmp.ble.master.PlatformConnectorStatus
import com.yunext.kotlin.kmp.ble.master.PlatformMasterScanStatus
import com.yunext.kotlin.kmp.ble.util.domain.Project
import com.yunext.kotlin.kmp.ble.util.impl.DefaultProject
import com.yunext.kotlin.kmp.ble.util.impl.asDefaultProject
import com.yunext.kotlin.kmp.ble.util.ui.HistoriesInfo
import com.yunext.kotlin.kmp.ble.util.ui.PlatformBluetoothContextInfo
import com.yunext.kotlin.kmp.ble.util.ui.Screen
import com.yunext.kotlin.kmp.ble.util.ui.common.HDCommonPageWithTitle
import com.yunext.kotlin.kmp.ble.util.ui.slave.VMFactory
import com.yunext.kotlin.kmp.ble.util.ui.slave.display
import kotlinx.coroutines.launch
import randomZhongGuoSe
import com.yunext.kotlin.kmp.ble.util.util.clipBroad
import kotlinx.serialization.json.Json


internal fun NavHostController.navigatorMasterScreen(project: Project) {
    val json = if (project == null) "{}" else Json.encodeToString(
        DefaultProject.serializer(),
        project.asDefaultProject()
    )
    this.navigate(route = Screen.Master.name+"/${json}" , builder = {
        // 启用动画、清除之前的路由等
//        popUpTo(this@navigatorMainScreen.graph.startDestinationId) {
//            this.inclusive = false
//        }

        launchSingleTop = true
    })
}

@OptIn(ExperimentalMaterialApi::class)
@androidx.compose.runtime.Composable
fun MasterScreen(modifier: Modifier = Modifier.fillMaxSize(),
                 project:Project,
                 controller: NavHostController, onBack: () -> Unit) {
    val masterVM: MasterVM = viewModel(factory = VMFactory(project = project))
    val state by masterVM.state.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    val drawerState = rememberBottomDrawerState(Closed)
    val scaffoldState = rememberScaffoldState()

    val content: @Composable () -> Unit = {
        Column(modifier.fillMaxWidth().padding(horizontal = 0.dp)) {
            PlatformBluetoothContextInfo(
                Modifier.fillMaxWidth(),
                state.enable,
                state.location,
                state.permissions
            ) {
                masterVM.requestPermission(it)
            }

            Column {
                Button(enabled = when (state.scanStatus) {
                    PlatformMasterScanStatus.ScanStopped -> true
                    is PlatformMasterScanStatus.Scanning -> true
                }, onClick = {
                    when (state.scanStatus) {
                        PlatformMasterScanStatus.ScanStopped -> masterVM.start()
                        is PlatformMasterScanStatus.Scanning -> masterVM.stop()
                    }
                }) {
                    Text(
                        when (state.scanStatus) {
                            PlatformMasterScanStatus.ScanStopped -> "开始搜索"
                            is PlatformMasterScanStatus.Scanning -> "停止搜索"
                        }
                    )
                }
            }
            ScanResultInfo(
                Modifier.fillMaxWidth().border(1.dp, randomZhongGuoSe().color).padding(0.dp),
                list = state.scanResults
            ) {
                masterVM.connect(it)
            }

            ConnectInfo(
                Modifier.fillMaxWidth().border(1.dp, randomZhongGuoSe().color).padding(0.dp),
                list = state.connectStatusList,
                onClick = {
                    when (it) {
                        is PlatformConnectorStatus.Connected -> masterVM.disconnect(it.device)
                        is PlatformConnectorStatus.Connecting -> {}
                        is PlatformConnectorStatus.Disconnected -> masterVM.connect(it.device)
                        is PlatformConnectorStatus.Disconnecting -> {}
                        is PlatformConnectorStatus.Idle -> {}
                        is PlatformConnectorStatus.ServiceDiscovered -> masterVM.disconnect(it.device)
                    }
                },
                onNotify = { device, service, char ->
                    masterVM.notify(device, service, char)
                })
        }
    }
    val histories: @Composable () -> Unit = {
        HistoriesInfo(
            Modifier
                .fillMaxWidth()
                .heightIn(min = 320.dp, max = 320.dp), state.histories, onClose = {
                coroutineScope.launch {
                    drawerState.close()
                }
            }, onShare = {
                coroutineScope.launch {
                    clipBroad("master", it.joinToString("\n") { h -> h.display })
                    scaffoldState.snackbarHostState.showSnackbar("已复制至剪贴板！")
                }
            }
        )
    }

    HDCommonPageWithTitle(
        modifier,
        title = "Master",
        onBack = onBack
    ) {
        Box(Modifier.fillMaxSize()) {
            Scaffold(scaffoldState = scaffoldState) {
                BottomDrawer(
                    drawerContent = {
                        histories()
                    },
                    modifier = Modifier.fillMaxSize(),
                    drawerState = drawerState,
                    gesturesEnabled = false//drawerState.isClosed

                ) {
                    content()
                }
            }
        }
    }
}