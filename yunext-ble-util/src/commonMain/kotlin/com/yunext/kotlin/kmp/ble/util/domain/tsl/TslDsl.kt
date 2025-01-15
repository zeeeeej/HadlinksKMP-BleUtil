package com.yunext.kotlin.kmp.ble.util.domain.tsl

fun tsl(
    block: TslBuilder.() -> Unit
): Tsl {
    val tslBuilder = TslBuilder()
    tslBuilder.block()
    return tslBuilder.build()
}

class TslPropertyScope {

    private val _props: MutableMap<String, TslProperty> = mutableMapOf()
    val props: List<TslProperty>
        get() = _props.values.toList()

    fun int(builder: TslIntPropertyBuilder.() -> Unit) {
        val p = intProp(builder)
        if (_props.containsKey(p.identifier)) throw TslIllegalStateException("int{} 重复定义${p.identifier}")
        _props[p.identifier] = p
    }

    fun float(builder: TslFloatPropertyBuilder.() -> Unit) {
        val p = floatProp(builder)
        if (_props.containsKey(p.identifier)) throw TslIllegalStateException("float{} 重复定义${p.identifier}")
        _props[p.identifier] = p
    }

    fun double(builder: TslDoublePropertyBuilder.() -> Unit) {
        val p = doubleProp(builder)
        if (_props.containsKey(p.identifier)) throw TslIllegalStateException("double{} 重复定义${p.identifier}")
        _props[p.identifier] = p
    }

    fun text(builder: TslTextPropertyBuilder.() -> Unit) {
        val p = textProp(builder)
        if (_props.containsKey(p.identifier)) throw TslIllegalStateException("text{} 重复定义${p.identifier}")
        _props[p.identifier] = p
    }

    fun date(builder: TslDatePropertyBuilder.() -> Unit) {
        val p = dateProp(builder)
        if (_props.containsKey(p.identifier)) throw TslIllegalStateException("date{} 重复定义${p.identifier}")
        _props[p.identifier] = p
    }

    fun bool(builder: TslBooleanPropertyBuilder.() -> Unit) {
        val p = booleanProp(builder)
        if (_props.containsKey(p.identifier)) throw TslIllegalStateException("bool{} 重复定义${p.identifier}")
        _props[p.identifier] = p
    }

    fun enum(builder: TslEnumTextPropertyBuilder.() -> Unit) {
        val p = enumProp(builder)
        if (_props.containsKey(p.identifier)) throw TslIllegalStateException("enum{} 重复定义${p.identifier}")
        _props[p.identifier] = p
    }

    fun array(builder: TslArrayPropertyBuilder.() -> Unit) {
        val p = arrayProp(builder)
        if (_props.containsKey(p.identifier)) throw TslIllegalStateException("array{} 重复定义${p.identifier}")
        _props[p.identifier] = p
    }

    fun struct(builder: TslStructPropertyBuilder.() -> Unit) {
        val p = structProp(builder)
        if (_props.containsKey(p.identifier)) throw TslIllegalStateException("struct{} 重复定义${p.identifier}")
        _props[p.identifier] = p
    }

}

class TslEventScope {
    private val _events: MutableMap<String, TslEvent> = mutableMapOf()
    val events: List<TslEvent>
        get() = _events.values.toList()

    fun event(block: TslEventBuilder.() -> Unit) {
        val event = eventInternal(block)
        if (_events.containsKey(event.identifier)) throw TslIllegalStateException("event{} 重复定义${event.identifier}")
        _events[event.identifier] = event
    }
}

class TslServiceScope {
    private val _services: MutableMap<String, TslService> = mutableMapOf()
    val services: List<TslService>
        get() = _services.values.toList()

    fun service(block: TslServiceBuilder.() -> Unit) {
        val service = serviceInternal(block)
        if (_services.containsKey(service.identifier)) throw TslIllegalStateException("service{} 重复定义${service.identifier}")
        _services[service.identifier] = service
    }
}

class TslBuilder {
    var id: String? = null
    var version: String? = null
    var productKey: String? = null
    private val tslPropertyScope = TslPropertyScope()
    private val tslEventScope = TslEventScope()
    private val tslServiceScope = TslServiceScope()

    fun properties(block: TslPropertyScope.() -> Unit) {
        tslPropertyScope.block()
    }

