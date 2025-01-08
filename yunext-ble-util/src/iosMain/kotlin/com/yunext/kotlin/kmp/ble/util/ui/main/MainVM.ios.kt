package com.yunext.kotlin.kmp.ble.util.ui.main

import com.yunext.kotlin.kmp.ble.core.PlatformPermission
import com.yunext.kotlin.kmp.ble.util.domain.HDResult
import com.yunext.kotlin.kmp.ble.util.domain.Project
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

actual fun generateMainVM(): IMainVM {
    return object :IMainVM{
        private val _state: MutableStateFlow<MainVMState> = MutableStateFlow(MainVMState())
        override val state: StateFlow<MainVMState> = _state.asStateFlow()

        override fun onCleared() {
        }

        override suspend fun addSuspend(project: Project): HDResult<Boolean> {
            return HDResult.Success(true)
        }

        override suspend fun editSuspend(project: Project): HDResult<Boolean> {
            return HDResult.Success(true)
        }

        override fun selectProject(project: Project) {
        }

        override fun requestPermission(permission: PlatformPermission) {
        }

    }
}