package com.yunext.kotlin.kmp.ble.util.ui.project

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import com.yunext.kotlin.kmp.ble.util.domain.Project
import com.yunext.kotlin.kmp.ble.util.impl.DefaultProject
import com.yunext.kotlin.kmp.ble.util.impl.asDefaultProject
import com.yunext.kotlin.kmp.ble.util.ui.Screen
import com.yunext.kotlin.kmp.ble.util.ui.common.HDCommonPageWithTitle
import kotlinx.serialization.json.Json

internal const val KEY_ProjectAdd = "ProjectAdd_project"

internal fun NavHostController.navigatorProjectAddScreen(project: Project?) {
    val json = if (project == null) "{}" else Json.encodeToString(
        DefaultProject.serializer(),
        project.asDefaultProject()
    )
    this.navigate(route = Screen.ProjectAdd.name + "/${json}", builder = {
        // 启用动画、清除之前的路由等
//        popUpTo(this@navigatorProjectListScreen.graph.startDestinationId) {
//            this.inclusive = false
//        }
        launchSingleTop = true
    })
}

@Composable
fun ProjectAddScreen(
    modifier: Modifier = Modifier.fillMaxSize(),
    controller: NavHostController,
    project: Project?,
    onBack: () -> Unit,
    onCommit: (Project) -> Unit
) {
    HDCommonPageWithTitle(
        modifier,
        title = "${if (project == null) "添加" else "编辑"}项目",
        onBack = onBack
    ) {
        Column {
            var curId by remember {
                mutableStateOf(project?.id ?: "")
            }
            var curName by remember {
                mutableStateOf(project?.name ?: "")
            }

            var curSecret by remember {
                mutableStateOf(project?.secret ?: "")
            }

            Row {
                Text("id")
                TextField(value = curId, onValueChange = {
                    curId = it
                })
            }

            Row {
                Text("name")
                TextField(value = curName, onValueChange = { curName = it })
            }

            Row {
                Text("secret")
                TextField(value = curSecret, onValueChange = {
                    curSecret = it
                })
            }

            Button(onClick = {
                onCommit(
                    DefaultProject(
                        id = curId,
                        name = curName,
//                        scanFilters = listOf(DeviceNamePlatformMasterScanFilter("angel")),
                        secret = curSecret
                    )
                )
            }) {
                Text(if (project == null) "添加" else "修改")
            }
        }
    }
}