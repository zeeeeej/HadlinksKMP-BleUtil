package com.yunext.kotlin.kmp.ble.util.domain

import com.yunext.kotlin.kmp.ble.slave.SlaveSetting

interface IProfile {
    fun create(): SlaveSetting
}