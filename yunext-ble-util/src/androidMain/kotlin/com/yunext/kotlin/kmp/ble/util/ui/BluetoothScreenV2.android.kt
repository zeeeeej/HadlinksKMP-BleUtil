package com.yunext.kotlin.kmp.ble.util.ui

import androidx.compose.runtime.Composable

@Composable
actual fun <VM : XVM> viewModel(): VM {
    val vm = viewModel<VM>()
    return vm
}