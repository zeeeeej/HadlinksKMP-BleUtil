package com.yunext.kotlin.kmp.ble.util.ui.main

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.yunext.kotlin.kmp.ble.core.PlatformPermission
import com.yunext.kotlin.kmp.ble.util.domain.Project
import com.yunext.kotlin.kmp.ble.util.ui.PlatformBluetoothContextInfo
import com.yunext.kotlin.kmp.ble.util.ui.Screen
import com.yunext.kotlin.kmp.ble.util.ui.project.navigatorProjectAddScreen
import com.yunext.kotlin.kmp.ble.util.ui.project.navigatorProjectListScreen
import com.yunext.kotlin.kmp.ble.util.util.randomBG

internal fun NavHostController.navigatorMainScreen() {
    this.navigate(route = Screen.Main.name, builder = {
        // 启用动画、清除之前的路由等
        popUpTo(this@navigatorMainScreen.graph.startDestinationId) {
            this.inclusive = false
        }
        launchSingleTop = true
    })
}

@Composable
internal fun MainScreen(
    modifier: Modifier,
    navController: NavHostController,
    project: Project?,
    state:MainVMState,
    onSlave: (Project) -> Unit,
    onMaster: (Project) -> Unit,
    onRequestPermission: (PlatformPermission) -> Unit,
) {
    Column(modifier.padding(0.dp)) {

        ProjectTitle(Modifier.wrapContentWidth().height(56.dp), project, onAdd = {
            navController.navigatorProjectAddScreen(null)
        }, onList = {
            navController.navigatorProjectListScreen()
        })

        PlatformBluetoothContextInfo(
            Modifier.fillMaxWidth(),
            state.enable,
            state.location,
            state.permissions
        ) {
            onRequestPermission(it)

        }

        Row(
            modifier = Modifier.fillMaxWidth().weight(1f).randomBG()
        ) {
            Item(Modifier.fillMaxSize().clickable(enabled = project != null) {
                onSlave(project!!)
            }, "Slave")
        }

        Row(
            modifier = Modifier.fillMaxWidth().weight(1f).randomBG()
        ) {
            Item(Modifier.fillMaxSize().clickable(enabled = project != null) {
                onMaster(project!!)
            }, "Master")
        }
    }


}

@Composable
private fun Item(modifier: Modifier, text: String) {
    Box(modifier, contentAlignment = Alignment.Center) {
        Text(
            text,
            style = TextStyle.Default.copy(fontSize = 64.sp, fontWeight = FontWeight.Bold)
        )
    }
}

@Composable
private fun ProjectTitle(
    modifier: Modifier = Modifier, project: Project?,
    onAdd: () -> Unit,
    onList: () -> Unit,
) {
    Row(
        modifier
            .clickable {
//            if (project == null) {
//                onAdd()
//            } else {
//                onList()
//            }
                onList()
            }
            .padding(start = 16.dp, end = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        AnimatedContent(project?.name ?: "点击选择项目") {
            Text(
                text = it, style = TextStyle.Default.copy(
                    fontWeight = FontWeight.Bold, fontSize = 24.sp
                )
            )
        }

        Spacer(Modifier.width(4.dp))
        AnimatedVisibility(project == null) {
            Image(Icons.AutoMirrored.Filled.KeyboardArrowRight, null)
        }
    }
}
