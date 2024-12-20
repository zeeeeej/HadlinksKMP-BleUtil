package com.yunext.kotlin.kmp.ble.util.ui.slave

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.yunext.kotlin.kmp.ble.core.PlatformBluetoothContext
import com.yunext.kotlin.kmp.ble.core.PlatformPermission
import com.yunext.kotlin.kmp.ble.core.PlatformPermissionStatus
import com.yunext.kotlin.kmp.ble.core.platformBluetoothContext
import com.yunext.kotlin.kmp.ble.history.BluetoothHistory
import com.yunext.kotlin.kmp.ble.slave.PlatformSlave
import com.yunext.kotlin.kmp.ble.slave.SlaveState
import com.yunext.kotlin.kmp.ble.util.domain.Project
import com.yunext.kotlin.kmp.ble.util.impl.ProjectRepositoryImpl
import com.yunext.kotlin.kmp.ble.util.impl.DefaultProfile
import com.yunext.kotlin.kmp.ble.util.impl.RandomProfile
import com.yunext.kotlin.kmp.ble.util.impl.WaterDispenserProfile
import com.yunext.kotlin.kmp.ble.util.ui.master.MasterVM
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlin.random.Random
import kotlin.reflect.KClass
import kotlin.uuid.ExperimentalUuidApi

data class SlaveVMState(
    val enable: Boolean = false,
    val location: Boolean = false,
    val permissions: List<Pair<PlatformPermission, PlatformPermissionStatus>> = emptyList(),
    val slaveState: SlaveState,
    val histories: List<BluetoothHistory> = emptyList()
)

//internal class SlaveVMFactory(private val project: Project): AbstractSavedStateViewModelFactory(){
internal class VMFactory(private val project: Project) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: KClass<T>, extras: CreationExtras): T {
        @OptIn(ExperimentalUuidApi::class)
        return when (modelClass) {
            SlaveVM::class -> SlaveVM(project) as T
            MasterVM::class -> MasterVM(project) as T
            else -> super.create(modelClass, extras)
        }
    }


//    override fun <T : ViewModel> create(
//        key: String,
//        modelClass: KClass<T>,
//        handle: SavedStateHandle
//    ): T {
//        @OptIn(ExperimentalUuidApi::class)
//        if (modelClass.isInstance(SlaveVM::class)){
//            @Suppress("UNCHECKED_CAST")
//            return SlaveVM(project) as T
//        }
//        return super.create(modelClass, CreationExtras.Empty)
//    }

}

@ExperimentalUuidApi
class SlaveVM(private val project: Project) : ViewModel() {
    private val platformBluetoothContext: PlatformBluetoothContext = platformBluetoothContext()


    @OptIn(ExperimentalStdlibApi::class)
    private val setting = when (project.id) {
        ProjectRepositoryImpl.p3.id -> {
            WaterDispenserProfile("water_${Random.Default.nextBytes(4).toHexString()}").create()
        }
        ProjectRepositoryImpl.p1.id -> {
            DefaultProfile("angel").create()
        }
        ProjectRepositoryImpl.p2.id -> {
            DefaultProfile("A#QY#URQ6690#686B60").create()
        }
        else -> RandomProfile(project.id).create()
    }
    private val _state: MutableStateFlow<SlaveVMState> =
        MutableStateFlow(SlaveVMState(slaveState = SlaveState.Idle(setting = setting)))

    val state: StateFlow<SlaveVMState> = _state.asStateFlow()
    private val slave by lazy {
        PlatformSlave(context = platformBluetoothContext, setting = setting)
    }


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

            launch {
                slave.slaveState.collect {
                    println("[BLE]vm state = $it")
                    _state.value = state.value.copy(slaveState = it)

                    when (state.value.slaveState) {
                        is SlaveState.AdvertiseSuccess -> {}
                        is SlaveState.Connected -> startWriteJob()
                        is SlaveState.Idle -> {}
                        is SlaveState.ServerOpened -> {}
                    }
                }
            }

            launch {
                slave.eventChannel.receiveAsFlow().collect {
                    println("[BLE]vm event = $it")
                }
            }
            launch {
                slave.history.histories.collect {
                    _state.value = state.value.copy(histories = it.asReversed())
                }
            }
        }
    }

    private fun startWriteJob() {
    }

    fun requestPermission(permission: PlatformPermission) {
        platformBluetoothContext.requestPermission(permission)
    }

    fun start() {
        slave.startBroadcast()
    }

    fun stop() {
        slave.stopBroadcast()
    }


    override fun onCleared() {
        super.onCleared()
        println("slaveVm onClear")
        slave.close()
    }
}