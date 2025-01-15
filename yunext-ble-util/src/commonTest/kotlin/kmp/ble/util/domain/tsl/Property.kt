package kmp.ble.util.domain.tsl

import kotlinx.serialization.*
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.*
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass

// 使用自定义的 Json 配置
val jsonInner = Json {
    prettyPrint = true
//    serializersModule = module
//    classDiscriminator = "type"
}

@Serializable(with = Property.Companion::class)
//@Polymorphic
// 密封类表示不同的属性类型
sealed class Property {
    abstract val identifier: String
    abstract val desc: String
    abstract val name: String
    abstract val dataType: String
    abstract val accessMode: AccessMode
    abstract val required: Boolean
    abstract val specs: Specs

    companion object : KSerializer<Property> {
        override val descriptor: SerialDescriptor =
            PrimitiveSerialDescriptor("Property", PrimitiveKind.STRING)

        override fun serialize(encoder: Encoder, value: Property) {
            // 反序列化时，根据 JSON 数据中的字段判断具体类型
            val jsonInput =
                encoder as? JsonEncoder ?: throw SerializationException("Expected JsonEncoder")
            // 将 Property 转换为 JsonObject
            val jsonObject = buildJsonObject {
                put("identifier", value.identifier)
                put("desc", value.desc)
                put("name", value.name)
                put("dataType", value.dataType)
                put("accessMode", value.accessMode.toJsonString()) // 枚举值转换为字符串
                put("required", value.required)
                put("specs", jsonInner.encodeToJsonElement(value.specs))
            }
            // 将 JsonObject 写入编码器
            jsonInput.encodeJsonElement(jsonObject)

//            // 序列化时，根据具体类型调用对应的序列化器
//            when (value) {
//                is IntProperty -> encoder.encodeSerializableValue(IntProperty.serializer(), value)
//                is FloatProperty -> encoder.encodeSerializableValue(
//                    FloatProperty.serializer(),
//                    value
//                )
//
//                is TextProperty -> encoder.encodeSerializableValue(TextProperty.serializer(), value)
//                is BooleanProperty -> encoder.encodeSerializableValue(
//                    BooleanProperty.serializer(),
//                    value
//                )
//
//                is EnumProperty -> encoder.encodeSerializableValue(EnumProperty.serializer(), value)
//                is ArrayProperty -> encoder.encodeSerializableValue(
//                    ArrayProperty.serializer(),
//                    value
//                )
//
//                is StructProperty -> encoder.encodeSerializableValue(
//                    StructProperty.serializer(),
//                    value
//                )
//            }
        }

        override fun deserialize(decoder: Decoder): Property {
            val jsonInput =
                decoder as? JsonDecoder ?: throw SerializationException("Expected JsonDecoder")
            val jsonElement = jsonInput.decodeJsonElement()
            val jsonObject = jsonElement.jsonObject

            // 将 accessMode 字符串转换为枚举类型
            val accessMode = AccessMode.fromJsonString(
                jsonObject["accessMode"]?.jsonPrimitive?.content
                    ?: throw SerializationException("Missing accessMode")
            )

            // 根据 dataType 字段判断具体类型
            return when (val dataType = jsonObject["dataType"]?.jsonPrimitive?.content) {
                "int" -> jsonInner.decodeFromJsonElement(IntProperty.serializer(), jsonElement)
                "float" -> jsonInner.decodeFromJsonElement(FloatProperty.serializer(), jsonElement)
                "text" -> jsonInner.decodeFromJsonElement(TextProperty.serializer(), jsonElement)
                "bool" -> jsonInner.decodeFromJsonElement(BooleanProperty.serializer(), jsonElement)
                "enum" -> jsonInner.decodeFromJsonElement(EnumProperty.serializer(), jsonElement)
                "array" -> jsonInner.decodeFromJsonElement(ArrayProperty.serializer(), jsonElement)
                "struct" -> jsonInner.decodeFromJsonElement(
                    StructProperty.serializer(),
                    jsonElement
                )

                else -> throw SerializationException("Unknown dataType: $dataType")
            }
//            // 反序列化时，根据 JSON 数据中的字段判断具体类型
//            val jsonInput =
//                decoder as? JsonDecoder ?: throw SerializationException("Expected JsonDecoder")
//            val jsonElement = jsonInput.decodeJsonElement()
//
//            // 将 JSON 数据解析为 JsonObject
//            val jsonObject = jsonElement.jsonObject
//
//            // 根据 dataType 字段判断具体类型
//            return when (val dataType = jsonObject["dataType"]?.jsonPrimitive?.content) {
//                "int" -> jsonInner.decodeFromJsonElement(IntProperty.serializer(), jsonElement)
//                "float" -> jsonInner.decodeFromJsonElement(FloatProperty.serializer(), jsonElement)
//                "text" -> jsonInner.decodeFromJsonElement(TextProperty.serializer(), jsonElement)
//                "bool" -> jsonInner.decodeFromJsonElement(BooleanProperty.serializer(), jsonElement)
//                "enum" -> jsonInner.decodeFromJsonElement(EnumProperty.serializer(), jsonElement)
//                "array" -> jsonInner.decodeFromJsonElement(ArrayProperty.serializer(), jsonElement)
//                "struct" -> jsonInner.decodeFromJsonElement(
//                    StructProperty.serializer(),
//                    jsonElement
//                )
//
//                else -> throw SerializationException("Unknown dataType: $dataType")
//            }


        }
    }
}

