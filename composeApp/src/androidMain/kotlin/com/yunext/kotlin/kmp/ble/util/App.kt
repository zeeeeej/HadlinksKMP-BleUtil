package com.yunext.kotlin.kmp.ble.util

import android.app.Application
import com.yunext.kotlin.kmp.context.registerHDContext

class App:Application() {
    override fun onCreate() {
        super.onCreate()
        registerHDContext {
            this.init(this@App)
        }

    }
}