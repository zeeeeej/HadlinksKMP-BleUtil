package com.yunext.kotlin.kmp.ble.util.ui.common

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.MutableWindowInsets
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.exclude
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.onConsumedWindowInsetsChanged
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContent
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yunext.kotlin.kmp.ble.util.ui.common.HDCommonTitleDefaults.default_height
import com.yunext.kotlin.kmp.ble.util.ui.common.HDCommonTitleDefaults.default_title_style
import com.yunext.kotlin.kmp.ble.util.util.DEFAULT_DP
import kotlinx.coroutines.launch

private object HDCommonTitleDefaults {
    val default_height = 56.dp
    val default_title_style = TextStyle.Default.copy(fontWeight = FontWeight.Bold, fontSize = 18.sp)
}

@Composable
fun HDCommonTitle(
    modifier: Modifier = Modifier.fillMaxWidth().height(default_height),
    //.height(HDCommonTitleDefaults.default_height)
//        .shadow(4.dp)
//        .padding(vertical = DEFAULT_DP)
    title: String,
    showIcon: Boolean = true,
    onBack: () -> Unit,
) {
    val rememberCoroutineScope = rememberCoroutineScope()
    Box(
        modifier
    ) {
        AnimatedVisibility(showIcon,
            enter = slideInHorizontally { -it } + fadeIn(),
            exit = slideOutHorizontally { -it } + fadeOut()) {
            Image(Icons.AutoMirrored.Rounded.ArrowBack, null, modifier = Modifier
                .size(default_height)
                .clickable {
                    onBack()
                }
                .padding(12.dp)
            )
        }

        Text(
            text = title,
            style = default_title_style,
            modifier = Modifier.align(Alignment.Center)
        )
    }

}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun HDCommonPageWithTitle(
    modifier: Modifier = Modifier.fillMaxSize(),
    title: String,
    onBack: () -> Unit,
    content: @Composable () -> Unit
) {
    val remainingInsets = remember { MutableWindowInsets() }
    val safeContent = WindowInsets.safeContent
    Box(Modifier.navigationBarsPadding()
        .onConsumedWindowInsetsChanged { consumedWindowInsets ->
            remainingInsets.insets = safeContent.exclude(consumedWindowInsets)
        }) {
        Column(modifier.padding(remainingInsets.asPaddingValues())) {
            HDCommonTitle(title = title, onBack = onBack)
            content()
        }
    }
}