    fun events(block: TslEventScope.() -> Unit) {
        tslEventScope.block()
    }

    fun services(block: TslServiceScope.() -> Unit) {
        tslServiceScope.block()
    }

    fun build(): Tsl {
        return Tsl(
            id = id ?: "",
            version = version ?: "",
            productKey = productKey ?: "",
            current = false,
            properties = tslPropertyScope.props,
            events = tslEventScope.events,
            services = tslServiceScope.services
        )
    }
}

//<editor-fold desc="tsl property">

private fun TslPropertyScope.intProp(builder: TslIntPropertyBuilder.() -> Unit): TslProperty {
    return tslProperty(TslIntPropertyBuilder(), builder)
}

private fun TslPropertyScope.floatProp(builder: TslFloatPropertyBuilder.() -> Unit): TslProperty {
    return tslProperty(TslFloatPropertyBuilder(), builder)
}

private fun TslPropertyScope.doubleProp(builder: TslDoublePropertyBuilder.() -> Unit): TslProperty {
    return tslProperty(TslDoublePropertyBuilder(), builder)
}

private fun TslPropertyScope.booleanProp(builder: TslBooleanPropertyBuilder.() -> Unit): TslProperty {
    return tslProperty(TslBooleanPropertyBuilder(), builder)
}

//fun booleanEnum(builder: TslEnumBooleanPropertyBuilder.() -> Unit): TslProperty {
//    return tslProperty(TslEnumBooleanPropertyBuilder(), builder)
//}

private fun TslPropertyScope.enumProp(builder: TslEnumTextPropertyBuilder.() -> Unit): TslProperty {
    return tslProperty(TslEnumTextPropertyBuilder(), builder)
}

private fun TslPropertyScope.textProp(builder: TslTextPropertyBuilder.() -> Unit): TslProperty {
    return tslProperty(TslTextPropertyBuilder(), builder)
}

private fun TslPropertyScope.dateProp(builder: TslDatePropertyBuilder.() -> Unit): TslProperty {
    return tslProperty(TslDatePropertyBuilder(), builder)
}

private fun TslPropertyScope.arrayProp(builder: TslArrayPropertyBuilder.() -> Unit): TslProperty {
    return tslProperty(TslArrayPropertyBuilder(), builder)
}

private fun TslPropertyScope.structProp(builder: TslStructPropertyBuilder.() -> Unit): TslProperty {
    return tslProperty(TslStructPropertyBuilder(), builder)
}

private inline fun <reified T : TslPropertyBuilder> TslPropertyScope.tslProperty(
    builder: T,
    builderBlock: T.() -> Unit
): TslProperty {
    builder.builderBlock()
    return builder.build()
}

sealed class TslPropertyBuilder {
    var identifier: String? = null
    var name: String? = null
    var required: Boolean? = null
    var desc: String? = null
    var accessMode: TslAccessMode? = null

    fun build(): TslProperty {
        val buildInternal = buildInternal()
        return buildInternal
    }

    protected abstract fun buildInternal(): TslProperty
}

class TslIntPropertyBuilder : TslPropertyBuilder() {
    var min: Int? = null
    var max: Int? = null
    var unit: String? = null
    var unitName: String? = null
    var step: Int? = null

    override fun buildInternal(): TslProperty.IntProperty {
        return TslProperty.IntProperty(
            identifier = super.identifier ?: "",
            name = super.name ?: "",
            required = super.required ?: false,
            desc = super.desc ?: "",
            accessMode = super.accessMode ?: TslAccessMode.R,
            specs = TslNumberSpec(
                min = min ?: 0,
                max = max ?: 0,
                unit = unit ?: "",
                unitName = unitName ?: "",
                step = step ?: 1,
            )

        )
    }

}

class TslFloatPropertyBuilder : TslPropertyBuilder() {
    var min: Float? = null
    var max: Float? = null
    var unit: String? = null
    var unitName: String? = null
    var step: Float? = null

    override fun buildInternal(): TslProperty.FloatProperty {
        return TslProperty.FloatProperty(
            identifier = super.identifier ?: "",
            name = super.name ?: "",
            required = super.required ?: false,
            desc = super.desc ?: "",
            accessMode = super.accessMode ?: TslAccessMode.R,
            specs = TslNumberSpec(
                min = min ?: 0f,
                max = max ?: 0f,
                unit = unit ?: "",
                unitName = unitName ?: "",
                step = step ?: 1f,
            )

        )
    }

}

