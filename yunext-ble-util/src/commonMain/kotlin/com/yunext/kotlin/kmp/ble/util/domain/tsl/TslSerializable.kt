package com.yunext.kotlin.kmp.ble.util.domain.tsl

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encodeToString
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.decodeStructure
import kotlinx.serialization.encoding.encodeStructure
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonEncoder
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonObjectBuilder
import kotlinx.serialization.json.booleanOrNull
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.doubleOrNull
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.json.floatOrNull
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put
import kotlinx.serialization.json.putJsonArray
import kotlinx.serialization.json.putJsonObject

interface TslSerializable<T> {
    fun decode(source: T): Tsl
    fun encode(source: Tsl): T

    companion object {
        // 严格模式
        // true：按照sealed-class序列化/反序列化
        // false：按照json字段逐个解析
        internal const val StrictMode = true
    }
}

private const val KEY_TSL_ID = "id"
private const val KEY_TSL_VERSION = "version"
private const val KEY_TSL_PRODUCT_KEY = "productKey"
private const val KEY_TSL_CURRENT = "current"
private const val KEY_TSL_EVENTS = "events"
private const val KEY_TSL_PROPERTIES = "properties"
private const val KEY_TSL_SERVICES = "services"
private const val KEY_TSL_PROPERTY_DATATYPE = "dataType"
private const val KEY_TSL_IDENTIFIER = "identifier"
private const val KEY_TSL_NAME = "name"
private const val KEY_TSL_PROPERTY_ACCESS_MODE = "accessMode"
private const val KEY_TSL_REQUIRED = "required"
private const val KEY_TSL_DESC = "desc"
private const val KEY_TSL_PROPERTY_SPECS = "specs"
private const val KEY_TSL_PROPERTY_SPECS_MIN = "min"
private const val KEY_TSL_PROPERTY_SPECS_MAX = "max"
private const val KEY_TSL_PROPERTY_SPECS_UNIT = "unit"
private const val KEY_TSL_PROPERTY_SPECS_UNIT_NAME = "unitName"
private const val KEY_TSL_PROPERTY_SPECS_STEP = "step"
private const val KEY_TSL_PROPERTY_SPECS_LENGTH = "length"
private const val KEY_TSL_PROPERTY_SPECS_TYPE = "type"
private const val KEY_TSL_PROPERTY_SPECS_ENUM_DESC = "enumDesc"
private const val KEY_TSL_PROPERTY_SPECS_ITEMS = "items"
private const val KEY_TSL_SERVICE_CALL_TYPE = "callType"
private const val KEY_TSL_SERVICE_INPUT_DATA = "inputData"
private const val KEY_TSL_METHOD = "method"
private const val KEY_TSL_OUTPUT_DATA = "outputData"
private const val KEY_TSL_EVENT_TYPE = "type"


fun Tsl.encodeJson(): String = this.encodeJson(jsonTslSerializable)
fun String.decodeJson(): Tsl = this.decodeJson(jsonTslSerializable)

fun <T> Tsl.encodeJson(serializable: TslSerializable<T>): T {
    return serializable.encode(this)
}

fun <T> T.decodeJson(serializable: TslSerializable<T>): Tsl {
    return serializable.decode(this)
}

private val jsonTslSerializable =
    if (TslSerializable.StrictMode) JsonTslSerializableV2() else JsonTslSerializable()

//<editor-fold desc="Json impl">

class JsonTslSerializable : TslSerializable<String> {
    private val json = Json {
        encodeDefaults = true
        ignoreUnknownKeys = true
        prettyPrint = true
    }


    private fun jsonElement2Event(jsonElement: JsonElement): TslEvent? {
        return try {
            val identifier =
                jsonElement.jsonObject[KEY_TSL_IDENTIFIER]?.jsonPrimitive?.contentOrNull
            val name =
                jsonElement.jsonObject[KEY_TSL_NAME]?.jsonPrimitive?.contentOrNull
            val desc =
                jsonElement.jsonObject[KEY_TSL_DESC]?.jsonPrimitive?.contentOrNull
            val required =
                jsonElement.jsonObject[KEY_TSL_REQUIRED]?.jsonPrimitive?.booleanOrNull
            val type =
                jsonElement.jsonObject[KEY_TSL_EVENT_TYPE]?.jsonPrimitive?.contentOrNull

            val method =
                jsonElement.jsonObject[KEY_TSL_METHOD]?.jsonPrimitive?.contentOrNull

            val outputDatas =
                jsonElement.jsonObject[KEY_TSL_OUTPUT_DATA] as? JsonArray

            val outputData =
                outputDatas?.mapNotNull {
                    jsonElement2Property(it)
                } ?: emptyList()
            return TslEvent(
                identifier = identifier ?: "",
                name = name ?: "",
                desc = desc ?: "",
                required = required ?: false,
                type = TslEventType.of(type ?: ""),
                method = method ?: "",
                outputData = outputData
            )
        } catch (e: Exception) {
            println("jsonElement2Event fail : $e")
            null
        }
    }

