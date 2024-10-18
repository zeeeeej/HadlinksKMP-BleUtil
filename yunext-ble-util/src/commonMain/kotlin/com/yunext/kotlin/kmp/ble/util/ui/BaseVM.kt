package com.yunext.kotlin.kmp.ble.util.ui

import kotlinx.coroutines.CoroutineScope

@Deprecated("delete")
expect open class PlatformViewModel() {
    protected val scope:CoroutineScope

    protected open fun onCleared()
}

//abstract class BaseVM {
//    protected val vmScope =
//        CoroutineScope(Dispatchers.Main.immediate + SupervisorJob() + CoroutineName("SlaveVMSlaveVM"))
//
//    protected open fun onClear() {
//        vmScope.cancel()
//    }
//}