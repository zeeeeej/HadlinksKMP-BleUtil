package com.yunext.kotlin.kmp.ble.util.impl

import com.yunext.kotlin.kmp.ble.core.NotifyDescriptorUUID
import com.yunext.kotlin.kmp.ble.core.PlatformBluetoothGattCharacteristic
import com.yunext.kotlin.kmp.ble.core.PlatformBluetoothGattDescriptor
import com.yunext.kotlin.kmp.ble.core.PlatformBluetoothGattService
import com.yunext.kotlin.kmp.ble.core.bluetoothGattCharacteristic
import com.yunext.kotlin.kmp.ble.core.bluetoothGattDescriptor
import com.yunext.kotlin.kmp.ble.core.bluetoothGattService
import com.yunext.kotlin.kmp.ble.slave.SlaveSetting
import com.yunext.kotlin.kmp.ble.util.domain.IProfile
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@ExperimentalUuidApi
class DefaultProfile(private val name: String) : IProfile {

    private fun angelService(): PlatformBluetoothGattService {


        val serviceCreator: (String, Array<PlatformBluetoothGattCharacteristic>) -> PlatformBluetoothGattService =
            { uuid, chs ->
                bluetoothGattService(
                    Uuid.parse(uuid), PlatformBluetoothGattService.ServiceType.Primary,
                    emptyArray(), chs
                )
            }
        val write =

            bluetoothGattCharacteristic(
                Uuid.parse("616e6765-6c62-6c65-6e6f-746964796368"),
                arrayOf(
                    PlatformBluetoothGattCharacteristic.Permission.Read,
                    PlatformBluetoothGattCharacteristic.Permission.Write
                ),
                arrayOf(
                    PlatformBluetoothGattCharacteristic.Property.Read,
                    PlatformBluetoothGattCharacteristic.Property.WriteNoResponse,
                ),
                arrayOf(
                    bluetoothGattDescriptor(
                        uuid = Uuid.parse(NotifyDescriptorUUID),
                        permissions = arrayOf(PlatformBluetoothGattDescriptor.Permission.PermissionRead),
                        byteArrayOf()
                    )
                ),
                byteArrayOf()
            )

        val notify =
            bluetoothGattCharacteristic(
                Uuid.parse("616e6765-6c62-6c65-7365-6e6463686172"),
                arrayOf(
                    PlatformBluetoothGattCharacteristic.Permission.Read,
                    PlatformBluetoothGattCharacteristic.Permission.Write
                ),
                arrayOf(
                    PlatformBluetoothGattCharacteristic.Property.Read,
                    PlatformBluetoothGattCharacteristic.Property.Notify,
                ),
                arrayOf(
                    bluetoothGattDescriptor(
                        uuid = Uuid.parse(NotifyDescriptorUUID),
                        permissions = arrayOf(PlatformBluetoothGattDescriptor.Permission.PermissionRead),
                        byteArrayOf()
                    )
                ),
                byteArrayOf()
            )
        return serviceCreator("616e6765-6c62-6c70-6573-657276696365", arrayOf(write, notify))
    }

    override fun create(): SlaveSetting {
        return object : SlaveSetting {
            @OptIn(ExperimentalStdlibApi::class)
//        override val deviceName: String = "angel_${Random.Default.nextBytes(3).toHexString()}"
//        override val deviceName: String = "B#QY#ZR2P2570#20C590"
            override val deviceName: String = name

            //        override val deviceName: String = "B#QY#${Random.Default.nextBytes(4).toHexString()}#${
//            Random.Default.nextBytes(3).toHexString()
//        }"
            override val broadcastService: PlatformBluetoothGattService = angelService()
            override val services: Array<PlatformBluetoothGattService> =
                arrayOf(angelService())
            override val broadcastTimeout: Long = 60_000

        }
    }

}