    private fun jsonElement2Service(jsonElement: JsonElement): TslService? {
        return try {
            val identifier =
                jsonElement.jsonObject[KEY_TSL_IDENTIFIER]?.jsonPrimitive?.contentOrNull
            val name =
                jsonElement.jsonObject[KEY_TSL_NAME]?.jsonPrimitive?.contentOrNull
            val desc =
                jsonElement.jsonObject[KEY_TSL_DESC]?.jsonPrimitive?.contentOrNull
            val required =
                jsonElement.jsonObject[KEY_TSL_REQUIRED]?.jsonPrimitive?.booleanOrNull
            val type =
                jsonElement.jsonObject[KEY_TSL_SERVICE_CALL_TYPE]?.jsonPrimitive?.contentOrNull

            val method =
                jsonElement.jsonObject[KEY_TSL_METHOD]?.jsonPrimitive?.contentOrNull

            val outputDatas =
                jsonElement.jsonObject[KEY_TSL_OUTPUT_DATA] as? JsonArray

            val outputData =
                outputDatas?.mapNotNull {
                    jsonElement2Property(it)
                } ?: emptyList()

            val inputDatas =
                jsonElement.jsonObject[KEY_TSL_SERVICE_INPUT_DATA] as? JsonArray
            val inputData =
                inputDatas?.mapNotNull {
                    jsonElement2Property(it)
                } ?: emptyList()
            return TslService(
                identifier = identifier ?: "",
                name = name ?: "",
                desc = desc ?: "",
                required = required ?: false,
                method = method ?: "",
                outputData = outputData,
                inputData = inputData,
                callType = TslServiceCallType.of(type ?: "")
            )
        } catch (e: Exception) {
            println("jsonElement2Service fail : $e")
            null
        }
    }

