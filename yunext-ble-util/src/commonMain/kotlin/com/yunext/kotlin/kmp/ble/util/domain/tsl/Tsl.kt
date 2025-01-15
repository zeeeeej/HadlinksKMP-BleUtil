package com.yunext.kotlin.kmp.ble.util.domain.tsl

import androidx.compose.ui.util.fastJoinToString
import kotlinx.serialization.Serializable

@Serializable
class Tsl(
    val id: String,
    val version: String,
    val productKey: String,
    val current: Boolean,
    val properties: List<TslProperty>,
    val events: List<TslEvent>,
    val services: List<TslService>,
) {
    override fun toString(): String {
        return """
            |
            |>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
            |
            |           Tsl
            |           
            |id          :  $id
            |version     :  $version
            |productKey  :  $productKey
            |current     :  $current
            |// 属性(${properties.size}个)
            |${properties.display()}
            |// 事件(${events.size}个)
            |${events.display()}
            |// 服务(${services.size}个)
            |${services.display()}
            |<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 
            |
        """.trimMargin()
    }
}

sealed interface TslDef {
    val identifier: String
    val name: String
    val required: Boolean
    val desc: String
}

interface TslEventDef : TslDef

interface TslPropertyDef : TslDef

interface TslServiceDef : TslDef

//<editor-fold desc="tsl property definition">
@Serializable(with = TslPropertyTypeSerializer::class)
enum class TslPropertyType(override val key: String) : JsonKey {
    INT("int"),
    FLOAT("float"),
    DOUBLE("double"),
    TEXT("text"),
    DATE("date"),
    BOOL("bool"),
    ENUM("enum"),
    STRUCT("struct"),
    ARRAY("array"),
    ;

    override fun toString(): String {
        return this.key
    }

    companion object {
        fun of(text: String): TslPropertyType {
            return when (text) {
                INT.key -> INT
                FLOAT.key -> FLOAT
                DOUBLE.key -> DOUBLE
                TEXT.key -> TEXT
                DATE.key -> DATE
                BOOL.key -> BOOL
                ENUM.key -> ENUM
                STRUCT.key -> STRUCT
                ARRAY.key -> ARRAY
                else -> throw TslIllegalStateException("不支持的PropertyType:$text")
            }
        }
    }
}

@Serializable(with = TslAccessModeSerializer::class)
enum class TslAccessMode(override val key: String) : JsonKey {
    R("r"), W("w"), RW("rw")
    ;

    companion object {
        fun of(mode: String): TslAccessMode {
            return when (mode) {
                R.key -> R
                W.key -> W
                RW.key -> RW
                else -> throw TslIllegalStateException("不支持的TslAccessMode ：$mode")
            }
        }
    }
}

fun TslProperty.commonDisplay(deep: Int = 0): String {
    return "\n" + """
            |${if (deep == 0) "===================================" else "---------------------------"}
            |identifier  :   $identifier
            |name        :   $name
            |required    :   $required
            |desc        :   $desc
            |accessMode  :   $accessMode
            |dataType    :   $dataType
        """.trimMargin()
}

@Serializable(with = TslPropertySerializer::class)
sealed interface TslProperty : TslPropertyDef {
    val accessMode: TslAccessMode
    val dataType: TslPropertyType
    val specs: TslSpecDef

    @Serializable
    class IntProperty(
        override val identifier: String,
        override val name: String,
        override val required: Boolean,
        override val desc: String,
        override val accessMode: TslAccessMode,
        override val dataType: TslPropertyType = TslPropertyType.INT,
        override val specs: TslNumberSpec<Int>,
    ) : TslProperty {
        override fun toString(): String {
            return commonDisplay() + "\n" + """
                |$specs
            """.trimMargin()
        }
    }

    @Serializable
    class FloatProperty(
        override val identifier: String,
        override val name: String,
        override val required: Boolean,
        override val desc: String,
        override val accessMode: TslAccessMode,
        override val dataType: TslPropertyType = TslPropertyType.FLOAT,
        override val specs: TslNumberSpec<Float>,
    ) : TslProperty {

        override fun toString(): String {
            return commonDisplay() + "\n" + """
                |$specs
            """.trimMargin()
        }
    }