class TslDoublePropertyBuilder : TslPropertyBuilder() {
    var min: Double? = null
    var max: Double? = null
    var unit: String? = null
    var unitName: String? = null
    var step: Double? = null

    override fun buildInternal(): TslProperty.DoubleProperty {
        return TslProperty.DoubleProperty(
            identifier = super.identifier ?: "",
            name = super.name ?: "",
            required = super.required ?: false,
            desc = super.desc ?: "",
            accessMode = super.accessMode ?: TslAccessMode.R,
            specs = TslNumberSpec(
                min = min ?: 0.0,
                max = max ?: 0.0,
                unit = unit ?: "",
                unitName = unitName ?: "",
                step = step ?: 1.0,
            ),

            )
    }
}

class TslTextPropertyBuilder : TslPropertyBuilder() {
    var length: Int? = null
    override fun buildInternal(): TslProperty.TextProperty {
        return TslProperty.TextProperty(
            identifier = super.identifier ?: "",
            name = super.name ?: "",
            required = super.required ?: false,
            desc = super.desc ?: "",
            accessMode = super.accessMode ?: TslAccessMode.R,
            specs = TslTextSpec(
                length = length ?: 0
            ),
        )
    }
}

class TslDatePropertyBuilder : TslPropertyBuilder() {
    var length: Int? = null

    override fun buildInternal(): TslProperty.DateProperty {
        return TslProperty.DateProperty(
            identifier = super.identifier ?: "",
            name = super.name ?: "",
            required = super.required ?: false,
            desc = super.desc ?: "",
            accessMode = super.accessMode ?: TslAccessMode.R,
            specs = TslDateSpec(
                length = length ?: 0
            ),
        )
    }
}

class TslBooleanPropertyBuilder : TslPropertyBuilder() {
    var trueValueDesc: Pair<Int, String>? = null
    var falseValueDesc: Pair<Int, String>? = null


    override fun buildInternal(): TslProperty.BoolProperty {
        return TslProperty.BoolProperty(
            identifier = super.identifier ?: "",
            name = super.name ?: "",
            required = super.required ?: false,
            desc = super.desc ?: "",
            accessMode = super.accessMode ?: TslAccessMode.R,
            specs = TslEnumSpec(
                enumDesc = mapOf(
                    trueValueDesc?.let { (k, v) -> k.toString() to v } ?: ("1" to ""),
                    falseValueDesc?.let { (k, v) -> k.toString() to v } ?: ("0" to ""),
                )
            ),
        )
    }
}

//class TslEnumBooleanPropertyBuilder : TslPropertyBuilder() {
//    var valueDescList: (Map<Int, String>)? = null
//    override fun buildInternal(): TslProperty.EnumBooleanProperty {
//
//        return TslProperty.EnumBooleanProperty(
//            identifier = super.identifier ?: "",
//            name = super.name ?: "",
//            required = super.required ?: false,
//            desc = super.desc ?: "",
//            accessMode = super.accessMode ?: TslAccessMode.R,
//            spec = TslEnumIntSpec(
//                enumDesc = valueDescList ?: mapOf()
//            ),
//        )
//    }
//}

class TslEnumTextPropertyBuilder : TslPropertyBuilder() {
    var valueDescList: (Map<String, String>)? = null
    override fun buildInternal(): TslProperty.EnumTextProperty {

        return TslProperty.EnumTextProperty(
            identifier = super.identifier ?: "",
            name = super.name ?: "",
            required = super.required ?: false,
            desc = super.desc ?: "",
            accessMode = super.accessMode ?: TslAccessMode.R,
            specs = TslEnumSpec(
                enumDesc = valueDescList ?: mapOf()
            ),
        )
    }
}

class TslArrayPropertyBuilder : TslPropertyBuilder() {
    private val tslPropertyScope = TslPropertyScope()
    var length: Int? = null

    fun type(block: TslPropertyScope.() -> Unit) {
        if (tslPropertyScope.props.isNotEmpty()) throw TslIllegalStateException("type{}已存在定义")
        tslPropertyScope.block()
    }