    private fun jsonElement2Property(jsonElement: JsonElement): TslProperty? {
        return try {
            val identifier =
                jsonElement.jsonObject[KEY_TSL_IDENTIFIER]?.jsonPrimitive?.contentOrNull
            val dataType =
                jsonElement.jsonObject[KEY_TSL_PROPERTY_DATATYPE]?.jsonPrimitive?.contentOrNull
            val name =
                jsonElement.jsonObject[KEY_TSL_NAME]?.jsonPrimitive?.contentOrNull
            val desc =
                jsonElement.jsonObject[KEY_TSL_DESC]?.jsonPrimitive?.contentOrNull
            val required =
                jsonElement.jsonObject[KEY_TSL_REQUIRED]?.jsonPrimitive?.booleanOrNull
            val accessMode =
                jsonElement.jsonObject[KEY_TSL_PROPERTY_ACCESS_MODE]?.jsonPrimitive?.contentOrNull
            val specs =
                jsonElement.jsonObject[KEY_TSL_PROPERTY_SPECS]
            val propertyType = TslPropertyType.of(dataType ?: "")

            val toIntProp: JsonElement?.() -> TslProperty.IntProperty = {
                TslProperty.IntProperty(
                    identifier = identifier ?: "",
                    name = name ?: "",
                    accessMode = runCatching {
                        TslAccessMode.of(
                            accessMode ?: ""
                        )
                    }.getOrNull() ?: TslAccessMode.R,
                    required = required ?: false,
                    desc = desc ?: "",
                    specs = TslNumberSpec(
                        min = this?.jsonObject?.get(KEY_TSL_PROPERTY_SPECS_MIN)?.jsonPrimitive?.intOrNull
                            ?: 0,
                        max = this?.jsonObject?.get(KEY_TSL_PROPERTY_SPECS_MAX)?.jsonPrimitive?.intOrNull
                            ?: 0,
                        step = this?.jsonObject?.get(KEY_TSL_PROPERTY_SPECS_STEP)?.jsonPrimitive?.intOrNull
                            ?: 0,
                        unit = this?.jsonObject?.get(KEY_TSL_PROPERTY_SPECS_UNIT)?.jsonPrimitive?.contentOrNull
                            ?: "",
                        unitName = this?.jsonObject?.get(KEY_TSL_PROPERTY_SPECS_UNIT_NAME)?.jsonPrimitive?.contentOrNull
                            ?: "",
                    )
                )
            }
            val toFloatProp: JsonElement?.() -> TslProperty.FloatProperty = {
                TslProperty.FloatProperty(
                    identifier = identifier ?: "",
                    name = name ?: "",
                    accessMode = runCatching {
                        TslAccessMode.of(
                            accessMode ?: ""
                        )
                    }.getOrNull() ?: TslAccessMode.R,
                    required = required ?: false,
                    desc = desc ?: "",
                    specs = TslNumberSpec(
                        min = this?.jsonObject?.get(KEY_TSL_PROPERTY_SPECS_MIN)?.jsonPrimitive?.floatOrNull
                            ?: 0f,
                        max = this?.jsonObject?.get(KEY_TSL_PROPERTY_SPECS_MAX)?.jsonPrimitive?.floatOrNull
                            ?: 0f,
                        step = this?.jsonObject?.get(KEY_TSL_PROPERTY_SPECS_STEP)?.jsonPrimitive?.floatOrNull
                            ?: 0f,
                        unit = this?.jsonObject?.get(KEY_TSL_PROPERTY_SPECS_UNIT)?.jsonPrimitive?.contentOrNull
                            ?: "",
                        unitName = this?.jsonObject?.get(KEY_TSL_PROPERTY_SPECS_UNIT_NAME)?.jsonPrimitive?.contentOrNull
                            ?: "",
                    )
                )
            }
            val toDoubleProp: JsonElement?.() -> TslProperty.DoubleProperty = {
                TslProperty.DoubleProperty(
                    identifier = identifier ?: "",
                    name = name ?: "",
                    accessMode = runCatching {
                        TslAccessMode.of(
                            accessMode ?: ""
                        )
                    }.getOrNull() ?: TslAccessMode.R,
                    required = required ?: false,
                    desc = desc ?: "",
                    specs = TslNumberSpec(
                        min = this?.jsonObject?.get(KEY_TSL_PROPERTY_SPECS_MIN)?.jsonPrimitive?.doubleOrNull
                            ?: 0.0,
                        max = this?.jsonObject?.get(KEY_TSL_PROPERTY_SPECS_MAX)?.jsonPrimitive?.doubleOrNull
                            ?: 0.0,
                        step = this?.jsonObject?.get(KEY_TSL_PROPERTY_SPECS_STEP)?.jsonPrimitive?.doubleOrNull
                            ?: 0.0,
                        unit = this?.jsonObject?.get(KEY_TSL_PROPERTY_SPECS_UNIT)?.jsonPrimitive?.contentOrNull
                            ?: "",
                        unitName = this?.jsonObject?.get(KEY_TSL_PROPERTY_SPECS_UNIT_NAME)?.jsonPrimitive?.contentOrNull
                            ?: "",
                    )
                )
            }
            val toTextProp: JsonElement?.() -> TslProperty.TextProperty = {
                TslProperty.TextProperty(
                    identifier = identifier ?: "",
                    name = name ?: "",
                    accessMode = runCatching {
                        TslAccessMode.of(
                            accessMode ?: ""
                        )
                    }.getOrNull() ?: TslAccessMode.R,
                    required = required ?: false,
                    desc = desc ?: "",
                    specs = TslTextSpec(
                        length = this?.jsonObject?.get(KEY_TSL_PROPERTY_SPECS_LENGTH)?.jsonPrimitive?.intOrNull
                            ?: 0,
                    )
                )
            }
            val toDateProp: JsonElement?.() -> TslProperty.DateProperty = {
                TslProperty.DateProperty(
                    identifier = identifier ?: "",
                    name = name ?: "",
                    accessMode = runCatching {
                        TslAccessMode.of(
                            accessMode ?: ""
                        )
                    }.getOrNull() ?: TslAccessMode.R,
                    required = required ?: false,
                    desc = desc ?: "",
                    specs = TslDateSpec(
                        length = this?.jsonObject?.get(KEY_TSL_PROPERTY_SPECS_LENGTH)?.jsonPrimitive?.intOrNull
                            ?: 0,
                    )
                )
            }
            val toBoolProp: JsonElement?.() -> TslProperty.BoolProperty = {
                TslProperty.BoolProperty(
                    identifier = identifier ?: "",
                    name = name ?: "",
                    accessMode = runCatching {
                        TslAccessMode.of(
                            accessMode ?: ""
                        )
                    }.getOrNull() ?: TslAccessMode.R,
                    required = required ?: false,
                    desc = desc ?: "",
                    specs = TslEnumSpec(
                        enumDesc = this?.jsonObject?.get(KEY_TSL_PROPERTY_SPECS_ENUM_DESC)?.jsonObject?.let { ed ->
                            ed.jsonObject.map { (k, v) ->
                                k to (v.jsonPrimitive.contentOrNull ?: "")
                            }.toMap()
                        } ?: emptyMap()
                    )
                )
            }
//            val toEnumBooleanProp: JsonElement?.() -> TslProperty.EnumBooleanProperty = {
//                TslProperty.EnumBooleanProperty(
//                    identifier = identifier ?: "",
//                    name = name ?: "",
//                    accessMode = runCatching {
//                        TslAccessMode.of(
//                            accessMode ?: ""
//                        )
//                    }.getOrNull() ?: TslAccessMode.R,
//                    required = required ?: false,
//                    desc = desc ?: "",
//                    spec = TslEnumIntSpec(
//                        enumDesc = this?.jsonObject?.get(KEY_1_specs_enumDesc)?.jsonObject?.let { ed ->
//                            ed.jsonObject.map { (k, v) ->
//                                k.toInt() to (v.jsonPrimitive.contentOrNull ?: "")
//                            }.toMap()
//                        } ?: emptyMap()
//                    )
//                )
//            }
            val toEnumTextProp: JsonElement?.() -> TslProperty.EnumTextProperty = {
                TslProperty.EnumTextProperty(
                    identifier = identifier ?: "",
                    name = name ?: "",
                    accessMode = runCatching {
                        TslAccessMode.of(
                            accessMode ?: ""
                        )
                    }.getOrNull() ?: TslAccessMode.R,
                    required = required ?: false,
                    desc = desc ?: "",
                    specs = TslEnumSpec(
                        enumDesc = this?.jsonObject?.get(KEY_TSL_PROPERTY_SPECS_ENUM_DESC)?.jsonObject?.let { ed ->
                            ed.jsonObject.map { (k, v) ->
                                k to (v.jsonPrimitive.contentOrNull ?: "")
                            }.toMap()
                        } ?: emptyMap()
                    )
                )
            }
            val toArrayProp: JsonElement?.() -> TslProperty.ArrayProperty = {
                TslProperty.ArrayProperty(
                    identifier = identifier ?: "",
                    name = name ?: "",
                    accessMode = runCatching {
                        TslAccessMode.of(
                            accessMode ?: ""
                        )
                    }.getOrNull() ?: TslAccessMode.R,
                    required = required ?: false,
                    desc = desc ?: "",
                    specs = TslArraySpec(
                        length = this?.jsonObject?.get(KEY_TSL_PROPERTY_SPECS_LENGTH)?.jsonPrimitive?.intOrNull
                            ?: 0,
                        type = this?.jsonObject?.get(KEY_TSL_PROPERTY_SPECS_TYPE)?.toIntProp()
                            ?: throw IllegalArgumentException("TslArraySpec error")
                    )
                )
            }
            val toStructProp: JsonElement?.() -> TslProperty.StructProperty = {
                TslProperty.StructProperty(
                    identifier = identifier ?: "",
                    name = name ?: "",
                    accessMode = runCatching {
                        TslAccessMode.of(
                            accessMode ?: ""
                        )
                    }.getOrNull() ?: TslAccessMode.R,
                    required = required ?: false,
                    desc = desc ?: "",
                    specs = TslStructSpec(
                        items = (this?.jsonObject?.get(KEY_TSL_PROPERTY_SPECS_ITEMS)?.let {
                            it.jsonArray.mapNotNull { p ->
                                jsonElement2Property(p)
                            }
                        }) ?: emptyList()

                    )
                )
            }

            when (propertyType) {
                TslPropertyType.INT -> {
                    specs.toIntProp()
                }

                TslPropertyType.FLOAT -> specs.toFloatProp()
                TslPropertyType.DOUBLE -> specs.toDoubleProp()
                TslPropertyType.TEXT -> specs.toTextProp()
                TslPropertyType.DATE -> specs.toDateProp()
                TslPropertyType.BOOL -> specs.toBoolProp()
                TslPropertyType.ENUM -> specs.toEnumTextProp()
                TslPropertyType.STRUCT -> specs.toStructProp()
                TslPropertyType.ARRAY -> specs.toArrayProp()
            }
        } catch (e: Exception) {
            println("jsonElement2Property fail : $e")
            null
        }
    }


