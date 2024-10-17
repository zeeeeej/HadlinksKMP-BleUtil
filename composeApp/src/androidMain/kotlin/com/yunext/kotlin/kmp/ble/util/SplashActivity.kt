package com.yunext.kotlin.kmp.ble.util

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge

class SplashActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        startActivity(Intent(this, MainActivity::class.java))
        finish()
//        setContent {
//
//            Box(
//                Modifier
//                    .fillMaxSize()
//                    .background(randomZhongGuoSe().color),
//                contentAlignment = Alignment.Center
//            ) {
//                Text("hello")
//            }
//
//        }
    }
}