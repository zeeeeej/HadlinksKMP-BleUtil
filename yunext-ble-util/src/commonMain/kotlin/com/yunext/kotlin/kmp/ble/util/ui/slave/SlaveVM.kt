package com.yunext.kotlin.kmp.ble.util.ui.slave

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.MutableCreationExtras
import com.yunext.kotlin.kmp.ble.core.PlatformBluetoothContext
import com.yunext.kotlin.kmp.ble.core.PlatformBluetoothGattCharacteristic
import com.yunext.kotlin.kmp.ble.core.PlatformPermission
import com.yunext.kotlin.kmp.ble.core.PlatformPermissionStatus
import com.yunext.kotlin.kmp.ble.core.platformBluetoothContext
import com.yunext.kotlin.kmp.ble.history.BluetoothHistory
import com.yunext.kotlin.kmp.ble.slave.PlatformSlave
import com.yunext.kotlin.kmp.ble.slave.SlaveState
import com.yunext.kotlin.kmp.ble.slave.SlaveWriteParam
import com.yunext.kotlin.kmp.ble.util.domain.Project
import com.yunext.kotlin.kmp.ble.util.domain.his.His
import com.yunext.kotlin.kmp.ble.util.domain.his.uuid
import com.yunext.kotlin.kmp.ble.util.impl.ProjectRepositoryImpl
import com.yunext.kotlin.kmp.ble.util.impl.DefaultProfile
import com.yunext.kotlin.kmp.ble.util.impl.RandomProfile
import com.yunext.kotlin.kmp.ble.util.impl.WaterDispenserProfile
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.random.Random
import kotlin.uuid.ExperimentalUuidApi

data class SlaveVMState(
    val enable: Boolean = false,
    val location: Boolean = false,
    val permissions: List<Pair<PlatformPermission, PlatformPermissionStatus>> = emptyList(),
    val slaveState: SlaveState,
    val histories: List<BluetoothHistory> = emptyList()
)

object ProjectKey : CreationExtras.Key<Project>

fun creationExtrasOfProject(project: Project): CreationExtras {
    val mutableExtras = MutableCreationExtras()
    mutableExtras[ProjectKey] = project
    return mutableExtras
}

//@Composable
//inline fun <reified VM : ViewModel> composeViewModel():VM{
//    val factory = object : ViewModelProvider.Factory {
//        override fun <T : ViewModel> create(modelClass: KClass<T>, extras: CreationExtras): T {
//            return VM::class.constructors.first().call() as T
//        }
//    }
//    return viewModel(factory = factory)
//}

//internal class SlaveVMFactory(private val project: Project): AbstractSavedStateViewModelFactory(){

//internal class VMFactory : ViewModelProvider.Factory {
//    override fun <T : ViewModel> create(modelClass: KClass<T>, extras: CreationExtras): T {
//         val project: Project?  = extras[ProjectKey]
//        check(project!=null){
//            "project is null"
//        }
//        return when (modelClass) {
//            SlaveVM::class -> SlaveVM(project) as T
//            MasterVM::class -> MasterVM(project) as T
//            else -> super.create(modelClass, extras)
//        }
//    }


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

//}

class SlaveVM(handler: SavedStateHandle, project: Project) : ViewModel() {
    private val platformBluetoothContext: PlatformBluetoothContext = platformBluetoothContext()


    private val setting = when (project.id) {
        ProjectRepositoryImpl.p3.id -> {
            WaterDispenserProfile(project.defaultDeviceName).create()
        }

        ProjectRepositoryImpl.p1.id -> {

            DefaultProfile(project.defaultDeviceName).create()
        }

        ProjectRepositoryImpl.p2.id -> {
            DefaultProfile(project.defaultDeviceName).create()
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

                    when (val s = state.value.slaveState) {
                        is SlaveState.AdvertiseSuccess -> {}
                        is SlaveState.Connected -> startWriteJob(s)
                        is SlaveState.Idle -> {
                            startWriteJob?.cancel()
                        }

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

    private var startWriteJob: Job? = null

    @OptIn(ExperimentalUuidApi::class)
    private fun startWriteJob(connected: SlaveState.Connected) {
        val first = connected.services.firstOrNull() {
            it.uuid.toString() == His.BaseService.FilterService.uuid
        }?.characteristics?.firstOrNull() {
            it.properties.contains(PlatformBluetoothGattCharacteristic.Property.Notify) or
                    it.properties.contains(PlatformBluetoothGattCharacteristic.Property.Indicate)
        } ?: return
        println("startWriteJob $first")
        startWriteJob?.cancel()
        startWriteJob = viewModelScope.launch {
            while (isActive) {
                delay(3000)
                slave.notify(
                    SlaveWriteParam(
                        first,
                        byteArrayOf(0xFF.toByte(), 0xFF.toByte()) + Random.nextBytes(4),
                        false
                    )
                )
            }
        }

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