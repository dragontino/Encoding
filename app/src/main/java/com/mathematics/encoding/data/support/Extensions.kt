package com.mathematics.encoding.data.support

import android.content.Context
import android.widget.Toast
import androidx.compose.animation.core.*
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.graphics.ColorUtils
import com.mathematics.encoding.presentation.model.SymbolWithCode
import kotlin.math.abs
import kotlin.math.log2


const val loadingTimeMillis = 400L


@ExperimentalMaterialApi
fun ModalBottomSheetState.isExpanded() =
    targetValue == ModalBottomSheetValue.Expanded


@ExperimentalMaterialApi
fun ModalBottomSheetState.isHalfExpanded() =
    targetValue == ModalBottomSheetValue.HalfExpanded


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
    val pixelsToScroll = layoutInfo.visibleItemsInfo[0].size * itemsToScroll

    animateScrollBy(
        value = pixelsToScroll.toFloat(),
        animationSpec = tween(
            durationMillis = 250 * itemsToScroll,
            delayMillis = 40,
            easing = FastOutSlowInEasing
        )
    )
}