// 密封类表示不同的规格
@Serializable(with = Specs.Companion::class)
//@Polymorphic
sealed class Specs {

    companion object : KSerializer<Specs> {
        override val descriptor: SerialDescriptor =
            PrimitiveSerialDescriptor("Specs", PrimitiveKind.STRING)

        override fun serialize(encoder: Encoder, value: Specs) {
            // 序列化时，根据具体类型调用对应的序列化器
            when (value) {
                is IntSpecs -> encoder.encodeSerializableValue(IntSpecs.serializer(), value)
                is FloatSpecs -> encoder.encodeSerializableValue(FloatSpecs.serializer(), value)
                is TextSpecs -> encoder.encodeSerializableValue(TextSpecs.serializer(), value)
                is BooleanSpecs -> encoder.encodeSerializableValue(BooleanSpecs.serializer(), value)
                is EnumSpecs -> encoder.encodeSerializableValue(EnumSpecs.serializer(), value)
                is ArraySpecs -> encoder.encodeSerializableValue(ArraySpecs.serializer(), value)
                is StructSpecs -> encoder.encodeSerializableValue(StructSpecs.serializer(), value)
            }
        }

        override fun deserialize(decoder: Decoder): Specs {
            // 反序列化时，根据 JSON 数据中的字段判断具体类型
            val jsonInput =
                decoder as? JsonDecoder ?: throw SerializationException("Expected JsonDecoder")
            val jsonElement = jsonInput.decodeJsonElement()
            val jsonObject = jsonElement.jsonObject

            // 根据 dataType 或其他字段判断具体类型
            return when {
                jsonObject.containsKey("max") && jsonObject.containsKey("min") && jsonObject.containsKey(
                    "step"
                ) -> {
                    // 判断是否为 IntSpecs 或 FloatSpecs
                    if (jsonObject["max"]?.jsonPrimitive?.isString == false && jsonObject["max"]?.jsonPrimitive?.intOrNull != null) {
                        jsonInner.decodeFromJsonElement(IntSpecs.serializer(), jsonElement)
                    } else {
                        jsonInner.decodeFromJsonElement(FloatSpecs.serializer(), jsonElement)
                    }
                }

                jsonObject.containsKey("length") && !jsonObject.containsKey("type") -> {
                    jsonInner.decodeFromJsonElement(TextSpecs.serializer(), jsonElement)
                }

                jsonObject.containsKey("enumDesc") && jsonObject.containsKey("length") -> {
                    jsonInner.decodeFromJsonElement(ArraySpecs.serializer(), jsonElement)
                }

                jsonObject.containsKey("enumDesc") -> {
                    jsonInner.decodeFromJsonElement(EnumSpecs.serializer(), jsonElement)
                }

                jsonObject.containsKey("items") -> {
                    jsonInner.decodeFromJsonElement(StructSpecs.serializer(), jsonElement)
                }

                else -> throw SerializationException("Unknown Specs type: $jsonObject")
            }
        }
    }

}