    private fun TslProperty.property2JsonElement(deep: Int = 0): JsonObject {
        fun TslProperty.property2JsonElementInternal(
            builderAction: JsonObjectBuilder.() -> Unit = {}
        ): JsonObject {
            return buildJsonObject {
                this.put(KEY_TSL_IDENTIFIER, (this@property2JsonElementInternal.identifier))
                this.put(KEY_TSL_DESC, (this@property2JsonElementInternal.desc))
                this.put(KEY_TSL_NAME, (this@property2JsonElementInternal.name))
                this.put(KEY_TSL_PROPERTY_DATATYPE, (this@property2JsonElementInternal.dataType.key))
                this.put(KEY_TSL_PROPERTY_ACCESS_MODE, (this@property2JsonElementInternal.accessMode.key))
                this.put(KEY_TSL_REQUIRED, (this@property2JsonElementInternal.required))
                builderAction()
            }
        }

        return this.property2JsonElementInternal() {
            when (val property = this@property2JsonElement) {
                is TslProperty.ArrayProperty -> {
                    this.putJsonObject(KEY_TSL_PROPERTY_SPECS) {
                        this.put(KEY_TSL_PROPERTY_SPECS_LENGTH, (property.specs.length))
                        val innerProperty = property.specs.type
                        this.put(
                            KEY_TSL_PROPERTY_SPECS_TYPE,
                            innerProperty.property2JsonElement()
                        )
                    }
                }

                is TslProperty.BoolProperty -> {
                    this.putJsonObject(KEY_TSL_PROPERTY_SPECS) {
                        this.put(KEY_TSL_PROPERTY_SPECS_ENUM_DESC, buildJsonObject {
                            property.specs.enumDesc.forEach { (k, v) ->
                                this.put(k.toString(), v)
                            }
                        })
                    }
                }

                is TslProperty.DateProperty -> {
                    this.putJsonObject(KEY_TSL_PROPERTY_SPECS) {
                        this.put(KEY_TSL_PROPERTY_SPECS_LENGTH, property.specs.length)
                    }
                }

                is TslProperty.DoubleProperty -> {
                    this.putJsonObject(KEY_TSL_PROPERTY_SPECS) {
                        this.put(KEY_TSL_PROPERTY_SPECS_MAX, (property.specs.max))
                        this.put(KEY_TSL_PROPERTY_SPECS_MIN, (property.specs.min))
                        this.put(KEY_TSL_PROPERTY_SPECS_UNIT, (property.specs.unit))
                        this.put(KEY_TSL_PROPERTY_SPECS_UNIT_NAME, (property.specs.unitName))
                        this.put(KEY_TSL_PROPERTY_SPECS_STEP, (property.specs.step))
                    }
                }

                is TslProperty.EnumTextProperty -> {
                    this.putJsonObject(KEY_TSL_PROPERTY_SPECS) {
                        this.put(KEY_TSL_PROPERTY_SPECS_ENUM_DESC, buildJsonObject {
                            property.specs.enumDesc.forEach { (k, v) ->
                                this.put(k, v)
                            }
                        })
                    }
                }

                is TslProperty.FloatProperty -> {
                    this.putJsonObject(KEY_TSL_PROPERTY_SPECS) {
                        this.put(KEY_TSL_PROPERTY_SPECS_MAX, (property.specs.max))
                        this.put(KEY_TSL_PROPERTY_SPECS_MIN, (property.specs.min))
                        this.put(KEY_TSL_PROPERTY_SPECS_UNIT, (property.specs.unit))
                        this.put(KEY_TSL_PROPERTY_SPECS_UNIT_NAME, (property.specs.unitName))
                        this.put(KEY_TSL_PROPERTY_SPECS_STEP, (property.specs.step))
                    }
                }

                is TslProperty.IntProperty -> {
                    this.putJsonObject(KEY_TSL_PROPERTY_SPECS) {
                        this.put(KEY_TSL_PROPERTY_SPECS_MAX, (property.specs.max))
                        this.put(KEY_TSL_PROPERTY_SPECS_MIN, (property.specs.min))
                        this.put(KEY_TSL_PROPERTY_SPECS_UNIT, (property.specs.unit))
                        this.put(KEY_TSL_PROPERTY_SPECS_UNIT_NAME, (property.specs.unitName))
                        this.put(KEY_TSL_PROPERTY_SPECS_STEP, (property.specs.step))
                    }
                }

                is TslProperty.StructProperty -> {
                    this.putJsonObject(KEY_TSL_PROPERTY_SPECS) {
                        val v = buildJsonArray {
                            property.specs.items.map { innerP ->
                                this.add(innerP.property2JsonElement())

                            }
                        }
                        this.put(KEY_TSL_PROPERTY_SPECS_ITEMS, v)
                    }
                }

                is TslProperty.TextProperty -> {
                    this.putJsonObject(KEY_TSL_PROPERTY_SPECS) {
                        this.put(KEY_TSL_PROPERTY_SPECS_LENGTH, property.specs.length)
                    }
                }
            }
        }

    }

