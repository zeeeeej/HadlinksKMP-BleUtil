package com.yunext.kotlin.kmp.ble.util.ui.master

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yunext.kotlin.kmp.ble.core.PlatformBluetoothContext
import com.yunext.kotlin.kmp.ble.core.PlatformBluetoothDevice
import com.yunext.kotlin.kmp.ble.core.PlatformBluetoothGattCharacteristic
import com.yunext.kotlin.kmp.ble.core.PlatformBluetoothGattDescriptor
import com.yunext.kotlin.kmp.ble.core.PlatformBluetoothGattService
import com.yunext.kotlin.kmp.ble.core.PlatformPermission
import com.yunext.kotlin.kmp.ble.core.PlatformPermissionStatus
import com.yunext.kotlin.kmp.ble.core.bluetoothDevice
import com.yunext.kotlin.kmp.ble.core.platformBluetoothContext
import com.yunext.kotlin.kmp.ble.history.BluetoothHistory
import com.yunext.kotlin.kmp.ble.master.DeviceNamePlatformMasterScanFilter
import com.yunext.kotlin.kmp.ble.master.PlatformConnectorStatus
import com.yunext.kotlin.kmp.ble.master.PlatformMaster
import com.yunext.kotlin.kmp.ble.master.PlatformMasterScanResult
import com.yunext.kotlin.kmp.ble.master.PlatformMasterScanStatus
import com.yunext.kotlin.kmp.ble.util.domain.Project
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class MasterVMState(
    val enable: Boolean = false,
    val location: Boolean = false,
    val permissions: List<Pair<PlatformPermission, PlatformPermissionStatus>> = emptyList(),
    val scanStatus: PlatformMasterScanStatus = PlatformMasterScanStatus.ScanStopped,
    val connectStatusList: List<PlatformConnectorStatus> = emptyList(),
    val scanResults: List<PlatformMasterScanResult> = emptyList(),
    val connectServices: List<Pair<String, List<PlatformBluetoothGattService>>> = emptyList(),
    val histories: List<BluetoothHistory> = emptyList()
)


class MasterVM(handle: SavedStateHandle,private val project: Project) : ViewModel() {
    private val platformBluetoothContext: PlatformBluetoothContext = platformBluetoothContext()
    private val _state: MutableStateFlow<MasterVMState> =
        MutableStateFlow(MasterVMState())

    val state: StateFlow<MasterVMState> = _state.asStateFlow()
    private val master by lazy {
        PlatformMaster(
            platformBluetoothContext, listOf(
//            DeviceNamePlatformMasterScanFilter("valve"),
//            DeviceNamePlatformMasterScanFilter("B#"),
//            DeviceNamePlatformMasterScanFilter("angel"),
                DeviceNamePlatformMasterScanFilter(project.id),
            )
        )
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
                master.scanResults.collect {
                    _state.value = state.value.copy(scanResults = it)

                }
            }

            launch {
                master.status.collect {
                    _state.value = state.value.copy(scanStatus = it)
                }
            }

            launch {
                master.connectStatusMap.collect {
                    println("_connectStatusMap vm!")
                    _state.value = state.value.copy(connectStatusList = it.map { (k, v) ->
                        v
                    })
                }

            }

            launch {
                master.connectServicesMap.collect() {
                    _state.value = state.value.copy(connectServices = it.map { (k, v) ->
                        k to v
                    })
                }
            }

            launch {
                master.historyOwner.histories.collect {
                    _state.value = state.value.copy(histories = it.asReversed())
                }
            }
        }
    }

    fun requestPermission(permission: PlatformPermission) {
        platformBluetoothContext.requestPermission(permission)
    }

    fun start() {
        master.startScan()
    }

    fun stop() {
        master.stopScan()
    }

    override fun onCleared() {
        super.onCleared()
        println("MasterVm onCleared")
        master.close()
    }

    fun connect(result: PlatformMasterScanResult) {
        master.connect(
            bluetoothDevice(
                name = result.deviceName ?: "",
                address = result.address ?: ""
            )
        )

    }

    fun connect(result: PlatformBluetoothDevice) {
        master.connect(
            bluetoothDevice(
                name = result.name ?: "",
                address = result.address ?: ""
            )
        )

    }

    fun disconnect(device: PlatformBluetoothDevice) {
        master.disconnect(
            device
        )
    }

    fun notify(
        device: PlatformBluetoothDevice,
        service: PlatformBluetoothGattService,
        characteristic: PlatformBluetoothGattCharacteristic
    ) {
        try {
            val enable =
                !(characteristic.descriptors[0].value.contentEquals(PlatformBluetoothGattDescriptor.Value.EnableNotificationValue.value))
            master.enableNotify(device, service, characteristic, enable)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


}