// Int 类型属性
@Serializable
//@Polymorphic
data class IntProperty(
    override val identifier: String,
    override val desc: String,
    override val name: String,
    override val dataType: String,
    override val accessMode: AccessMode,
    override val required: Boolean,
    override val specs: IntSpecs
) : Property()

// Int 类型的规格
@Serializable
//@Polymorphic
data class IntSpecs(
    val max: Int,
    val min: Int,
    val unit: String,
    val unitName: String,
    val step: Int
) : Specs()

// Float 类型属性
@Serializable
//@Polymorphic
data class FloatProperty(
    override val identifier: String,
    override val desc: String,
    override val name: String,
    override val dataType: String,
    override val accessMode: AccessMode,
    override val required: Boolean,
    override val specs: FloatSpecs
) : Property()

// Float 类型的规格
@Serializable
//@Polymorphic
data class FloatSpecs(
    val max: Float,
    val min: Float,
    val unit: String,
    val unitName: String,
    val step: Float
) : Specs()

// Text 类型属性
//@Polymorphic
@Serializable
data class TextProperty(
    override val identifier: String,
    override val desc: String,
    override val name: String,
    override val dataType: String,
    override val accessMode: AccessMode,
    override val required: Boolean,
    override val specs: TextSpecs
) : Property()

// Text 类型的规格
//@Polymorphic
@Serializable
data class TextSpecs(
    val length: Int
) : Specs()

// Boolean 类型属性
@Serializable
//@Polymorphic
data class BooleanProperty(
    override val identifier: String,
    override val desc: String,
    override val name: String,
    override val dataType: String,
    override val accessMode: AccessMode,
    override val required: Boolean,
    override val specs: BooleanSpecs
) : Property()

// Boolean 类型的规格
@Serializable
//@Polymorphic
data class BooleanSpecs(
    val enumDesc: Map<String, String>
) : Specs()

// Enum 类型属性
@Serializable
//@Polymorphic
data class EnumProperty(
    override val identifier: String,
    override val desc: String,
    override val name: String,
    override val dataType: String,
    override val accessMode: AccessMode,
    override val required: Boolean,
    override val specs: EnumSpecs
) : Property()

// Enum 类型的规格
@Serializable
//@Polymorphic
data class EnumSpecs(
    val enumDesc: Map<String, String>
) : Specs()

// Array 类型属性
@Serializable
//@Polymorphic
data class ArrayProperty(
    override val identifier: String,
    override val desc: String,
    override val name: String,
    override val dataType: String,
    override val accessMode: AccessMode,
    override val required: Boolean,
    override val specs: ArraySpecs
) : Property()

// Array 类型的规格
@Serializable
//@Polymorphic
data class ArraySpecs(
    val length: Int,
    val type: Property
) : Specs()

// Struct 类型属性
@Serializable
//@Polymorphic
data class StructProperty(
    override val identifier: String,
    override val desc: String,
    override val name: String,
    override val dataType: String,
    override val accessMode: AccessMode,
    override val required: Boolean,
    override val specs: StructSpecs
) : Property()

// Struct 类型的规格
@Serializable
//@Polymorphic
data class StructSpecs(

    val items: List<Property>
) : Specs()

// Tsl 类
@Serializable
data class TslV2(
    val id: String,
    val version: String,
    val productKey: String,
    val current: Boolean,
    val properties: List<Property>,
    val events: List<String>, // 假设 events 是字符串列表
    val services: List<String> // 假设 services 是字符串列表
)


