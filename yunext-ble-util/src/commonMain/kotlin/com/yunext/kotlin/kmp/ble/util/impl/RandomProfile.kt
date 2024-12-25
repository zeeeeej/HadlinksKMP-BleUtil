package com.yunext.kotlin.kmp.ble.util.impl

import com.yunext.kotlin.kmp.ble.core.PlatformBluetoothGattCharacteristic
import com.yunext.kotlin.kmp.ble.core.PlatformBluetoothGattDescriptor
import com.yunext.kotlin.kmp.ble.core.PlatformBluetoothGattService
import com.yunext.kotlin.kmp.ble.core.bluetoothGattCharacteristic
import com.yunext.kotlin.kmp.ble.core.bluetoothGattDescriptor
import com.yunext.kotlin.kmp.ble.core.bluetoothGattService
import com.yunext.kotlin.kmp.ble.slave.SlaveSetting
import com.yunext.kotlin.kmp.ble.util.display
import com.yunext.kotlin.kmp.ble.util.domain.IProfile
import com.yunext.kotlin.kmp.common.logger.HDLogger.Companion.d
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid


class RandomProfile(val name:String) : IProfile {

    @ExperimentalUuidApi
    private fun generateServiceRandom(): PlatformBluetoothGattService {
        val descriptorCreator = {
            bluetoothGattDescriptor(
                Uuid.random(),
                arrayOf(
                    PlatformBluetoothGattDescriptor.Permission.PermissionRead,
                    PlatformBluetoothGattDescriptor.Permission.PermissionWrite
                ), byteArrayOf()
            )
        }
        val characteristicCreator: (Array<PlatformBluetoothGattDescriptor>) -> PlatformBluetoothGattCharacteristic =
            {
                bluetoothGattCharacteristic(
                    Uuid.random(),
                    arrayOf(
                        PlatformBluetoothGattCharacteristic.Permission.Read,
                        PlatformBluetoothGattCharacteristic.Permission.Write
                    ),
                    arrayOf(
                        PlatformBluetoothGattCharacteristic.Property.Notify,
                        PlatformBluetoothGattCharacteristic.Property.Read,
                        PlatformBluetoothGattCharacteristic.Property.WriteNoResponse,
                        PlatformBluetoothGattCharacteristic.Property.Write
                    ),
                    it,
                    byteArrayOf()
                )
            }
        val serviceCreator: (Array<PlatformBluetoothGattCharacteristic>) -> PlatformBluetoothGattService =
            {
                bluetoothGattService(
                    Uuid.random(), PlatformBluetoothGattService.ServiceType.Primary,
                    emptyArray(), it
                )
            }

        return serviceCreator((0..10).map {
            characteristicCreator(arrayOf(descriptorCreator()))
        }.toTypedArray())
    }
    @ExperimentalUuidApi
    private fun generateServicesRandom(): List<PlatformBluetoothGattService> {

        val services = (0..5).map {
            generateServiceRandom()
        }
        return services.apply {
            d("[BLE]", this.display)
        }
    }


    override fun create() = object : SlaveSetting {
        override val deviceName: String = "${name}_84C2E4030202"

        @ExperimentalUuidApi
        override val broadcastService: PlatformBluetoothGattService = generateServiceRandom()

        @ExperimentalUuidApi
        override val services: Array<PlatformBluetoothGattService> =
            generateServicesRandom().toTypedArray()
        override val broadcastTimeout: Long = 60_000

    }
}