    private fun TslEvent.event2JsonElement(deep: Int = 0): JsonObject {
        fun TslEvent.event2JsonElementInternal(
            builderAction: JsonObjectBuilder.() -> Unit = {}
        ): JsonObject {
            return buildJsonObject {
                this.put(KEY_TSL_IDENTIFIER, (this@event2JsonElementInternal.identifier))
                this.put(KEY_TSL_DESC, (this@event2JsonElementInternal.desc))
                this.put(KEY_TSL_NAME, (this@event2JsonElementInternal.name))
                this.put(KEY_TSL_REQUIRED, (this@event2JsonElementInternal.required))
                this.put(KEY_TSL_EVENT_TYPE, (this@event2JsonElementInternal.type.key))
                this.put(KEY_TSL_METHOD, (this@event2JsonElementInternal.method))
                builderAction()
            }
        }

        return this.event2JsonElementInternal() {
            this.putJsonArray(KEY_TSL_OUTPUT_DATA) {
                this@event2JsonElement.outputData.forEach {
                    add(it.property2JsonElement())
                }
            }
        }

    }

    private fun TslService.service2JsonElement(deep: Int = 0): JsonObject {
        fun TslService.service2JsonElementInternal(
            builderAction: JsonObjectBuilder.() -> Unit = {}
        ): JsonObject {
            return buildJsonObject {
                this.put(KEY_TSL_IDENTIFIER, (this@service2JsonElementInternal.identifier))
                this.put(KEY_TSL_DESC, (this@service2JsonElementInternal.desc))
                this.put(KEY_TSL_NAME, (this@service2JsonElementInternal.name))
                this.put(KEY_TSL_REQUIRED, (this@service2JsonElementInternal.required))
                this.put(KEY_TSL_SERVICE_CALL_TYPE, (this@service2JsonElementInternal.callType.key))
                this.put(KEY_TSL_METHOD, (this@service2JsonElementInternal.method))
                builderAction()
            }
        }

        return this.service2JsonElementInternal() {
            this.put(KEY_TSL_OUTPUT_DATA, buildJsonArray {
                this@service2JsonElement.outputData.forEach {
                    add(it.property2JsonElement())
                }
            })

            this.put(KEY_TSL_SERVICE_INPUT_DATA, buildJsonArray {
                this@service2JsonElement.inputData.forEach {
                    add(it.property2JsonElement())
                }
            })

        }

    }

    override fun decode(source: String): Tsl {
        val jsonElement: JsonElement = Json.parseToJsonElement(source)
        var id: String? = null
        var version: String? = null
        var productKey: String? = null
        var current: Boolean? = false
        var events: List<TslEvent>? = null
        var properties: List<TslProperty>? = null
        var services: List<TslService>? = null

        (jsonElement as JsonObject).forEach { (k, v) ->
            when (k) {
                KEY_TSL_ID -> id = v.jsonPrimitive.contentOrNull
                KEY_TSL_VERSION -> version = v.jsonPrimitive.contentOrNull
                KEY_TSL_PRODUCT_KEY -> productKey = v.jsonPrimitive.contentOrNull
                KEY_TSL_CURRENT -> current = v.jsonPrimitive.booleanOrNull
                KEY_TSL_EVENTS -> {
                    events = v.jsonArray.mapNotNull { eventElement ->
                        jsonElement2Event(eventElement)
                    }
                }

                KEY_TSL_SERVICES -> {
                    services = v.jsonArray.mapNotNull { eventElement ->
                        jsonElement2Service(eventElement)
                    }
                }

                KEY_TSL_PROPERTIES -> {
                    properties = v.jsonArray.mapNotNull { propertyElement ->
                        jsonElement2Property(propertyElement)
                    }
                }
            }
        }

        return Tsl(
            id = id ?: "",
            version = version ?: "",
            productKey = productKey ?: "",
            current = current ?: false,
            properties = properties ?: emptyList(),
            events = events ?: emptyList(),
            services = services ?: emptyList()
        )
    }