fun main() {
    val jsonString = """
        {
          "id": "tsl.id",
          "version": "productKey.version",
          "productKey": "tsl.productKey",
          "current": false,
          "properties": [
            {
              "identifier": "int.identifier",
              "desc": "int.desc",
              "name": "int.name",
              "dataType": "int",
              "accessMode": "r",
              "required": false,
              "specs": {
                "max": 100,
                "min": 0,
                "unit": "int.unit",
                "unitName": "int.unitName",
                "step": 1
              }
            },
            {
              "identifier": "float.identifier",
              "desc": "float.desc",
              "name": "float.name",
              "dataType": "float",
              "accessMode": "r",
              "required": false,
              "specs": {
                "max": 100.0,
                "min": 0.0,
                "unit": "int.unit",
                "unitName": "int.unitName",
                "step": 1.0
              }
            },
            {
              "identifier": "text.identifier",
              "desc": "text.desc",
              "name": "text.name",
              "dataType": "text",
              "accessMode": "r",
              "required": false,
              "specs": {
                "length": 10240
              }
            },
            {
              "identifier": "boolean.identifier",
              "desc": "boolean.desc",
              "name": "boolean.name",
              "dataType": "bool",
              "accessMode": "r",
              "required": false,
              "specs": {
                "enumDesc": {
                  "1": "boolean.true",
                  "0": "boolean.false"
                }
              }
            },
            {
              "identifier": "textEnum.identifier",
              "desc": "textEnum.desc",
              "name": "textEnum.name",
              "dataType": "enum",
              "accessMode": "r",
              "required": false,
              "specs": {
                "enumDesc": {
                  "dong": "*东*",
                  "xi": "*西*",
                  "nan": "*南*",
                  "bei": "*北*"
                }
              }
            },
            {
              "identifier": "array.identifier",
              "desc": "array.desc",
              "name": "array.name",
              "dataType": "array",
              "accessMode": "r",
              "required": false,
              "specs": {
                "length": 10,
                "type": {
                  "identifier": "array.int.identifier",
                  "desc": "array.int.desc",
                  "name": "array.int.name",
                  "dataType": "int",
                  "accessMode": "r",
                  "required": false,
                  "specs": {
                    "max": 100,
                    "min": 0,
                    "unit": "int.unit",
                    "unitName": "int.unitName",
                    "step": 1
                  }
                }
              }
            },
            {
              "identifier": "struct.identifier",
              "desc": "struct.desc",
              "name": "struct.name",
              "dataType": "struct",
              "accessMode": "r",
              "required": false,
              "specs": {
                "items": [
                  {
                    "identifier": "struct.int.identifier",
                    "desc": "struct.int.desc",
                    "name": "struct.int.name",
                    "dataType": "int",
                    "accessMode": "r",
                    "required": false,
                    "specs": {
                      "max": 100,
                      "min": 0,
                      "unit": "int.unit",
                      "unitName": "int.unitName",
                      "step": 1
                    }
                  },
                  {
                    "identifier": "struct.textEnum.identifier",
                    "desc": "struct.textEnum.desc",
                    "name": "struct.textEnum.name",
                    "dataType": "enum",
                    "accessMode": "r",
                    "required": false,
                    "specs": {
                      "enumDesc": {
                        "dong": "*东*",
                        "xi": "*西*",
                        "nan": "*南*",
                        "bei": "*北*"
                      }
                    }
                  }
                ]
              }
            }
          ],
          "events": [],
          "services": []
        }
    """.trimIndent()
    val tsl = jsonInner.decodeFromString<TslV2>(jsonString)
    println(tsl)
}

@Serializable(with = AccessModeSerializer::class)
enum class AccessMode {
    READ,       // 对应 "r"
    WRITE,      // 对应 "w"
    READ_WRITE; // 对应 "rw"

    // 将枚举值转换为 JSON 中的字符串
    fun toJsonString(): String {
        return when (this) {
            READ -> "r"
            WRITE -> "w"
            READ_WRITE -> "rw"
        }
    }

    companion object {
        // 将 JSON 中的字符串转换为枚举值
        fun fromJsonString(value: String): AccessMode {
            return when (value) {
                "r" -> READ
                "w" -> WRITE
                "rw" -> READ_WRITE
                else -> throw IllegalArgumentException("Unknown accessMode: $value")
            }
        }
    }
}

object AccessModeSerializer : KSerializer<AccessMode> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("AccessMode", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: AccessMode) {
        // 将枚举值转换为 JSON 字符串
        val jsonValue = when (value) {
            AccessMode.READ -> "r"
            AccessMode.WRITE -> "w"
            AccessMode.READ_WRITE -> "rw"
        }
        encoder.encodeString(jsonValue)
    }

    override fun deserialize(decoder: Decoder): AccessMode {
        // 将 JSON 字符串转换为枚举值
        val jsonValue = decoder.decodeString()
        return when (jsonValue) {
            "r" -> AccessMode.READ
            "w" -> AccessMode.WRITE
            "rw" -> AccessMode.READ_WRITE
            else -> throw SerializationException("Unknown accessMode: $jsonValue")
        }
    }
}
