package com.yunext.kotlin.kmp.ble.util.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yunext.kotlin.kmp.ble.core.PlatformPermission
import com.yunext.kotlin.kmp.ble.core.PlatformPermissionStatus
import com.yunext.kotlin.kmp.ble.util.util.DEFAULT_DP
import com.yunext.kotlin.kmp.ble.util.util.randomBG

@Composable
internal fun PlatformBluetoothContextInfo(
    modifier: Modifier = Modifier,
    enable: Boolean,
    location: Boolean,
    permissions: List<Pair<PlatformPermission, PlatformPermissionStatus>>,
    onRequestPermission: (PlatformPermission) -> Unit
) {
    var hide by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        val v = permissions.any { (_, p) ->
            p == PlatformPermissionStatus.Defined
        }
        if (v) {
            hide = true
        }
    }
    Column(
        modifier
            .randomBG()
    ) {
        Text(
            "蓝牙环境",
            style = TextStyle.Default.copy(fontWeight = FontWeight.Bold, fontSize = 22.sp),
            modifier = Modifier

                .padding(12.dp)
                .clip(RoundedCornerShape(DEFAULT_DP))
                .clickable {
                    hide = !hide
                }
                .padding(12.dp).align(Alignment.CenterHorizontally)
        )
        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
            Text("蓝牙： ${if (enable) "开启" else "关闭"}")
            Text("定位： ${if (location) "开启" else "关闭"}")
        }
        if (permissions.isNotEmpty()) {
            AnimatedVisibility(hide) {
                LazyColumn {
                    items(permissions, { it.first }) { (p, s) ->
                        PermissionItem(Modifier.fillParentMaxWidth().clickable {
                            onRequestPermission.invoke(p)
                        }, p, s)
                    }
                }
            }
        }
    }
}

@Composable
private fun PermissionItem(
    modifier: Modifier = Modifier,
    permission: PlatformPermission,
    status: PlatformPermissionStatus
) {
    Row(modifier) {
        Text("${permission.name} ")
        Text(
            text = "${status.name} ", color = when (status) {
                PlatformPermissionStatus.Granted -> Color.Black
                PlatformPermissionStatus.Defined -> Color.Red
            }
        )
    }
}