    @OptIn(ExperimentalSerializationApi::class)
    override fun encode(source: Tsl): String {

        val result = buildJsonObject {
            put(KEY_TSL_ID, (source.id))
            put(KEY_TSL_VERSION, (source.version))
            put(KEY_TSL_PRODUCT_KEY, (source.productKey))
            put(KEY_TSL_CURRENT, (source.current))
            put(KEY_TSL_PROPERTIES, buildJsonArray {
                (source.properties.forEach {
                    add(it.property2JsonElement())
//                    add(property2JsonElement(it))
                })

            })
            put(KEY_TSL_EVENTS, buildJsonArray {
                source.events.forEach {
                    add(it.event2JsonElement())
                }
            })
            put(KEY_TSL_SERVICES, buildJsonArray {
                source.services.forEach {
                    add(it.service2JsonElement())
                }
            })

        }
        return json.encodeToString(result)
    }

}
//</editor-fold>

//<editor-fold desc="Json impl v2">

class JsonTslSerializableV2 : TslSerializable<String> {
    private val json = Json {
        encodeDefaults = true
        ignoreUnknownKeys = true
        prettyPrint = true
    }

    override fun decode(source: String): Tsl {
        return json.decodeFromString(source)
    }

    override fun encode(source: Tsl): String {
        return json.encodeToString(source)
    }

}

object TslPropertySerializer : KSerializer<TslProperty> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("com.yunext.kotlin.kmp.ble.util.domain.tsl.TslProperty", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): TslProperty {
        val jsonInput =
            decoder as? JsonDecoder ?: throw SerializationException("Expected JsonDecoder")
        val jsonElement = jsonInput.decodeJsonElement()
        val jsonObject = jsonElement.jsonObject
        val jsonInner = jsonInput.json
        println("====deserialize===>")

        // 根据 dataType 字段判断具体类型
        val dataType = jsonObject[KEY_TSL_PROPERTY_DATATYPE]?.jsonPrimitive?.content
        return when (TslPropertyType.of(dataType?:"")) {
            TslPropertyType.INT -> jsonInner.decodeFromJsonElement(
                TslProperty.IntProperty.serializer(),
                jsonElement
            )

            TslPropertyType.FLOAT -> jsonInner.decodeFromJsonElement(
                TslProperty.FloatProperty.serializer(),
                jsonElement
            )

            TslPropertyType.DOUBLE -> jsonInner.decodeFromJsonElement(
                TslProperty.DoubleProperty.serializer(),
                jsonElement
            )

            TslPropertyType.TEXT -> jsonInner.decodeFromJsonElement(
                TslProperty.TextProperty.serializer(),
                jsonElement
            )

            TslPropertyType.DATE-> jsonInner.decodeFromJsonElement(
                TslProperty.DateProperty.serializer(),
                jsonElement
            )

            TslPropertyType.BOOL -> jsonInner.decodeFromJsonElement(
                TslProperty.BoolProperty.serializer(),
                jsonElement
            )

            TslPropertyType.ENUM -> jsonInner.decodeFromJsonElement(
                TslProperty.EnumTextProperty.serializer(),
                jsonElement
            )

            TslPropertyType.ARRAY -> jsonInner.decodeFromJsonElement(
                TslProperty.ArrayProperty.serializer(),
                jsonElement
            )

            TslPropertyType.STRUCT -> jsonInner.decodeFromJsonElement(
                TslProperty.StructProperty.serializer(),
                jsonElement
            )

            else -> throw SerializationException("Unknown dataType: $dataType")
        }
    }

    override fun serialize(encoder: Encoder, value: TslProperty) {
        // 反序列化时，根据 JSON 数据中的字段判断具体类型
        val jsonInput =
            encoder as? JsonEncoder ?: throw SerializationException("Expected JsonEncoder")
        // 将 Property 转换为 JsonObject
        val jsonObject = buildJsonObject {
            put(KEY_TSL_IDENTIFIER, value.identifier)
            put(KEY_TSL_DESC, value.desc)
            put(KEY_TSL_NAME, value.name)
            put(KEY_TSL_PROPERTY_DATATYPE, value.dataType.key)
            put(KEY_TSL_PROPERTY_ACCESS_MODE, value.accessMode.key) // 枚举值转换为字符串
            put(KEY_TSL_REQUIRED, value.required)
            put(KEY_TSL_PROPERTY_SPECS, jsonInput.json.encodeToJsonElement(value.specs))
        }
        // 将 JsonObject 写入编码器
        jsonInput.encodeJsonElement(jsonObject)
    }

}

