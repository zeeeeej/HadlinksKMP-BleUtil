package kmp.ble.util.domain.tsl

import com.yunext.kotlin.kmp.ble.util.domain.tsl.TslProperty
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encodeToString
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.Json.Default.serializersModule
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.float
import kotlinx.serialization.json.floatOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.serializer
import kotlin.test.Test

class SealedTest {
    @Test
    fun test() {
//        val json = Json {
//            encodeDefaults = true
//            ignoreUnknownKeys = true
//            prettyPrint = true
//        }
//        val bird = Animal.Bird("鸟", 1f, Sex.F)
//        val cat = Animal.Cat("猫", 15, Sex.M)
//        println(json.encodeToString(bird))
//        println(json.encodeToString(cat))
//        val bird1: Animal = Animal.Bird("鸟", 1f, Sex.F)
//        val cat1: Animal = Animal.Cat("猫", 15, Sex.M)
//        println(json.encodeToString(Animal, bird1))
//        println(json.encodeToString(Animal, cat1))
//        val json2 = json.encodeToString(Animal, cat1)
//        val decodeFromString = json.decodeFromString(Animal, json2)
//        println("animal  =  $decodeFromString")
//
//        val bird2: Animal = Animal.Bird("鸟", 1f, Sex.F)
//        val info = MyInfo("lilei", bird2, listOf(bird2))
//        println(json.encodeToString(info))
//        val jsonResult = json.encodeToString(info)
//        val info2 = json.decodeFromString<MyInfo>(jsonResult)
//        println(info2)

        val json = Json {
            encodeDefaults = true
            ignoreUnknownKeys = true
            prettyPrint = true
        }
        val source = """
            {
                "name": "lilei",
                "pet": {
                    "name": "鸟",
                    "weight": 1.0,
                    "sex": "f"
                },
                "pets": [
                    {
                        "name": "鸟",
                        "weight": 1.0,
                        "sex": "f"
                    }
                ]
            }
        """.trimIndent()
        json.decodeFromString<MyInfo>(source)
    }
}

@Serializable
class MyInfo(
    val name: String,
    val pet: Animal,
    val pets: List<Animal>
)

//@Serializable(with = Animal.Companion::class)
@Serializable(with = AnimalKSerializer::class)
sealed interface Animal {
    val name: String
    val sex: Sex

    @Serializable
    class Bird(override val name: String, val weight: Float, override val sex: Sex) : Animal

    @Serializable
    class Cat(override val name: String, val color: Int, override val sex: Sex) : Animal

    companion object : KSerializer<Animal> {
        override val descriptor: SerialDescriptor = buildClassSerialDescriptor("Animal") {
            element<String>("name")
            element<Float>("weight", isOptional = true)
            element<Int>("color", isOptional = true)
            element<String>("sex")
        }

        override fun deserialize(decoder: Decoder): Animal {
            println("====>deserialize")
            val input = decoder.beginStructure(descriptor)
            var name: String? = null
            var weight: Float? = null
            var color: Int? = null
            var sex: Sex? = null

            loop@ while (true) {
                when (val index = input.decodeElementIndex(descriptor)) {
                    0 -> name = input.decodeStringElement(descriptor, index)
                    1 -> weight = input.decodeFloatElement(descriptor, index)
                    2 -> color = input.decodeIntElement(descriptor, index)
                    3 -> sex = input.decodeSerializableElement(descriptor, index, Sex)
                    CompositeDecoder.DECODE_DONE -> break@loop
                    else -> throw SerializationException("Unexpected index: $index")
                }
            }

            input.endStructure(descriptor)

            return if (weight != null) {
                Animal.Bird(name!!, weight, sex!!)
            } else if (color != null) {
                Animal.Cat(name!!, color, sex!!)
            } else {
                throw SerializationException("Unknown type of Animal")
            }
        }

        override fun serialize(encoder: Encoder, value: Animal) {
            println("====>serialize")
            val output = encoder.beginStructure(descriptor)
            output.encodeStringElement(descriptor, 0, value.name)
            when (value) {
                is Animal.Bird -> {
                    output.encodeFloatElement(descriptor, 1, value.weight)
                }

                is Animal.Cat -> {
                    output.encodeIntElement(descriptor, 2, value.color)
                }
            }
            output.encodeSerializableElement(descriptor, 3, Sex, value.sex)
            output.endStructure(descriptor)
        }

    }
}

@Deprecated("error")
object AnimalKSerializer : KSerializer<Animal> {
    override val descriptor: SerialDescriptor
        get() = PrimitiveSerialDescriptor("AnimalKSerializer#123", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): Animal {
        println("00000")
        require(decoder is JsonDecoder) // 确保我们正在处理JSON数据
        println("11111")
        val jsonElement = decoder.decodeJsonElement()
        println("2222")
        val dataType = jsonElement.jsonObject["weight"]?.jsonPrimitive?.floatOrNull
        println("33333 $dataType")
        return if (dataType == null) {
            println("44444")
            decoder.decodeSerializableValue(Animal.Cat.serializer())
        } else {
            println("55555")
            decoder.decodeSerializableValue(Animal.Bird.serializer())
        }
    }

    override fun serialize(encoder: Encoder, value: Animal) {
        when (value) {
            is Animal.Bird -> encoder.encodeSerializableValue(Animal.Bird.serializer(), value)
            is Animal.Cat -> encoder.encodeSerializableValue(Animal.Cat.serializer(), value)
        }
    }

}

@Serializable(with = Sex.Companion::class)
enum class Sex {
    F, M;

    companion object : KSerializer<Sex> {
        override val descriptor: SerialDescriptor =
            PrimitiveSerialDescriptor("Sex", PrimitiveKind.STRING)


        override fun deserialize(decoder: Decoder): Sex {
            println("~~~ Sex::deserialize")
            val k = decoder.decodeString()
            return when (k) {
                "f" -> F
                "m" -> M
                else -> throw IllegalArgumentException("k:$k")
            }
        }

        override fun serialize(encoder: Encoder, value: Sex) {
            println("~~~ Sex::serialize value:$value")
            encoder.encodeString(value.name.lowercase())
        }

    }
}

//fun encode(){
//    val bird: Animal = Animal.Bird("鸟", 1f, Sex.F)
//    // 序列化成
//    {
//        "name": "鸟",
//        "weight": 1f,
//        "sex": "f"
//    }
//    val cat: Animal = Animal.Cat("猫", 15, Sex.M)
//    // 序列化成
//    {
//        "name": "猫",
//        "color": 15,
//        "sex": "m"
//    }
//}


private object SexSerializer : KSerializer<Sex> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("Sex", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: Sex) {
        println("==>SexSerializer::serialize")
        when (value) {
            Sex.F -> encoder.encodeSerializableValue(SexSerializer, value)
            Sex.M -> encoder.encodeSerializableValue(SexSerializer, value)
        }

    }

    override fun deserialize(decoder: Decoder): Sex {
        println("==>SexSerializer::deserialize")
        val key = decoder.decodeString()
        return when (key) {
            "f" -> Sex.F
            "m" -> Sex.M
            else -> throw IllegalArgumentException("key:$key")
        }
    }
}