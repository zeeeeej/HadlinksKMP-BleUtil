package com.yunext.kotlin.kmp.ble.util.ui.slave

import androidx.compose.ui.text.intl.LocaleList.Companion.Empty
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.MutableCreationExtras
import com.juul.kable.characteristicOf
import com.yunext.kotlin.kmp.ble.core.PlatformBluetoothContext
import com.yunext.kotlin.kmp.ble.core.PlatformBluetoothGattCharacteristic
import com.yunext.kotlin.kmp.ble.core.PlatformPermission
import com.yunext.kotlin.kmp.ble.core.PlatformPermissionStatus
import com.yunext.kotlin.kmp.ble.core.bluetoothGattCharacteristic
import com.yunext.kotlin.kmp.ble.core.platformBluetoothContext
import com.yunext.kotlin.kmp.ble.history.BluetoothHistory
import com.yunext.kotlin.kmp.ble.slave.PlatformBleSlaveOnCharacteristicReadRequest
import com.yunext.kotlin.kmp.ble.slave.PlatformBleSlaveOnCharacteristicWriteRequest
import com.yunext.kotlin.kmp.ble.slave.PlatformBleSlaveOnDescriptorReadRequest
import com.yunext.kotlin.kmp.ble.slave.PlatformBleSlaveOnDescriptorWriteRequest
import com.yunext.kotlin.kmp.ble.slave.PlatformBleSlaveOnExecuteWrite
import com.yunext.kotlin.kmp.ble.slave.PlatformBleSlaveOnMtuChanged
import com.yunext.kotlin.kmp.ble.slave.PlatformBleSlaveOnNotificationSent
import com.yunext.kotlin.kmp.ble.slave.PlatformBleSlaveOnPhyRead
import com.yunext.kotlin.kmp.ble.slave.PlatformBleSlaveOnPhyUpdate
import com.yunext.kotlin.kmp.ble.slave.PlatformResponse
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
import kotlinx.serialization.json.JsonObject
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

@OptIn(ExperimentalUuidApi::class, ExperimentalStdlibApi::class)
class SlaveVM(handler: SavedStateHandle, private val project: Project) : ViewModel() {
    private val platformBluetoothContext: PlatformBluetoothContext = platformBluetoothContext()