object SpecPropertySerializer : KSerializer<TslSpecDef> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("com.yunext.kotlin.kmp.ble.util.domain.tsl.TslSpecDef", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): TslSpecDef {
        // 反序列化时，根据 JSON 数据中的字段判断具体类型
        val jsonInput =
            decoder as? JsonDecoder ?: throw SerializationException("Expected JsonDecoder")
        val jsonInner = jsonInput.json
        val jsonElement = jsonInput.decodeJsonElement()
        val jsonObject = jsonElement.jsonObject

        // 根据 dataType 或其他字段判断具体类型
        return when {
            jsonObject.containsKey(KEY_TSL_PROPERTY_SPECS_MAX) && jsonObject.containsKey(KEY_TSL_PROPERTY_SPECS_MIN) && jsonObject.containsKey(
                KEY_TSL_PROPERTY_SPECS_STEP
            ) -> {
                // 判断是否为 IntSpecs 或 FloatSpecs
                if (jsonObject[KEY_TSL_PROPERTY_SPECS_MAX]?.jsonPrimitive?.isString == false && jsonObject[KEY_TSL_PROPERTY_SPECS_MAX]?.jsonPrimitive?.intOrNull != null) {
                    jsonInner.decodeFromJsonElement(
                        TslNumberSpecSerializer(Int.serializer()),
                        jsonElement
                    )
                } else if (jsonObject[KEY_TSL_PROPERTY_SPECS_MAX]?.jsonPrimitive?.isString == false && jsonObject[KEY_TSL_PROPERTY_SPECS_MAX]?.jsonPrimitive?.floatOrNull != null) {
                    jsonInner.decodeFromJsonElement(
                        TslNumberSpecSerializer(Float.serializer()),
                        jsonElement
                    )
                } else if (jsonObject[KEY_TSL_PROPERTY_SPECS_MAX]?.jsonPrimitive?.isString == false && jsonObject[KEY_TSL_PROPERTY_SPECS_MAX]?.jsonPrimitive?.doubleOrNull != null) {
                    jsonInner.decodeFromJsonElement(
                        TslNumberSpecSerializer(Double.serializer()),
                        jsonElement
                    )
                } else throw IllegalArgumentException("TslNumberSpecSerializer error")
            }

            jsonObject.containsKey(KEY_TSL_PROPERTY_SPECS_LENGTH) && !jsonObject.containsKey(KEY_TSL_PROPERTY_SPECS_TYPE) -> {
                jsonInner.decodeFromJsonElement(TslArraySpec.serializer(), jsonElement)
            }

            jsonObject.containsKey(KEY_TSL_PROPERTY_SPECS_LENGTH) -> {
                jsonInner.decodeFromJsonElement(TslTextSpec.serializer(), jsonElement)
            }

            jsonObject.containsKey(KEY_TSL_PROPERTY_SPECS_ENUM_DESC) -> {
                jsonInner.decodeFromJsonElement(TslEnumSpec.serializer(), jsonElement)
            }

            jsonObject.containsKey(KEY_TSL_PROPERTY_SPECS_ITEMS) -> {
                jsonInner.decodeFromJsonElement(TslStructSpec.serializer(), jsonElement)
            }

            else -> throw SerializationException("Unknown Specs type: $jsonObject")
        }
    }

    override fun serialize(encoder: Encoder, value: TslSpecDef) {
        // 序列化时，根据具体类型调用对应的序列化器
        when (value) {
            is TslArraySpec -> encoder.encodeSerializableValue(TslArraySpec.serializer(), value)
            is TslEnumSpec -> encoder.encodeSerializableValue(
                TslEnumSpec.serializer(),
                value
            )

            is TslNumberSpec<*> -> {
                when {
                    value.isTslNumberSpecOfInt() -> encoder.encodeSerializableValue(
                        TslNumberSpecSerializer(Int.serializer()),
                        value as TslNumberSpec<Int>
                    )

                    value.isTslNumberSpecOfFloat() -> encoder.encodeSerializableValue(
                        TslNumberSpecSerializer(Float.serializer()),
                        value as TslNumberSpec<Float>
                    )

                    value.isTslNumberSpecOfDouble() -> encoder.encodeSerializableValue(
                        TslNumberSpecSerializer(Double.serializer()),
                        value as TslNumberSpec<Double>
                    )
                }
            }

            is TslStructSpec -> encoder.encodeSerializableValue(TslStructSpec.serializer(), value)
            is TslTextSpec -> encoder.encodeSerializableValue(TslTextSpec.serializer(), value)
        }
    }

}

private fun TslNumberSpec<*>.isTslNumberSpecOfInt(): Boolean {
    return this.min is Int && this.max is Int && this.step is Int
}

private fun TslNumberSpec<*>.isTslNumberSpecOfFloat(): Boolean {
    return this.min is Float && this.max is Float && this.step is Float
}

private fun TslNumberSpec<*>.isTslNumberSpecOfDouble(): Boolean {
    return this.min is Double && this.max is Double && this.step is Double
}