    @Serializable
    class DoubleProperty(
        override val identifier: String,
        override val name: String,
        override val required: Boolean,
        override val desc: String,
        override val accessMode: TslAccessMode,
        override val dataType: TslPropertyType = TslPropertyType.DOUBLE,
        override val specs: TslNumberSpec<Double>,
    ) : TslProperty {
        override fun toString(): String {
            return commonDisplay() + "\n" + """
                |$specs
            """.trimMargin()
        }
    }

    @Serializable
    class TextProperty(
        override val identifier: String,
        override val name: String,
        override val required: Boolean,
        override val desc: String,
        override val accessMode: TslAccessMode,
        override val dataType: TslPropertyType = TslPropertyType.TEXT,
        override val specs: TslTextSpec,
    ) : TslProperty {

        override fun toString(): String {
            return commonDisplay() + "\n" + """
                |$specs
            """.trimMargin()
        }
    }

    @Serializable
    class DateProperty(
        override val identifier: String,
        override val name: String,
        override val required: Boolean,
        override val desc: String,
        override val accessMode: TslAccessMode,
        override val dataType: TslPropertyType = TslPropertyType.DATE,
        override val specs: TslDateSpec,
    ) : TslProperty {

        override fun toString(): String {
            return commonDisplay() + "\n" + """
                |$specs
            """.trimMargin()
        }
    }

    @Serializable
    class BoolProperty(
        override val identifier: String,
        override val name: String,
        override val required: Boolean,
        override val desc: String,
        override val accessMode: TslAccessMode,
        override val dataType: TslPropertyType = TslPropertyType.BOOL,
        override val specs: TslEnumSpec,
    ) : TslProperty {

        override fun toString(): String {
            return commonDisplay() + "\n" + """
                |$specs
            """.trimMargin()
        }
    }

    @Serializable
    // @Polymorphic
    class EnumTextProperty(
        override val identifier: String,
        override val name: String,
        override val required: Boolean,
        override val desc: String,
        override val accessMode: TslAccessMode,
        override val dataType: TslPropertyType = TslPropertyType.ENUM,
        override val specs: TslEnumSpec,
    ) : TslProperty {

        override fun toString(): String {
            return commonDisplay() + "\n" + """
                |$specs
            """.trimMargin()
        }
    }

    @Serializable
    class ArrayProperty(
        override val identifier: String,
        override val name: String,
        override val required: Boolean,
        override val desc: String,
        override val accessMode: TslAccessMode,
        override val dataType: TslPropertyType = TslPropertyType.ARRAY,
        override val specs: TslArraySpec,
    ) : TslProperty {

        override fun toString(): String {
            return commonDisplay() + "\n" + """
                |$specs
            """.trimMargin()
        }
    }

    @Serializable
    class StructProperty(
        override val identifier: String,
        override val name: String,
        override val required: Boolean,
        override val desc: String,
        override val accessMode: TslAccessMode,
        override val dataType: TslPropertyType = TslPropertyType.STRUCT,
        override val specs: TslStructSpec,
    ) : TslProperty {

        override fun toString(): String {
            return commonDisplay() + "\n" + """
                |$specs
            """.trimMargin()
        }
    }


}

//</editor-fold>

//<editor-fold desc="tsl spec">
@Serializable(with = SpecPropertySerializer::class)
sealed interface TslSpecDef

@Serializable
class TslNumberSpec<T : Number>(
    /* 参数最小值（int、float、double类型特有） */
    val min: T,
    /* 参数最大值（int、float、double类型特有） */
    val max: T,
    /* 属性单位（int、float、double类型特有，非必填） */
    val unit: String,
    /* 单位名称（int、float、double类型特有，非必填） */
    val unitName: String,
    val step: T,

    ) : TslSpecDef {
    override fun toString(): String {
        return """
                |min         :   $min
                |max         :   $max
                |unit        :   $unit
                |unitName    :   $unitName
                |step        :   $step
            """.trimMargin()
    }
}