    private val setting = when (project.id) {
        ProjectRepositoryImpl.p3.id -> {
            WaterDispenserProfile(project.defaultDeviceName).create()
        }

        ProjectRepositoryImpl.p1.id -> {

            DefaultProfile(project.defaultDeviceName).create()
        }

        ProjectRepositoryImpl.p0.id -> {

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
                    when (it) {
                        is PlatformBleSlaveOnCharacteristicReadRequest -> {}
                        is PlatformBleSlaveOnCharacteristicWriteRequest -> {
                            resp(it)
                        }

                        is PlatformBleSlaveOnDescriptorReadRequest -> {}
                        is PlatformBleSlaveOnDescriptorWriteRequest -> {}
                        is PlatformBleSlaveOnExecuteWrite -> {}
                        is PlatformBleSlaveOnMtuChanged -> {}
                        is PlatformBleSlaveOnNotificationSent -> {}
                        is PlatformBleSlaveOnPhyRead -> {}
                        is PlatformBleSlaveOnPhyUpdate -> {}
                    }

                }
            }
            launch {
                slave.history.histories.collect {
                    _state.value = state.value.copy(histories = it.asReversed())
                }
            }
        }
    }

    private suspend fun resp(request: PlatformBleSlaveOnCharacteristicWriteRequest) {
        when (project.id) {
            ProjectRepositoryImpl.p3.id -> {
            }

            ProjectRepositoryImpl.p1.id -> {
                respAngelLight(request)
            }

            ProjectRepositoryImpl.p0.id -> {
                respAngelLight(request)

            }

            ProjectRepositoryImpl.p2.id -> {
            }
        }
    }

    private suspend fun respAngelLight(request: PlatformBleSlaveOnCharacteristicWriteRequest) {
        val value = request.value ?: return
        // angel_light 模拟回复

        val hexValue = value.toHexString()
        println("[BLE]hexValue:$hexValue")
        when {
            // 鉴权 5aa5b100104b6ab6c528f6e7b4f218f35377af3d
            hexValue.startsWith("5aa5b1") -> {
                println("[BLE]ok1")
                val state = state.value.slaveState
                if (state is SlaveState.Connected) {
                    val notify = state.services.firstOrNull() {
                        it.uuid.toString() == "616e6765-6c62-6c70-6573-657276696365"
                    }?.characteristics?.firstOrNull() {
                        (it.uuid.toString() == "616e6765-6c62-6c65-6e6f-746964796368") and (
                                it.properties.contains(
                                    PlatformBluetoothGattCharacteristic.Property.Notify
                                ) or
                                        it.properties.contains(
                                            PlatformBluetoothGattCharacteristic.Property.Indicate
                                        ))
                    } ?: return
                    println("[BLE]ok2")
                    // 5a a5 b2 00 01 00 b2
                    val param = SlaveWriteParam(
                        characteristic = notify,
                        byteArrayOf(
                            0x5a,
                            0xa5.toByte(),
                            0xb2.toByte(),
                            0x00,
                            0x01,
                            0x00,
                            0xb2.toByte()
                        ),
                        request.responseNeeded
                    )
                    slave.notify(param)
                }


            }
            // 开关机:5aa5a30003020201aa
            // 开关机:5aa5a40003020200aa
            hexValue.startsWith("5aa5a300030202") -> {
                val state = state.value.slaveState
                if (state is SlaveState.Connected) {
                    val notify = state.services.firstOrNull() {
                        it.uuid.toString() == "616e6765-6c62-6c70-6573-657276696365"
                    }?.characteristics?.firstOrNull() {
                        (it.uuid.toString() == "616e6765-6c62-6c65-6e6f-746964796368") and (
                                it.properties.contains(
                                    PlatformBluetoothGattCharacteristic.Property.Notify
                                ) or
                                        it.properties.contains(
                                            PlatformBluetoothGattCharacteristic.Property.Indicate
                                        ))
                    } ?: return
                    val param = SlaveWriteParam(
                        characteristic = notify,
                        "5aa5a40003020200aa".hexToByteArray(),
                        request.responseNeeded
                    )
                    slave.notify(param)
                    delay(1000)

                    slave.notify(
                        SlaveWriteParam(
                            characteristic = notify,
                            if (value[value.size - 2] == 0x01.toByte()) {
                                "5aa5a2000403060000ae"
                            } else {
                                "5aa5a2000403060001af"
                            }.hexToByteArray(),
                            request.responseNeeded
                        )
                    )
                }


            }
            // 冲洗:5aa5a30003020301aa
            // 冲洗:5aa5a40003020300aa
            hexValue.startsWith("5aa5a300030203") -> {
                val state = state.value.slaveState
                if (state is SlaveState.Connected) {
                    val notify = state.services.firstOrNull() {
                        it.uuid.toString() == "616e6765-6c62-6c70-6573-657276696365"
                    }?.characteristics?.firstOrNull() {
                        (it.uuid.toString() == "616e6765-6c62-6c65-6e6f-746964796368") and (
                                it.properties.contains(
                                    PlatformBluetoothGattCharacteristic.Property.Notify
                                ) or
                                        it.properties.contains(
                                            PlatformBluetoothGattCharacteristic.Property.Indicate
                                        ))
                    } ?: return
                    // 5a a5 b2 00 01 00 b2
                    val param = SlaveWriteParam(
                        characteristic = notify,
                        "5aa5a40003020300ab".hexToByteArray(),
                        request.responseNeeded
                    )
                    slave.notify(param)
                    delay(1000)
                    slave.notify(
                        SlaveWriteParam(
                            characteristic = notify,
                            if (value[value.size-2]==0x01.toByte()){
                                "5aa5a2000403060004b2"
                            }else{
                                "5aa5a2000403060000ae"
                            }.hexToByteArray(),
                            request.responseNeeded
                        )
                    )
                }


            }

            // 产测:5aa5fe0000fd
            // 产测:5aa5fe00120203000506010203040506060102030405064f
            hexValue.startsWith("5aa5fe") -> {
                val state = state.value.slaveState
                if (state is SlaveState.Connected) {
                    val notify = state.services.firstOrNull() {
                        it.uuid.toString() == "616e6765-6c62-6c70-6573-657276696365"
                    }?.characteristics?.firstOrNull() {
                        (it.uuid.toString() == "616e6765-6c62-6c65-6e6f-746964796368") and (
                                it.properties.contains(
                                    PlatformBluetoothGattCharacteristic.Property.Notify
                                ) or
                                        it.properties.contains(
                                            PlatformBluetoothGattCharacteristic.Property.Indicate
                                        ))
                    } ?: return
                    // 5a a5 b2 00 01 00 b2
                    // 01020304 06313233343536 06373932383131
                    //            123456           792811
                    val param = SlaveWriteParam(
                        characteristic = notify,
                        "5aa5fe001201020304063132333435360637393238313196".hexToByteArray(),
                        request.responseNeeded
                    )
                    slave.notify(param)
                }


            }
            // 恢复出厂设置 5aa5a30003022100c8
            // 恢复出厂设置 5aa5a40003022100c9
            hexValue.startsWith("5aa5a300030221") -> {
                val state = state.value.slaveState
                if (state is SlaveState.Connected) {
                    val notify = state.services.firstOrNull() {
                        it.uuid.toString() == "616e6765-6c62-6c70-6573-657276696365"
                    }?.characteristics?.firstOrNull() {
                        (it.uuid.toString() == "616e6765-6c62-6c65-6e6f-746964796368") and (
                                it.properties.contains(
                                    PlatformBluetoothGattCharacteristic.Property.Notify
                                ) or
                                        it.properties.contains(
                                            PlatformBluetoothGattCharacteristic.Property.Indicate
                                        ))
                    } ?: return
                    val param = SlaveWriteParam(
                        characteristic = notify,
                        "5aa5a40003022100c9".hexToByteArray(),
                        request.responseNeeded
                    )
                    slave.notify(param)
                }


            }
        }


    }

    private var startWriteJob: Job? = null

    @OptIn(ExperimentalUuidApi::class)
    private fun startWriteJob(connected: SlaveState.Connected) {
        val first = connected.services.firstOrNull() {
            it.uuid.toString() == TEST_Service
        }?.characteristics?.firstOrNull() {
            (it.uuid.toString() == TEST_CH) and (
                    it.properties.contains(PlatformBluetoothGattCharacteristic.Property.Notify) or
                            it.properties.contains(PlatformBluetoothGattCharacteristic.Property.Indicate))
        } ?: return
        println("startWriteJob $first $TEST_Service $TEST_CH")
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


val TEST_Service = His.BaseService.FilterService.uuid

val TEST_CH =
    His.uuidOf("a201", His.BaseService.FilterService)


class A {

    companion object
}

val A.Companion.Empty: A
    get() = A()