class TslNumberSpecSerializer<T : Number>(private val dataSerializer: KSerializer<T>) :
    KSerializer<TslNumberSpec<T>> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("com.yunext.kotlin.kmp.ble.util.domain.tsl.TslNumberSpec") {
        element(KEY_TSL_PROPERTY_SPECS_MIN, dataSerializer.descriptor)
        element(KEY_TSL_PROPERTY_SPECS_MAX, dataSerializer.descriptor)
        element(
            KEY_TSL_PROPERTY_SPECS_UNIT,
            PrimitiveSerialDescriptor("${dataSerializer.toString()}-${KEY_TSL_PROPERTY_SPECS_UNIT}", PrimitiveKind.STRING)
        )
        element(
            KEY_TSL_PROPERTY_SPECS_UNIT_NAME,
            PrimitiveSerialDescriptor("${dataSerializer.toString()}-${KEY_TSL_PROPERTY_SPECS_UNIT_NAME}", PrimitiveKind.STRING)
        )
        element(KEY_TSL_PROPERTY_SPECS_STEP, dataSerializer.descriptor)
    }

    override fun serialize(encoder: Encoder, value: TslNumberSpec<T>) {
        encoder.encodeStructure(descriptor) {
            encodeSerializableElement(descriptor, 0, dataSerializer, value.min)
            encodeSerializableElement(descriptor, 1, dataSerializer, value.max)
            encodeStringElement(descriptor, 2, value.unit)
            encodeStringElement(descriptor, 3, value.unitName)
            encodeSerializableElement(descriptor, 4, dataSerializer, value.step)
        }
    }

    override fun deserialize(decoder: Decoder): TslNumberSpec<T> {
        return decoder.decodeStructure(descriptor) {
            var min: T? = null
            var max: T? = null
            var unit: String = ""
            var unitName: String = ""
            var step: T? = null

            while (true) {
                when (val index = decodeElementIndex(descriptor)) {
                    0 -> min = decodeSerializableElement(descriptor, 0, dataSerializer)
                    1 -> max = decodeSerializableElement(descriptor, 1, dataSerializer)
                    2 -> unit = decodeStringElement(descriptor, 2)
                    3 -> unitName = decodeStringElement(descriptor, 3)
                    4 -> step = decodeSerializableElement(descriptor, 4, dataSerializer)
                    CompositeDecoder.DECODE_DONE -> break
                    else -> throw SerializationException("Unknown index: $index")
                }
            }

            requireNotNull(min) { "min is required" }
            requireNotNull(max) { "max is required" }
            requireNotNull(step) { "step is required" }

            TslNumberSpec(min, max, unit, unitName, step)
        }
    }

    companion object {
        fun decodeTslNumberSpec(json: Json, jsonElement: JsonElement): TslNumberSpec<*> {
            val jsonObject = jsonElement.jsonObject

            // 判断 min 和 max 的类型
            val minValue = jsonObject[KEY_TSL_PROPERTY_SPECS_MIN]?.jsonPrimitive?.content
            val maxValue = jsonObject[KEY_TSL_PROPERTY_SPECS_MAX]?.jsonPrimitive?.content
            val stepValue = jsonObject[KEY_TSL_PROPERTY_SPECS_STEP]?.jsonPrimitive?.content

            return when {
                minValue?.toIntOrNull() != null && maxValue?.toIntOrNull() != null && stepValue?.toIntOrNull() != null -> {
                    json.decodeFromJsonElement(
                        TslNumberSpecSerializer(Int.serializer()),
                        jsonElement
                    )
                }

                minValue?.toFloatOrNull() != null && maxValue?.toFloatOrNull() != null && stepValue?.toFloatOrNull() != null -> {
                    json.decodeFromJsonElement(
                        TslNumberSpecSerializer(Float.serializer()),
                        jsonElement
                    )
                }

                minValue?.toDoubleOrNull() != null && maxValue?.toDoubleOrNull() != null && stepValue?.toDoubleOrNull() != null -> {
                    json.decodeFromJsonElement(
                        TslNumberSpecSerializer(Double.serializer()),
                        jsonElement
                    )
                }

                else -> throw SerializationException("Unknown number type in TslNumberSpec")
            }
        }
    }
}

object TslAccessModeSerializer : KSerializer<TslAccessMode> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("com.yunext.kotlin.kmp.ble.util.domain.tsl.TslAccessMode", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): TslAccessMode {
        return TslAccessMode.of(decoder.decodeString())
    }

    override fun serialize(encoder: Encoder, value: TslAccessMode) {
        encoder.encodeString(value.key)
    }
}

object TslPropertyTypeSerializer : KSerializer<TslPropertyType> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("com.yunext.kotlin.kmp.ble.util.domain.tsl.TslPropertyType", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): TslPropertyType {
        return TslPropertyType.of(decoder.decodeString())
    }

    override fun serialize(encoder: Encoder, value: TslPropertyType) {
        encoder.encodeString(value.key)
    }
}

object TslServiceCallTypeSerializer : KSerializer<TslServiceCallType> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("com.yunext.kotlin.kmp.ble.util.domain.tsl.TslServiceCallType", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): TslServiceCallType {
        return TslServiceCallType.of(decoder.decodeString())
    }

    override fun serialize(encoder: Encoder, value: TslServiceCallType) {
        encoder.encodeString(value.key)
    }
}

object TslEventTypeSerializer : KSerializer<TslEventType> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("com.yunext.kotlin.kmp.ble.util.domain.tsl.TslEventType", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): TslEventType {
        return TslEventType.of(decoder.decodeString())
    }

    override fun serialize(encoder: Encoder, value: TslEventType) {
        encoder.encodeString(value.key)
    }
}


//</editor-fold>

