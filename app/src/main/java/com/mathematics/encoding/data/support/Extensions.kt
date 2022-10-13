package com.mathematics.encoding.data.support

import android.content.Context
import android.widget.Toast
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.graphics.ColorUtils
import com.mathematics.encoding.presentation.model.SymbolWithCode
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


@ExperimentalMaterialApi
suspend fun ModalBottomSheetState.open(stiffness: Float = Spring.StiffnessMediumLow) {
    animateTo(
        targetValue = ModalBottomSheetValue.HalfExpanded,
        anim = spring(stiffness = stiffness)
    )
}


@ExperimentalMaterialApi
suspend fun ModalBottomSheetState.close(stiffness: Float = Spring.StiffnessMediumLow) =
    animateTo(
        targetValue = ModalBottomSheetValue.Hidden,
        anim = spring(stiffness = stiffness)
    )


fun showToast(context: Context, text: String, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(context, text, duration).show()
}


operator fun Color.plus(other: Color): Color {
    return Color(ColorUtils.blendARGB(this.toArgb(), other.toArgb(), 1f))
}