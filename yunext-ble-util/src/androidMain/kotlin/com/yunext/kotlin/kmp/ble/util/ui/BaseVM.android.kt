package com.yunext.kotlin.kmp.ble.util.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope

actual open class PlatformViewModel : ViewModel() {
    protected actual val scope: CoroutineScope
        get() = viewModelScope

    protected actual override fun onCleared() {
        super.onCleared()
    }
}