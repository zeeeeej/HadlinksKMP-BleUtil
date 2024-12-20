package com.yunext.kotlin.kmp.ble.util.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yunext.kotlin.kmp.ble.core.PlatformBluetoothContext
import com.yunext.kotlin.kmp.ble.core.PlatformPermission
import com.yunext.kotlin.kmp.ble.core.PlatformPermissionStatus
import com.yunext.kotlin.kmp.ble.core.platformBluetoothContext
import com.yunext.kotlin.kmp.ble.util.domain.HDResult
import com.yunext.kotlin.kmp.ble.util.domain.Project
import com.yunext.kotlin.kmp.ble.util.domain.ProjectRepository
import com.yunext.kotlin.kmp.ble.util.impl.ProjectRepositoryImpl
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.uuid.ExperimentalUuidApi

data class MainVMState(
    val project: Project? = null,
    val projectList: List<Project> = emptyList(),
    val enable: Boolean = false,
    val location: Boolean = false,
    val permissions: List<Pair<PlatformPermission, PlatformPermissionStatus>> = emptyList(),
)


@ExperimentalUuidApi

class MainVM : ViewModel() {
    private val projectRepository: ProjectRepository by lazy {
        ProjectRepositoryImpl()
    }

    private val platformBluetoothContext: PlatformBluetoothContext = platformBluetoothContext()

    private val _state: MutableStateFlow<MainVMState> = MutableStateFlow(MainVMState())
    val state: StateFlow<MainVMState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            launch {
                platformBluetoothContext.enable.collect {
                    _state.value = state.value.copy(
                        enable = it
                    )
                }
            }
            launch {
                platformBluetoothContext.location.collect {
                    _state.value = state.value.copy(
                        location = it
                    )
                }
            }
            launch {
                platformBluetoothContext.permissions.collect {
                    _state.value = state.value.copy(
                        permissions = it.toList()
                    )
                }
            }

            projectList()
            launch {
                delay(500)
                this@MainVM.state.value.projectList.run {
                    if (this.isNotEmpty()) {
                        selectProject(this[0])
                    }
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        println("MainVM onClear")
    }

    private fun projectList() {
        viewModelScope.launch {
            try {
                val all = projectRepository.all()
                _state.value = state.value.copy(
                    projectList = all,
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    suspend fun addSuspend(project: Project): HDResult<Boolean> {
        return try {
            val add = projectRepository.add(project)
            if (add) {
                val all = projectRepository.all()
                _state.value = state.value.copy(
                    projectList = all, project = project
                )
            }
            HDResult.Success(true)
        } catch (e: Throwable) {
            HDResult.Fail(e)
        }
    }

    suspend fun editSuspend(project: Project): HDResult<Boolean> {
        return try {
            val add = projectRepository.edit(project)
            if (add) {
                val all = projectRepository.all()
                _state.value = state.value.copy(
                    projectList = all, project = project
                )
            }
            HDResult.Success(true)
        } catch (e: Throwable) {
            HDResult.Fail(e)
        }
    }

    fun add(project: Project) {
        viewModelScope.launch {
            try {
                val add = projectRepository.add(project)
                if (add) {
                    val all = projectRepository.all()
                    _state.value = state.value.copy(
                        projectList = all, project = project
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }


    }

    fun selectProject(project: Project) {
        _state.value = state.value.copy(project = project)
    }


    fun requestPermission(permission: PlatformPermission) {
        platformBluetoothContext.requestPermission(permission)
    }
}