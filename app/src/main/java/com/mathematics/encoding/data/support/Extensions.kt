package com.mathematics.encoding.data.support

import android.content.Context
import android.content.ContextWrapper
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.*
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import androidx.core.graphics.ColorUtils
import com.mathematics.encoding.presentation.model.SymbolWithCode
import com.mathematics.encoding.presentation.theme.animate
import kotlin.math.abs
import kotlin.math.log2


const val loadingTimeMillis = 450L


val List<SymbolWithCode>.averageCodeLength: Double
get() = sumOf { it.code.length * it.probability }


val List<SymbolWithCode>.entropy: Double
get() = fold(0.0) { sum, element ->
    sum - element.probability * log2(element.probability)
}


fun showToast(context: Context, text: String, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(context, text, duration).show()
}


operator fun Color.plus(other: Color): Color {
    return Color(ColorUtils.blendARGB(this.toArgb(), other.toArgb(), 1f))
}


fun Context.getActivity(): AppCompatActivity? = when (this) {
    is AppCompatActivity -> this
    is ContextWrapper -> baseContext.getActivity()
    else -> null
}


@Composable
fun LazyListState.isScrollingUp(): Boolean {
    var previousIndex by remember(this) { mutableStateOf(firstVisibleItemIndex) }
    var previousScrollOffset by remember(this) { mutableStateOf(firstVisibleItemScrollOffset) }
    return remember(this) {
        derivedStateOf {
            if (previousIndex != firstVisibleItemIndex) {
                previousIndex > firstVisibleItemIndex
            } else {
                previousScrollOffset >= firstVisibleItemScrollOffset
            }.also {
                previousIndex = firstVisibleItemIndex
                previousScrollOffset = firstVisibleItemScrollOffset
            }
        }
    }.value
}


suspend fun LazyListState.smoothScrollToItem(targetPosition: Int) {
    val itemsToScroll = abs(targetPosition - firstVisibleItemScrollOffset)
    val itemSize = layoutInfo.visibleItemsInfo.firstOrNull()?.size ?: 0
    val pixelsToScroll = itemSize * itemsToScroll

    animateScrollBy(
        value = pixelsToScroll.toFloat(),
        animationSpec = tween(
            durationMillis = 250 * itemsToScroll,
            delayMillis = 40,
            easing = FastOutSlowInEasing
        )
    )
}


fun <T : Any> mainScreenTabsAnimationSpec() = tween<T>(
    durationMillis = 500,
    delayMillis = 10,
    easing = FastOutSlowInEasing,
)



@ExperimentalAnimationApi
@Composable
internal fun Loading(isLoading: Boolean, modifier: Modifier = Modifier) {
    AnimatedVisibility(
        visible = isLoading,
        enter = fadeIn(
            tween(durationMillis = 500, easing = LinearOutSlowInEasing)
        ),
        exit = fadeOut(
            tween(durationMillis = 500, easing = LinearOutSlowInEasing)
        ),
        modifier = modifier
            .background(MaterialTheme.colorScheme.background.animate())
            .fillMaxSize()
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            CircularProgressIndicator(
                color = MaterialTheme.colorScheme.primary.animate(),
                strokeWidth = 3.5.dp,
                modifier = Modifier
                    .animateEnterExit(
                        enter = slideInVertically(
                            tween(
                                durationMillis = 300,
                                delayMillis = 100,
                                easing = FastOutSlowInEasing
                            )
                        ),
                        exit = slideOutVertically(
                            tween(
                                durationMillis = 300,
                                delayMillis = 80,
                                easing = FastOutSlowInEasing
                            )
                        ) { it / 2 }
                    )
                    .scale(1.4f)
                    .padding(top = 100.dp)
            )
        }
    }
}


fun Bundle?.getString(key: String, defaultValue: String) =
    this?.getString(key) ?: defaultValue