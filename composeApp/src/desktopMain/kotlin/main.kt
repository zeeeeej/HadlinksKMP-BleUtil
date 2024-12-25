import androidx.compose.material.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.application
import androidx.compose.ui.window.awaitApplication
import androidx.compose.ui.window.rememberWindowState

suspend fun main() = awaitApplication {
    val state = rememberWindowState(
        size = DpSize(600.dp, 900.dp),
        position = WindowPosition(Alignment.BottomCenter)
    )
    Window(
        onCloseRequest = ::exitApplication,
        title = "12345",//stringResource( Res.string.app_name),
        state = state
    ) {

        App()
//
//        Text("hello mac")
    }
}