package com.yunext.kotlin.kmp.ble.util.impl

import com.yunext.kotlin.kmp.ble.core.PlatformBluetoothGattCharacteristic
import com.yunext.kotlin.kmp.ble.core.PlatformBluetoothGattDescriptor
import com.yunext.kotlin.kmp.ble.core.PlatformBluetoothGattService
import com.yunext.kotlin.kmp.ble.core.bluetoothGattCharacteristic
import com.yunext.kotlin.kmp.ble.core.bluetoothGattDescriptor
import com.yunext.kotlin.kmp.ble.core.bluetoothGattService
import com.yunext.kotlin.kmp.ble.slave.SlaveSetting
import com.yunext.kotlin.kmp.ble.util.domain.his.His
import com.yunext.kotlin.kmp.ble.util.domain.his.Sig
import com.yunext.kotlin.kmp.ble.util.domain.his.base
import com.yunext.kotlin.kmp.ble.util.domain.his.uuid
import com.yunext.kotlin.kmp.ble.util.domain.his.uuid16bit
import kotlin.random.Random
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class WaterDispenserProfile(val name: String) {


    fun create(): SlaveSetting {
        return object : SlaveSetting {


            @OptIn(ExperimentalStdlibApi::class)
            override val deviceName: String =
                "${name}"

            @OptIn(ExperimentalUuidApi::class)
            override val broadcastService: PlatformBluetoothGattService = bluetoothGattService(
                uuid = Uuid.parse(His.BaseService.WaterDispenserService.uuid),
                serviceType = PlatformBluetoothGattService.ServiceType.Primary,
                includeServices = emptyArray(),
                characteristics = emptyArray()
            )
            override val services: Array<PlatformBluetoothGattService> = generateServices()
            override val broadcastTimeout: Long = 60_000

        }
    }


    private fun generateServices(): Array<PlatformBluetoothGattService> {
        @OptIn(ExperimentalUuidApi::class)
        val deviceInfoService =
            bluetoothGattService(
                uuid = Uuid.parse(His.BaseService.DeviceInfoService.uuid),
                serviceType = PlatformBluetoothGattService.ServiceType.Primary,
                includeServices = emptyArray(),
                characteristics = generateDeviceInfoServiceGattCharacteristics()
            )

        @OptIn(ExperimentalUuidApi::class)
        val waterDispatcherService =
            bluetoothGattService(
                uuid = Uuid.parse(His.BaseService.WaterDispenserService.uuid),
                serviceType = PlatformBluetoothGattService.ServiceType.Primary,
                includeServices = emptyArray(),
                characteristics = generateWaterDispatcherServiceGattCharacteristics()
            )

        @OptIn(ExperimentalUuidApi::class)
        val filterService =
            bluetoothGattService(
                uuid = Uuid.parse(His.BaseService.FilterService.uuid),
                serviceType = PlatformBluetoothGattService.ServiceType.Primary,
                includeServices = emptyArray(),
                characteristics = generateFilterServiceGattCharacteristics()
            )
        return arrayOf(deviceInfoService, filterService)
    }

    private fun generateDeviceInfoServiceGattCharacteristics(): Array<PlatformBluetoothGattCharacteristic> {
        @OptIn(ExperimentalUuidApi::class)
        val mac =
            bluetoothGattCharacteristic(
                uuid = Uuid.parse(His.uuidOf("A001", His.BaseService.DeviceInfoService)),
                permissions = arrayOf(
                    PlatformBluetoothGattCharacteristic.Permission.Read,
                ),
                properties = arrayOf(
                    PlatformBluetoothGattCharacteristic.Property.Read,
                    PlatformBluetoothGattCharacteristic.Property.Notify,
                ),
                descriptors = arrayOf(
                    bluetoothGattDescriptor(
                        uuid = Uuid.parse(Sig.UUID_CLIENT_CHARACTERISTIC_CONFIGURATION),
                        permissions = arrayOf(PlatformBluetoothGattDescriptor.Permission.PermissionRead,PlatformBluetoothGattDescriptor.Permission.PermissionWrite),
                        value = byteArrayOf(0x00)
                    ),
                    bluetoothGattDescriptor(
                        uuid = Uuid.parse(Sig.UUID_CHARACTERISTIC_USER_DESCRIPTION),
                        permissions = arrayOf(PlatformBluetoothGattDescriptor.Permission.PermissionRead),
                        value = "device mac".encodeToByteArray()
                    ),
                ),
                value = byteArrayOf()
            )

        @OptIn(ExperimentalUuidApi::class)
        val sn =
            bluetoothGattCharacteristic(
                uuid = Uuid.parse(His.uuidOf("A002", His.BaseService.DeviceInfoService)),
                permissions = arrayOf(
                    PlatformBluetoothGattCharacteristic.Permission.Read,
                ),
                properties = arrayOf(
                    PlatformBluetoothGattCharacteristic.Property.Read,
                    PlatformBluetoothGattCharacteristic.Property.Notify,
                ),
                descriptors = arrayOf(
                    bluetoothGattDescriptor(
                        uuid = Uuid.parse(Sig.UUID_CLIENT_CHARACTERISTIC_CONFIGURATION),
                        permissions = arrayOf(PlatformBluetoothGattDescriptor.Permission.PermissionRead,PlatformBluetoothGattDescriptor.Permission.PermissionWrite),
                        value = byteArrayOf(0x00)
                    ),
                    bluetoothGattDescriptor(
                        uuid = Uuid.parse(Sig.UUID_CHARACTERISTIC_USER_DESCRIPTION),
                        permissions = arrayOf(PlatformBluetoothGattDescriptor.Permission.PermissionRead),
                        value = "device sn".encodeToByteArray()
                    ),
                ),
                value = byteArrayOf()
            )

        @OptIn(ExperimentalUuidApi::class)

        val version =
            bluetoothGattCharacteristic(
                uuid = Uuid.parse(His.uuidOf("A003", His.BaseService.DeviceInfoService)),
                permissions = arrayOf(
                    PlatformBluetoothGattCharacteristic.Permission.Read,
                ),
                properties = arrayOf(
                    PlatformBluetoothGattCharacteristic.Property.Read,
                    PlatformBluetoothGattCharacteristic.Property.Notify,
                ),
                descriptors = arrayOf(
                    bluetoothGattDescriptor(
                        uuid = Uuid.parse(Sig.UUID_CLIENT_CHARACTERISTIC_CONFIGURATION),
                        permissions = arrayOf(PlatformBluetoothGattDescriptor.Permission.PermissionRead,PlatformBluetoothGattDescriptor.Permission.PermissionWrite),
                        value = byteArrayOf(0x00)
                    ),
                    bluetoothGattDescriptor(
                        uuid = Uuid.parse(Sig.UUID_CHARACTERISTIC_USER_DESCRIPTION),
                        permissions = arrayOf(PlatformBluetoothGattDescriptor.Permission.PermissionRead),
                        value = "version".encodeToByteArray()
                    ),
                ),
                value = byteArrayOf()
            )


        @OptIn(ExperimentalUuidApi::class)
        val protocolVersion =
            bluetoothGattCharacteristic(
                uuid = Uuid.parse(His.uuidOf("A004", His.BaseService.DeviceInfoService)),
                permissions = arrayOf(
                    PlatformBluetoothGattCharacteristic.Permission.Read,
                ),
                properties = arrayOf(
                    PlatformBluetoothGattCharacteristic.Property.Read,
                    PlatformBluetoothGattCharacteristic.Property.Notify,
                ),
                descriptors = arrayOf(
                    bluetoothGattDescriptor(
                        uuid = Uuid.parse(Sig.UUID_CLIENT_CHARACTERISTIC_CONFIGURATION),
                        permissions = arrayOf(PlatformBluetoothGattDescriptor.Permission.PermissionRead,PlatformBluetoothGattDescriptor.Permission.PermissionWrite),
                        value = byteArrayOf(0x00)
                    ),
                    bluetoothGattDescriptor(
                        uuid = Uuid.parse(Sig.UUID_CHARACTERISTIC_USER_DESCRIPTION),
                        permissions = arrayOf(PlatformBluetoothGattDescriptor.Permission.PermissionRead),
                        value = "protocolVersion".encodeToByteArray()
                    ),
                ),
                value = byteArrayOf()
            )
        return arrayOf(mac, sn, version, protocolVersion)
    }

    private fun generateWaterDispatcherServiceGattCharacteristics(): Array<PlatformBluetoothGattCharacteristic> {
        return emptyArray()
    }

    @OptIn(ExperimentalUuidApi::class)
    private fun generateFilterServiceGattCharacteristics(): Array<PlatformBluetoothGattCharacteristic> {

        val filterLife1 = bluetoothGattCharacteristic(
            uuid = Uuid.parse(
                His.uuidOf(
                    His.filterService16bit(
                        1,
                        His.FilterServiceCharacteristic.FilterLifeN
                    ), His.BaseService.FilterService
                )
            ),

            permissions = arrayOf(
                PlatformBluetoothGattCharacteristic.Permission.Read,
                PlatformBluetoothGattCharacteristic.Permission.Write,
            ),
            properties = arrayOf(
                PlatformBluetoothGattCharacteristic.Property.Read,
                PlatformBluetoothGattCharacteristic.Property.WriteNoResponse,
                PlatformBluetoothGattCharacteristic.Property.Notify,
            ),
            descriptors = arrayOf(
                bluetoothGattDescriptor(
                    uuid = Uuid.parse(Sig.UUID_CLIENT_CHARACTERISTIC_CONFIGURATION),
                    permissions = arrayOf(PlatformBluetoothGattDescriptor.Permission.PermissionRead,PlatformBluetoothGattDescriptor.Permission.PermissionWrite),
                    value = byteArrayOf(0x00)
                ),
                bluetoothGattDescriptor(
                    uuid = Uuid.parse(Sig.UUID_CHARACTERISTIC_USER_DESCRIPTION),
                    permissions = arrayOf(PlatformBluetoothGattDescriptor.Permission.PermissionRead),
                    value = "滤芯1总寿命".encodeToByteArray()
                ),
                bluetoothGattDescriptor(
                    uuid = Uuid.parse(
                        His.uuidOf(
                            His.TslCharacteristicDescriptor.Base.uuid16bit,
                            His.BaseService.FilterService
                        )
                    ),
                    permissions = arrayOf(PlatformBluetoothGattDescriptor.Permission.PermissionRead),
                    value = His.TslCharacteristicDescriptor.base(
                        read = true,
                        write = false,
                        required = true,
                        tslCharacteristicDescriptor = His.TslCharacteristicDescriptor.Base,
                        identifier = "filterLife1",
                        name = "滤芯1总寿命",
                        desc = "滤芯1总寿命",
                    )
                ),
                bluetoothGattDescriptor(
                    uuid = Uuid.parse(
                        His.uuidOf(
                            His.TslCharacteristicDescriptor.IntProperty.uuid16bit,
                            His.BaseService.FilterService
                        )
                    ),
                    permissions = arrayOf(PlatformBluetoothGattDescriptor.Permission.PermissionRead),
                    value = His.TslCharacteristicDescriptor.intProperty(
                        min = 0,
                        max = 100,
                        unit = His.Unit.CE00
                    )
                ),
            ),
            value = byteArrayOf()
        )

        val filterLife2 = bluetoothGattCharacteristic(
            uuid = Uuid.parse(
                His.uuidOf(
                    His.filterService16bit(
                        2,
                        His.FilterServiceCharacteristic.FilterLifeN
                    ), His.BaseService.FilterService
                )
            ),
            permissions = arrayOf(
                PlatformBluetoothGattCharacteristic.Permission.Read,
                PlatformBluetoothGattCharacteristic.Permission.Write,
            ),
            properties = arrayOf(
                PlatformBluetoothGattCharacteristic.Property.Read,
                PlatformBluetoothGattCharacteristic.Property.WriteNoResponse,
                PlatformBluetoothGattCharacteristic.Property.Notify,
            ),
            descriptors = arrayOf(
                bluetoothGattDescriptor(
                    uuid = Uuid.parse(Sig.UUID_CLIENT_CHARACTERISTIC_CONFIGURATION),
                    permissions = arrayOf(PlatformBluetoothGattDescriptor.Permission.PermissionRead,PlatformBluetoothGattDescriptor.Permission.PermissionWrite),
                    value = byteArrayOf(0x00)
                ),
                bluetoothGattDescriptor(
                    uuid = Uuid.parse(Sig.UUID_CHARACTERISTIC_USER_DESCRIPTION),
                    permissions = arrayOf(PlatformBluetoothGattDescriptor.Permission.PermissionRead),
                    value = "滤芯2总寿命".encodeToByteArray()
                ),
                bluetoothGattDescriptor(
                    uuid = Uuid.parse(
                        His.uuidOf(
                            His.TslCharacteristicDescriptor.Base.uuid16bit,
                            His.BaseService.FilterService
                        )
                    ),
                    permissions = arrayOf(PlatformBluetoothGattDescriptor.Permission.PermissionRead),
                    value = His.TslCharacteristicDescriptor.base(
                        read = true,
                        write = false,
                        required = true,
                        tslCharacteristicDescriptor = His.TslCharacteristicDescriptor.Base,
                        identifier = "filterLife2",
                        name = "滤芯2总寿命",
                        desc = "滤芯2总寿命",
                    )
                ),
                bluetoothGattDescriptor(
                    uuid = Uuid.parse(
                        His.uuidOf(
                            His.TslCharacteristicDescriptor.IntProperty.uuid16bit,
                            His.BaseService.FilterService
                        )
                    ),
                    permissions = arrayOf(PlatformBluetoothGattDescriptor.Permission.PermissionRead),
                    value =
                    His.TslCharacteristicDescriptor.intProperty(
                        min = 0,
                        max = 100,
                        unit = His.Unit.CE00
                    )
                ),
            ),
            value = byteArrayOf()
        )

        val filterPercent1 = bluetoothGattCharacteristic(
            uuid = Uuid.parse(
                His.uuidOf(
                    His.filterService16bit(
                        1,
                        His.FilterServiceCharacteristic.FilterPercentN
                    ), His.BaseService.FilterService
                )
            ),
            permissions = arrayOf(
                PlatformBluetoothGattCharacteristic.Permission.Read,
                PlatformBluetoothGattCharacteristic.Permission.Write,
            ),
            properties = arrayOf(
                PlatformBluetoothGattCharacteristic.Property.Read,
                PlatformBluetoothGattCharacteristic.Property.WriteNoResponse,
                PlatformBluetoothGattCharacteristic.Property.Notify,
            ),
            descriptors = arrayOf(
                bluetoothGattDescriptor(
                    uuid = Uuid.parse(Sig.UUID_CLIENT_CHARACTERISTIC_CONFIGURATION),
                    permissions = arrayOf(PlatformBluetoothGattDescriptor.Permission.PermissionRead,PlatformBluetoothGattDescriptor.Permission.PermissionWrite),
                    value =
                    byteArrayOf(0x00)
                ),
                bluetoothGattDescriptor(
                    uuid = Uuid.parse(
                        Sig.UUID_CHARACTERISTIC_USER_DESCRIPTION
                    ),
                    permissions = arrayOf(PlatformBluetoothGattDescriptor.Permission.PermissionRead),
                    value =
                    "滤芯1百分比".encodeToByteArray()
                ),
                bluetoothGattDescriptor(
                    uuid = Uuid.parse(
                        His.uuidOf(
                            His.TslCharacteristicDescriptor.Base.uuid16bit,
                            His.BaseService.FilterService
                        )
                    ),
                    permissions = arrayOf(PlatformBluetoothGattDescriptor.Permission.PermissionRead),
                    value = His.TslCharacteristicDescriptor.base(
                        read = true,
                        write = false,
                        required = true,
                        tslCharacteristicDescriptor = His.TslCharacteristicDescriptor.Base,
                        identifier = "filterPercent1",
                        name = "滤芯1百分比",
                        desc = "滤芯1百分比",
                    )
                ),
                bluetoothGattDescriptor(
                    uuid = Uuid.parse(
                        His.uuidOf(
                            His.TslCharacteristicDescriptor.IntProperty.uuid16bit,
                            His.BaseService.FilterService
                        )
                    ),
                    permissions = arrayOf(PlatformBluetoothGattDescriptor.Permission.PermissionRead),
                    value =
                    His.TslCharacteristicDescriptor.intProperty(
                        min = 0,
                        max = 100,
                        unit = His.Unit.CE00
                    )
                ),
            ),
            value = byteArrayOf()
        )

        val filterPercent2 = bluetoothGattCharacteristic(
            uuid = Uuid.parse(
                His.uuidOf(
                    His.filterService16bit(
                        2,
                        His.FilterServiceCharacteristic.FilterPercentN
                    ), His.BaseService.FilterService
                )
            ),
            permissions = arrayOf(
                PlatformBluetoothGattCharacteristic.Permission.Read,
                PlatformBluetoothGattCharacteristic.Permission.Write,
            ),
            properties = arrayOf(
                PlatformBluetoothGattCharacteristic.Property.Read,
                PlatformBluetoothGattCharacteristic.Property.WriteNoResponse,
                PlatformBluetoothGattCharacteristic.Property.Notify,
            ),
            descriptors = arrayOf(
                bluetoothGattDescriptor(
                    uuid = Uuid.parse(
                        Sig.UUID_CLIENT_CHARACTERISTIC_CONFIGURATION
                    ),
                    permissions = arrayOf(PlatformBluetoothGattDescriptor.Permission.PermissionRead,PlatformBluetoothGattDescriptor.Permission.PermissionWrite),
                    value =
                    byteArrayOf(0x00)
                ),
                bluetoothGattDescriptor(
                    uuid = Uuid.parse(
                        Sig.UUID_CHARACTERISTIC_USER_DESCRIPTION
                    ),
                    permissions = arrayOf(PlatformBluetoothGattDescriptor.Permission.PermissionRead),
                    value =
                    "滤芯2百分比".encodeToByteArray()
                ),
                bluetoothGattDescriptor(
                    uuid = Uuid.parse(
                        His.uuidOf(
                            His.TslCharacteristicDescriptor.Base.uuid16bit,
                            His.BaseService.FilterService
                        )
                    ),
                    permissions = arrayOf(PlatformBluetoothGattDescriptor.Permission.PermissionRead),
                    value = His.TslCharacteristicDescriptor.base(
                        read = true,
                        write = false,
                        required = true,
                        tslCharacteristicDescriptor = His.TslCharacteristicDescriptor.Base,
                        identifier = "filterPercent2",
                        name = "滤芯2百分比",
                        desc = "滤芯2百分比",
                    )
                ),
                bluetoothGattDescriptor(
                    uuid = Uuid.parse(
                        His.uuidOf(
                            His.TslCharacteristicDescriptor.IntProperty.uuid16bit,
                            His.BaseService.FilterService
                        )
                    ),
                    permissions = arrayOf(PlatformBluetoothGattDescriptor.Permission.PermissionRead),
                    value =
                    His.TslCharacteristicDescriptor.intProperty(
                        min = 0,
                        max = 100,
                        unit = His.Unit.CE00
                    )
                ),
            ),
            value = byteArrayOf()
        )

        val filterExpWater1 = bluetoothGattCharacteristic(
            uuid = Uuid.parse(
                His.uuidOf(
                    His.filterService16bit(
                        1,
                        His.FilterServiceCharacteristic.FilterExpWaterN
                    ), His.BaseService.FilterService
                )
            ),
            permissions = arrayOf(
                PlatformBluetoothGattCharacteristic.Permission.Read,
                PlatformBluetoothGattCharacteristic.Permission.Write,
            ),
            properties = arrayOf(
                PlatformBluetoothGattCharacteristic.Property.Read,
                PlatformBluetoothGattCharacteristic.Property.WriteNoResponse,
                PlatformBluetoothGattCharacteristic.Property.Notify,
            ),
            descriptors = arrayOf(
                bluetoothGattDescriptor(
                    uuid = Uuid.parse(
                        Sig.UUID_CLIENT_CHARACTERISTIC_CONFIGURATION
                    ),
                    permissions = arrayOf(PlatformBluetoothGattDescriptor.Permission.PermissionRead,PlatformBluetoothGattDescriptor.Permission.PermissionWrite),
                    value = byteArrayOf(0x00)
                ),
                bluetoothGattDescriptor(
                    uuid = Uuid.parse(
                        Sig.UUID_CHARACTERISTIC_USER_DESCRIPTION
                    ),
                    permissions = arrayOf(PlatformBluetoothGattDescriptor.Permission.PermissionRead),
                    value =
                    "滤芯1预计水量".encodeToByteArray()
                ),
                bluetoothGattDescriptor(
                    uuid = Uuid.parse(
                        His.uuidOf(
                            His.TslCharacteristicDescriptor.Base.uuid16bit,
                            His.BaseService.FilterService
                        )
                    ),
                    permissions = arrayOf(PlatformBluetoothGattDescriptor.Permission.PermissionRead),
                    value = His.TslCharacteristicDescriptor.base(
                        read = true,
                        write = false,
                        required = true,
                        tslCharacteristicDescriptor = His.TslCharacteristicDescriptor.Base,
                        identifier = "filterExpWater1",
                        name = "滤芯1预计水量",
                        desc = "滤芯1预计水量",
                    )
                ),
                bluetoothGattDescriptor(
                    uuid = Uuid.parse(
                        His.uuidOf(
                            His.TslCharacteristicDescriptor.IntProperty.uuid16bit,
                            His.BaseService.FilterService
                        )
                    ),
                    permissions = arrayOf(PlatformBluetoothGattDescriptor.Permission.PermissionRead),
                    value =
                    His.TslCharacteristicDescriptor.intProperty(
                        min = 0,
                        max = 0xffff,
                        unit = His.Unit.CE01
                    )
                ),

                ),
            value = byteArrayOf()
        )

        val filterExpWater2 = bluetoothGattCharacteristic(
            uuid = Uuid.parse(
                His.uuidOf(
                    His.filterService16bit(
                        2,
                        His.FilterServiceCharacteristic.FilterExpWaterN
                    ), His.BaseService.FilterService
                )
            ),
            permissions = arrayOf(
                PlatformBluetoothGattCharacteristic.Permission.Read,
                PlatformBluetoothGattCharacteristic.Permission.Write,
            ),
            properties = arrayOf(
                PlatformBluetoothGattCharacteristic.Property.Read,
                PlatformBluetoothGattCharacteristic.Property.WriteNoResponse,
                PlatformBluetoothGattCharacteristic.Property.Notify,
            ),
            descriptors = arrayOf(
                bluetoothGattDescriptor(
                    uuid = Uuid.parse(
                        Sig.UUID_CLIENT_CHARACTERISTIC_CONFIGURATION
                    ),
                    permissions = arrayOf(PlatformBluetoothGattDescriptor.Permission.PermissionRead,PlatformBluetoothGattDescriptor.Permission.PermissionWrite),
                    value =
                    byteArrayOf(0x00)
                ),
                bluetoothGattDescriptor(
                    uuid = Uuid.parse(
                        Sig.UUID_CHARACTERISTIC_USER_DESCRIPTION
                    ),
                    permissions = arrayOf(PlatformBluetoothGattDescriptor.Permission.PermissionRead),
                    value =
                    "滤芯2预计水量".encodeToByteArray()
                ),
                bluetoothGattDescriptor(
                    uuid = Uuid.parse(
                        His.uuidOf(
                            His.TslCharacteristicDescriptor.Base.uuid16bit,
                            His.BaseService.FilterService
                        )
                    ),
                    permissions = arrayOf(PlatformBluetoothGattDescriptor.Permission.PermissionRead),
                    value = His.TslCharacteristicDescriptor.base(
                        read = true,
                        write = false,
                        required = true,
                        tslCharacteristicDescriptor = His.TslCharacteristicDescriptor.Base,
                        identifier = "filterExpWater2",
                        name = "预计2额定水量",
                        desc = "预计2额定水量",
                    )
                ),
                bluetoothGattDescriptor(
                    uuid = Uuid.parse(
                        His.uuidOf(
                            His.TslCharacteristicDescriptor.IntProperty.uuid16bit,
                            His.BaseService.FilterService
                        )
                    ),
                    permissions = arrayOf(PlatformBluetoothGattDescriptor.Permission.PermissionRead),
                    value =
                    His.TslCharacteristicDescriptor.intProperty(
                        min = 0,
                        max = 0xffff,
                        unit = His.Unit.CE01
                    )
                ),
            ),
            value = byteArrayOf()
        )

        val filterRatWater1 = bluetoothGattCharacteristic(
            uuid = Uuid.parse(
                His.uuidOf(
                    His.filterService16bit(
                        1,
                        His.FilterServiceCharacteristic.FilterRatWaterN
                    ), His.BaseService.FilterService
                )
            ),
            permissions = arrayOf(
                PlatformBluetoothGattCharacteristic.Permission.Read,
                PlatformBluetoothGattCharacteristic.Permission.Write,
            ),
            properties = arrayOf(
                PlatformBluetoothGattCharacteristic.Property.Read,
                PlatformBluetoothGattCharacteristic.Property.WriteNoResponse,
                PlatformBluetoothGattCharacteristic.Property.Notify,
            ),
            descriptors = arrayOf(
                bluetoothGattDescriptor(
                    uuid = Uuid.parse(
                        Sig.UUID_CLIENT_CHARACTERISTIC_CONFIGURATION
                    ),
                    permissions = arrayOf(PlatformBluetoothGattDescriptor.Permission.PermissionRead,PlatformBluetoothGattDescriptor.Permission.PermissionWrite),
                    value =
                    byteArrayOf(0x00)
                ),
                bluetoothGattDescriptor(
                    uuid = Uuid.parse(
                        Sig.UUID_CHARACTERISTIC_USER_DESCRIPTION
                    ),
                    permissions = arrayOf(PlatformBluetoothGattDescriptor.Permission.PermissionRead),
                    value =
                    "滤芯1额定水量".encodeToByteArray()
                ),
                bluetoothGattDescriptor(
                    uuid = Uuid.parse(
                        His.uuidOf(
                            His.TslCharacteristicDescriptor.Base.uuid16bit,
                            His.BaseService.FilterService
                        )
                    ),
                    permissions = arrayOf(PlatformBluetoothGattDescriptor.Permission.PermissionRead),
                    value = His.TslCharacteristicDescriptor.base(
                        read = true,
                        write = false,
                        required = true,
                        tslCharacteristicDescriptor = His.TslCharacteristicDescriptor.Base,
                        identifier = "filterRatWater1",
                        name = "滤芯1额定水量",
                        desc = "滤芯1额定水量",
                    )
                ),
                bluetoothGattDescriptor(
                    uuid = Uuid.parse(
                        His.uuidOf(
                            His.TslCharacteristicDescriptor.IntProperty.uuid16bit,
                            His.BaseService.FilterService
                        )
                    ),
                    permissions = arrayOf(PlatformBluetoothGattDescriptor.Permission.PermissionRead),
                    value =
                    His.TslCharacteristicDescriptor.intProperty(
                        min = 0,
                        max = 0xffff,
                        unit = His.Unit.CE01
                    )
                ),
            ),
            value = byteArrayOf()
        )

        val filterRatWater2 = bluetoothGattCharacteristic(
            uuid = Uuid.parse(
                His.uuidOf(
                    His.filterService16bit(
                        2,
                        His.FilterServiceCharacteristic.FilterRatWaterN
                    ), His.BaseService.FilterService
                )
            ),
            permissions = arrayOf(
                PlatformBluetoothGattCharacteristic.Permission.Read,
                PlatformBluetoothGattCharacteristic.Permission.Write,
            ),
            properties = arrayOf(
                PlatformBluetoothGattCharacteristic.Property.Read,
                PlatformBluetoothGattCharacteristic.Property.WriteNoResponse,
                PlatformBluetoothGattCharacteristic.Property.Notify,
            ),
            descriptors = arrayOf(
                bluetoothGattDescriptor(
                    uuid = Uuid.parse(
                        Sig.UUID_CLIENT_CHARACTERISTIC_CONFIGURATION
                    ),
                    permissions = arrayOf(PlatformBluetoothGattDescriptor.Permission.PermissionRead,PlatformBluetoothGattDescriptor.Permission.PermissionWrite),
                    value =
                    byteArrayOf(0x00)
                ),
                bluetoothGattDescriptor(
                    uuid = Uuid.parse(
                        Sig.UUID_CHARACTERISTIC_USER_DESCRIPTION
                    ),
                    permissions = arrayOf(PlatformBluetoothGattDescriptor.Permission.PermissionRead),
                    value =
                    "滤芯2额定水量".encodeToByteArray()
                ),
                bluetoothGattDescriptor(
                    uuid = Uuid.parse(
                        His.uuidOf(
                            His.TslCharacteristicDescriptor.Base.uuid16bit,
                            His.BaseService.FilterService
                        )
                    ),
                    permissions = arrayOf(PlatformBluetoothGattDescriptor.Permission.PermissionRead),
                    value = His.TslCharacteristicDescriptor.base(
                        read = true,
                        write = false,
                        required = true,
                        tslCharacteristicDescriptor = His.TslCharacteristicDescriptor.Base,
                        identifier = "filterRatWater2",
                        name = "滤芯2额定水量",
                        desc = "滤芯2额定水量",
                    )
                ),
                bluetoothGattDescriptor(
                    uuid = Uuid.parse(
                        His.uuidOf(
                            His.TslCharacteristicDescriptor.IntProperty.uuid16bit,
                            His.BaseService.FilterService
                        )
                    ),
                    permissions = arrayOf(PlatformBluetoothGattDescriptor.Permission.PermissionRead),
                    value =
                    His.TslCharacteristicDescriptor.intProperty(
                        min = 0,
                        max = 0xffff,
                        unit = His.Unit.CE01
                    )
                ),
            ),
            value = byteArrayOf()
        )

        val filterError = bluetoothGattCharacteristic(
            uuid = Uuid.parse(
                His.uuidOf(
                    His.FilterServiceCharacteristic.FilterError.base,
                    His.BaseService.FilterService
                )
            ),
            permissions = arrayOf(
                PlatformBluetoothGattCharacteristic.Permission.Read,
                PlatformBluetoothGattCharacteristic.Permission.Write,
            ),
            properties = arrayOf(
                PlatformBluetoothGattCharacteristic.Property.Read,
                PlatformBluetoothGattCharacteristic.Property.WriteNoResponse,
                PlatformBluetoothGattCharacteristic.Property.Notify,
            ),
            descriptors = arrayOf(
                bluetoothGattDescriptor(
                    uuid = Uuid.parse(
                        Sig.UUID_CLIENT_CHARACTERISTIC_CONFIGURATION
                    ),
                    permissions = arrayOf(PlatformBluetoothGattDescriptor.Permission.PermissionRead,PlatformBluetoothGattDescriptor.Permission.PermissionWrite),
                    value =
                    byteArrayOf(0x00)
                ),
                bluetoothGattDescriptor(
                    uuid = Uuid.parse(
                        Sig.UUID_CHARACTERISTIC_USER_DESCRIPTION
                    ),
                    permissions = arrayOf(PlatformBluetoothGattDescriptor.Permission.PermissionRead),
                    value =
                    "filterError异常".encodeToByteArray()
                ),
                bluetoothGattDescriptor(
                    uuid = Uuid.parse(
                        His.uuidOf(
                            His.TslCharacteristicDescriptor.Base.uuid16bit,
                            His.BaseService.FilterService
                        )
                    ),
                    permissions = arrayOf(PlatformBluetoothGattDescriptor.Permission.PermissionRead),
                    value = His.TslCharacteristicDescriptor.base(
                        read = true,
                        write = false,
                        required = true,
                        tslCharacteristicDescriptor = His.TslCharacteristicDescriptor.Base,
                        identifier = "filterError",
                        name = "filterError异常",
                        desc = "filterError异常",
                    )
                ),
                bluetoothGattDescriptor(
                    uuid = Uuid.parse(
                        His.uuidOf(
                            His.TslCharacteristicDescriptor.IntProperty.uuid16bit,
                            His.BaseService.FilterService
                        )
                    ),
                    permissions = arrayOf(PlatformBluetoothGattDescriptor.Permission.PermissionRead),
                    value =
                    His.TslCharacteristicDescriptor.intProperty(
                        min = 0,
                        max = 0xffff,
                        unit = His.Unit.CE00
                    )
                ),
            ),
            value = byteArrayOf()
        )

        return arrayOf(
            filterLife1,
            filterLife2,
            filterPercent1,
            filterPercent2,
            filterExpWater1,
            filterExpWater2,
            filterRatWater1,
            filterRatWater2, filterError
        )
    }
}