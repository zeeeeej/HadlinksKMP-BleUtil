package kmp.ble.util.domain.tsl

import com.yunext.kotlin.kmp.ble.util.domain.tsl.TslEventType
import com.yunext.kotlin.kmp.ble.util.domain.tsl.decodeJson
import com.yunext.kotlin.kmp.ble.util.domain.tsl.encodeJson
import com.yunext.kotlin.kmp.ble.util.domain.tsl.tsl
import kotlin.test.Test

class TslDslAndSerializableTest {

    @Test
    fun t1() {

        val tsl = tsl {
            id = "tsl.id"
            productKey = "tsl.productKey"
            version = "tsl.version"

            properties {
                int {
                    identifier = "int.identifier.props"
                    name = "int.name"
                    desc = "int.desc"
                    max = 100
                    min = 0
                    step = 1
                    unit = "int.unit"
                    unitName = "int.unitName"
                }
                float {
                    identifier = "float.identifier"
                    name = "float.name"
                    desc = "float.desc"
                    max = 100f
                    min = 0f
                    step = 1f
                    unit = "int.unit"
                    unitName = "int.unitName"
                }
                double {
                    identifier = "double.identifier"
                    name = "double.name"
                    desc = "double.desc"
                    max = 100.0
                    min = 0.0
                    step = 1.0
                    unit = "double.unit"
                    unitName = "double.unitName"
                }
                date {
                    identifier = "date.identifier"
                    name = "date.name"
                    desc = "date.desc"
                    length = 10240
                }
                text {
                    identifier = "text.identifier"
                    name = "text.name"
                    desc = "text.desc"
                    length = 10240
                }
                bool {
                    identifier = "boolean.identifier"
                    name = "boolean.name"
                    desc = "boolean.desc"
                    trueValueDesc = 1 to "boolean.true"
                    falseValueDesc = 0 to "boolean.false"
                }
                enum {
                    identifier = "textEnum.identifier"
                    name = "textEnum.name"
                    desc = "textEnum.desc"
                    valueDescList = mapOf(
                        "dong" to "*东*",
                        "xi" to "*西*",
                        "nan" to "*南*",
                        "bei" to "*北*",
                    )
                }
                array {
                    identifier = "array.identifier"
                    name = "array.name"
                    desc = "array.desc"
                    length = 10

                    type {
                        int {
                            identifier = "array.int.identifier"
                            name = "array.int.name"
                            desc = "array.int.desc"
                            max = 100
                            min = 0
                            step = 1
                            unit = "array.int.unit"
                            unitName = "array.int.unitName"
                        }
                    }
                }
                struct {
                    identifier = "struct.identifier"
                    name = "struct.name"
                    desc = "struct.desc"
                    items {
                        int {
                            identifier = "struct.int.identifier"
                            name = "struct.int.name"
                            desc = "struct.int.desc"
                            max = 100
                            min = 0
                            step = 1
                            unit = "struct.int.unit"
                            unitName = "struct.int.unitName"
                        }
                        enum {
                            identifier = "struct.textEnum.identifier"
                            name = "struct.textEnum.name"
                            desc = "struct.textEnum.desc"
                            valueDescList = mapOf(
                                "struct.dong" to "=东=",
                                "struct.xi" to "=西=",
                                "struct.nan" to "=南=",
                                "struct.bei" to "=北=",
                            )
                        }
                    }
                }
            }

            events {
                event {
                    identifier = "event.identifier"
                    name = "event.name"
                    desc = "event.desc"
                    type = TslEventType.Error
                    method = "event.method"
                    output {
                        int {
                            identifier = "struct.int.identifier"
                            name = "struct.int.name"
                            desc = "struct.int.desc"
                            max = 100
                            min = 0
                            step = 1
                            unit = "struct.int.unit"
                            unitName = "struct.int.unitName"
                        }
                        enum {
                            identifier = "struct.textEnum.identifier"
                            name = "struct.textEnum.name"
                            desc = "struct.textEnum.desc"
                            valueDescList = mapOf(
                                "struct.dong" to "=东=",
                                "struct.xi" to "=西=",
                                "struct.nan" to "=南=",
                                "struct.bei" to "=北=",
                            )
                        }
                    }
                }
                event {
                    identifier = "event2.identifier"
                    name = "event2.name"
                    desc = "event2.desc"
                    type = TslEventType.Error
                    method = "event2.method"
                    output {
                        int {
                            identifier = "event2.int.identifier"
                            name = "event2.int.name"
                            desc = "event2.int.desc"
                            max = 100
                            min = 0
                            step = 1
                            unit = "event2.int.unit"
                            unitName = "event2.int.unitName"
                        }
                        enum {
                            identifier = "struct.textEnum.identifier"
                            name = "event2.textEnum.name"
                            desc = "event2.textEnum.desc"
                            valueDescList = mapOf(
                                "event2.dong" to "=东=",
                                "event2.xi" to "=西=",
                                "event2.nan" to "=南=",
                                "event2.bei" to "=北=",
                            )
                        }
                    }
                }
            }

            services {
                service {
                    identifier = "service1.identifier"
                    name = "service1.name"
                    desc = "service1.desc"
                    method = "service1.method"
                    output {
                        int {
                            identifier = "service1.int.identifier"
                            name = "service1.int.name"
                            desc = "service1.int.desc"
                            max = 100
                            min = 0
                            step = 1
                            unit = "service1.int.unit"
                            unitName = "service1.int.unitName"
                        }
                        enum {
                            identifier = "struct.textEnum.identifier"
                            name = "service1.textEnum.name"
                            desc = "service1.textEnum.desc"
                            valueDescList = mapOf(
                                "service1.dong" to "=东=",
                                "service1.xi" to "=西=",
                                "service1.nan" to "=南=",
                                "service1.bei" to "=北=",
                            )
                        }
                    }
                    input {
                        int {
                            identifier = "service1.int.identifier"
                            name = "service1.int.name"
                            desc = "service1.int.desc"
                            max = 100
                            min = 0
                            step = 1
                            unit = "service1.int.unit"
                            unitName = "service1.int.unitName"
                        }
                        enum {
                            identifier = "struct.textEnum.identifier"
                            name = "service1.textEnum.name"
                            desc = "service1.textEnum.desc"
                            valueDescList = mapOf(
                                "service1.dong" to "=东=",
                                "service1.xi" to "=西=",
                                "service1.nan" to "=南=",
                                "service1.bei" to "=北=",
                            )
                        }
                    }
                }
                service {
                    identifier = "service2.identifier"
                    name = "service1.name"
                    desc = "service1.desc"
                    method = "service1.method"
                    output {
                        int {
                            identifier = "service1.int.identifier"
                            name = "service1.int.name"
                            desc = "service1.int.desc"
                            max = 100
                            min = 0
                            step = 1
                            unit = "service1.int.unit"
                            unitName = "service1.int.unitName"
                        }
                        enum {
                            identifier = "struct.textEnum.identifier"
                            name = "service1.textEnum.name"
                            desc = "service1.textEnum.desc"
                            valueDescList = mapOf(
                                "service1.dong" to "=东=",
                                "service1.xi" to "=西=",
                                "service1.nan" to "=南=",
                                "service1.bei" to "=北=",
                            )
                        }
                    }
                    input {
                        int {
                            identifier = "service1.int.identifier"
                            name = "service1.int.name"
                            desc = "service1.int.desc"
                            max = 100
                            min = 0
                            step = 1
                            unit = "service1.int.unit"
                            unitName = "service1.int.unitName"
                        }
                        enum {
                            identifier = "struct.textEnum.identifier"
                            name = "service1.textEnum.name"
                            desc = "service1.textEnum.desc"
                            valueDescList = mapOf(
                                "service1.dong" to "=东=",
                                "service1.xi" to "=西=",
                                "service1.nan" to "=南=",
                                "service1.bei" to "=北=",
                            )
                        }
                    }
                }
            }
        }
        println(tsl)
        val encode = tsl.encodeJson()
        println("-> encode:")
        println(encode)
        val decodeJson = encode.decodeJson()
        println("-> decodeJson:")
        println(decodeJson)

    }
}