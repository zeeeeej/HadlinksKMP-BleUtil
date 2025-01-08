import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.MutableWindowInsets
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.exclude
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.onConsumedWindowInsetsChanged
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContent
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.juul.kable.Filter
import com.juul.kable.Peripheral
import com.juul.kable.Scanner
import com.juul.kable.State
import com.juul.kable.characteristicOf
import com.juul.kable.descriptorOf
import com.juul.kable.logs.Hex
import com.juul.kable.logs.Logging
import com.juul.kable.logs.SystemLogEngine
import com.yunext.kotlin.kmp.ble.core.PlatformBluetoothGattDescriptor
import com.yunext.kotlin.kmp.ble.util.domain.his.Sig
import com.yunext.kotlin.kmp.ble.util.ui.BluetoothScreen
import com.yunext.kotlin.kmp.ble.util.ui.slave.TEST_CH
import com.yunext.kotlin.kmp.ble.util.ui.slave.TEST_Service
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

import org.jetbrains.compose.ui.tooling.preview.Preview
import kotlin.random.Random

@OptIn(ExperimentalLayoutApi::class)
@Composable
@Preview
fun App() {
    MaterialTheme {
        val rememberCoroutineScope = rememberCoroutineScope()
        val remainingInsets = remember { MutableWindowInsets() }
        val safeContent = WindowInsets.safeContent
        Box(Modifier.navigationBarsPadding()
            .onConsumedWindowInsetsChanged { consumedWindowInsets ->
                remainingInsets.insets = safeContent.exclude(consumedWindowInsets)
            }) {
            // padding can be used without recomposition when insets change.
            Box(
                Modifier
//                .padding(remainingInsets.asPaddingValues())
            ) {

                BluetoothScreen(Modifier.fillMaxSize())

                FloatingActionButton(onClick = {
                    testKble(rememberCoroutineScope)

                }, modifier = Modifier.align(Alignment.BottomEnd).padding(32.dp)) {
                    Text("测试kable-3")
                }
            }
        }
    }
}

@OptIn(ExperimentalStdlibApi::class)
private fun testKble(
    rememberCoroutineScope: CoroutineScope,
) {
    rememberCoroutineScope.launch {
        println("xpl 开始搜索 3")
        val scanner = Scanner {
            filters {
                match {
                    name = Filter.Name.Prefix("water_")
//                                    services = listOf(uuidFrom("00000000-1001-1000-6864-79756e657874"))
                }

//                                match {
//                                    services =
//                                        listOf(uuidFrom("00000000-1000-1000-6864-79756e657874"))
//                                }

//                                match {
//                                    services = emptyList()
//                                }
            }
            logging {
                engine = SystemLogEngine
                level = Logging.Level.Warnings
                format = Logging.Format.Multiline
            }
        }
        println("xpl 创建scanner :$scanner")

//                            scanner.advertisements.collect {
//                                val name = it.name
//                                val peripheralName = it.peripheralName
//                                println("xpl 搜索结果:$it $name $peripheralName uuids:${
//                                    it.uuids.joinToString { u->
//                                        "${u.toString()}\n"
//                                    }
//                                }")
//                            }
        val advertisement =
            scanner.advertisements.firstOrNull() ?: return@launch
        println("xpl 搜索到了 :$advertisement")
        val peripheral = Peripheral(advertisement) {
            logging {
                engine = SystemLogEngine
                level = Logging.Level.Warnings
                format = Logging.Format.Multiline
                data = Hex
                identifier = "xpl_ble"
            }

            onServicesDiscovered {
                println("xpl onServicesDiscovered")
//                this.write(
//                    descriptorOf(
//                        "00000000-1001-1000-6864-79756e657874",
//                        "0000a201-1001-1000-6864-79756e657874",
//                        Sig.UUID_CLIENT_CHARACTERISTIC_CONFIGURATION
//                    ),
//                    PlatformBluetoothGattDescriptor.Value.EnableNotificationValue.value
//                        ?: byteArrayOf()
//                )
                launch {
                    while (isActive) {
                        delay(2000)
                        write(
                            characteristicOf(
                                TEST_Service,
                                TEST_CH
                            ), byteArrayOf(0xEE.toByte(),0xEE.toByte())+ Random.nextBytes(5)
                        )
                    }
                }

            }


        }
        launch {
            peripheral.services.collect {
                println(
                    "xpl services ${
                        it?.joinToString {
                            "\n ${it.serviceUuid}"
                        }
                    }"
                )
            }
        }

        launch {
            peripheral.state.collect {
                println("xpl state : $it")
                when (it) {
                    State.Connected -> {}
                    State.Connecting.Bluetooth -> {}
                    State.Connecting.Observes -> {}
                    State.Connecting.Services -> {}
                    is State.Disconnected -> {}
                    State.Disconnecting -> {}
                }
            }
        }

        launch {
            println("xpl 开始observe $TEST_Service/$TEST_CH")
            peripheral.observe(
                characteristicOf(
                    TEST_Service,
                    TEST_CH
                ), onSubscription = {})
                .collect {
                    println("xpl observe $TEST_Service/$TEST_CH ${it.toHexString()}")
                }
        }


        println("xpl 开始连接")
        peripheral.connect()
    }
}



