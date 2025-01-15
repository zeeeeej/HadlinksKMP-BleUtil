package kmp.ble.util.domain.tsl

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.test.Test

class PropertyTest {

    @Test
    fun t1(){
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

        val json = jsonInner.encodeToString(tsl)
        println(json)

    }
}