    override fun buildInternal(): TslProperty.ArrayProperty {
//        val thisType = type
//        check(thisType != null) {
//            "type must not be null"
//        }
//        check(thisType.deep > 0) {
//            "Array type deep must >0 $thisType"
//        }
        return TslProperty.ArrayProperty(
            identifier = super.identifier ?: "",
            name = super.name ?: "",
            required = super.required ?: false,
            desc = super.desc ?: "",
            accessMode = super.accessMode ?: TslAccessMode.R,
            specs = TslArraySpec(
                length = length ?: 0,
                type = tslPropertyScope.props.singleOrNull()
                    ?: throw TslIllegalStateException("no type")
            ),
        )
    }
}

class TslStructPropertyBuilder : TslPropertyBuilder() {
    private val tslPropertyScope = TslPropertyScope()

    fun items(block: TslPropertyScope.() -> Unit) {
        tslPropertyScope.block()
    }

    override fun buildInternal(): TslProperty.StructProperty {
        return TslProperty.StructProperty(
            identifier = super.identifier ?: "",
            name = super.name ?: "",
            required = super.required ?: false,
            desc = super.desc ?: "",
            accessMode = super.accessMode ?: TslAccessMode.R,
            specs = TslStructSpec(
                items = tslPropertyScope.props
            ),
        )
    }
}
//</editor-fold>

//<editor-fold desc="tsl event">
private fun eventInternal(
    builderBlock: TslEventBuilder.() -> Unit
): TslEvent {
    val builder = TslEventBuilder()
    builderBlock.invoke(builder)
    return builder.build()
}

class TslEventBuilder {
    private val props: MutableMap<String, TslProperty> = mutableMapOf()
    private val tslPropertyScope = TslPropertyScope()

    fun output(block: TslPropertyScope.() -> Unit) {
        tslPropertyScope.block()
        tslPropertyScope.props.forEach {
            if (props.containsKey(it.identifier)) throw TslIllegalStateException("output{}重复定义:${it.identifier}")
            props[it.identifier] = it
        }
    }

    var identifier: String? = null
    var name: String? = null
    var required: Boolean? = null
    var desc: String? = null

    /* 事件类型（info、alert、error） */
    var type: TslEventType? = null

    /* 事件对应的方法名称（根据identifier生成）*/
    var method: String? = null

    fun build(): TslEvent {
        return TslEvent(
            identifier = identifier ?: "",
            name = name ?: "",
            required = required ?: false,
            desc = desc ?: "",
            type = type ?: TslEventType.Info,
            method = method ?: "",
            outputData = tslPropertyScope.props
        )
    }
}
//</editor-fold>

//<editor-fold desc="tsl service">
private fun serviceInternal(
    builderBlock: TslServiceBuilder.() -> Unit
): TslService {
    val builder = TslServiceBuilder()
    builderBlock.invoke(builder)
    return builder.build()
}

class TslServiceBuilder() {
    var identifier: String? = null
    var name: String? = null
    var required: Boolean? = null
    var desc: String? = null

    var callType: TslServiceCallType? = null

    /* 事件对应的方法名称（根据identifier生成）*/
    var method: String? = null

    private val outProps: MutableMap<String, TslProperty> = mutableMapOf()
    private val inProps: MutableMap<String, TslProperty> = mutableMapOf()
    private val inputTslPropertyScope = TslPropertyScope()
    private val outputTslPropertyScope = TslPropertyScope()

    fun output(block: TslPropertyScope.() -> Unit) {
        outputTslPropertyScope.block()
        outputTslPropertyScope.props.forEach {
            if (outProps.containsKey(it.identifier)) throw TslIllegalStateException("output{}重复定义:${it.identifier}")
            outProps[it.identifier] = it
        }
    }

    fun input(block: TslPropertyScope.() -> Unit) {
        inputTslPropertyScope.block()
        inputTslPropertyScope.props.forEach {
            if (inProps.containsKey(it.identifier)) throw TslIllegalStateException("inProps{}重复定义:${it.identifier}")
            inProps[it.identifier] = it
        }
    }


    fun build(): TslService {
        return TslService(
            identifier = identifier ?: "",
            name = name ?: "",
            required = required ?: false,
            desc = desc ?: "",
            method = method ?: "",
            outputData = outputTslPropertyScope.props,
            inputData = inputTslPropertyScope.props,
            callType = callType ?: TslServiceCallType.Async
        )
    }
}
//</editor-fold>