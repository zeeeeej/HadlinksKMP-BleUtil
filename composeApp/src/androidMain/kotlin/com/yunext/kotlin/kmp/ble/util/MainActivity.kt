package com.yunext.kotlin.kmp.ble.util

import App
import android.bluetooth.le.ScanSettings
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.yunext.kotlin.kmp.context.application
import com.yunext.kotlin.kmp.context.registerHDContext
import java.util.Scanner

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            App()

            Text("测试kable", modifier = Modifier
                .padding(32.dp)
                .clickable {

                })
        }
    }

    override fun onResume() {
        super.onResume()
//        startService(Intent(this, KtorServer::class.java))
    }

    override fun onStop() {
        super.onStop()
//        stopService(Intent(this, KtorServer::class.java))
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    LaunchedEffect(Unit) {
        registerHDContext {
            init(application)
        }
    }
    App()
}