typealias TslDateSpec = TslTextSpec

@Serializable
class TslTextSpec(
    val length: Int,
) : TslSpecDef {
    override fun toString(): String {
        return """
                |length      :   $length
            """.trimMargin()
    }
}

//@Serializable
//class TslBooleanSpec(
//    val enumDesc: Map<Int, String>,
//) : TslSpecDef {
//
//    override fun toString(): String {
//        return """
//                |enumDesc    :   ${enumDesc.toList().fastJoinToString()}
//            """.trimMargin()
//    }
//}

@Serializable
class TslEnumSpec(
    val enumDesc: Map<String, String>
) : TslSpecDef {
    override fun toString(): String {
        return "enumDesc    :" + """
            |   ${enumDesc.toList().fastJoinToString()}
        """.trimMargin()
    }
}

@Serializable
class TslArraySpec(
    val length: Int,
    val type: TslProperty
) : TslSpecDef {
    override fun toString(): String {
        return """
                |length      :   $length
                |type        :   $type
            """.trimMargin()
    }
}

@Serializable
class TslStructSpec(
    val items: List<TslProperty>
) : TslSpecDef {
    override fun toString(): String {
        return "items:${items.display()}"
    }
}
//</editor-fold>

//<editor-fold desc="tsl event definition">
@Serializable
class TslEvent(
    override val identifier: String,
    override val name: String,
    override val required: Boolean,
    override val desc: String,
    /* 事件类型（info、alert、error） */
    val type: TslEventType,
    /* 事件对应的方法名称（根据identifier生成）*/
    val method: String,
    /* 输出参数 */
    val outputData: List<TslProperty>,
) : TslEventDef {
    override fun toString(): String {
        return super.toString() + "\n" + """
            |type        :   $type
            |method      :   $method
            |outputData  :   ${outputData.joinToString("\n") { it.commonDisplay(1) + it.toString() }}
        """.trimMargin()
    }
}

@Serializable(with = TslEventTypeSerializer::class)
enum class TslEventType(override val key: String) : JsonKey {
    Info("info"), Alert("alert"), Error("error")
    ;

    companion object {
        fun of(type: String): TslEventType {
            return when (type) {
                Info.key -> TslEventType.Info
                Alert.key -> TslEventType.Alert
                Error.key -> TslEventType.Error
                else -> throw TslIllegalStateException("不支持的TslEventType : $type")
            }
        }
    }
}


//</editor-fold>

//<editor-fold desc="tsl service definition">
@Serializable
class TslService(
    override val identifier: String,
    override val name: String,
    override val required: Boolean,
    override val desc: String,
    /* async（异步调用）或sync（同步调用）*/
    val callType: TslServiceCallType,
    /* 是否是标准功能的必选属性 */
    val method: String,
    /* 输入参数 */
    val inputData: List<TslProperty>,
    /* 输出参数 */
    val outputData: List<TslProperty>

) : TslServiceDef {
    override fun toString(): String {
        return super.toString() + "\n" + """
            |callType    :   $callType
            |method      :   $method
            |inputData   :    ${inputData.joinToString("\n") { it.commonDisplay(1) + it.toString() }}
            |outputData  :    ${outputData.joinToString("\n") { it.commonDisplay(1) + it.toString() }}
        """.trimMargin()
    }
}

internal interface JsonKey {
    val key: String
}

@Serializable(with = TslServiceCallTypeSerializer::class)
enum class TslServiceCallType(override val key: String) : JsonKey {
    Async("async"), Sync("sync")
    ;

    companion object {
        fun of(type: String): TslServiceCallType {
            return when (type) {
                Async.key -> Async
                Sync.key -> Sync
                else -> throw TslIllegalStateException("不支持的TslServiceCallType : $type")
            }
        }
    }
}
//</editor-fold>

private inline fun <reified T> List<T>.display(crossinline toString: (T) -> String = { it.toString() }): String {
    return this.joinToString("\n") { toString(it) }
}


