package com.mathematics.encoding

import android.content.Context
import android.widget.Toast
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue
import com.mathematics.encoding.presentation.model.SymbolWithCode
import java.math.BigDecimal
import java.math.RoundingMode
import kotlin.math.log2

@ExperimentalMaterialApi
fun ModalBottomSheetState.isExpanded() =
    targetValue == ModalBottomSheetValue.Expanded


val List<SymbolWithCode>.averageCodeLength: Double
get() = sumOf { it.code.length * it.symbol.probability }


val List<SymbolWithCode>.entropy: Double
get() = fold(0.0) { sum, element ->
    sum - element.symbol.probability * log2(element.symbol.probability)
}


fun Double.round(countSigns: Int): Double {
    val bigDecimal = if (this.isNaN() || this == Double.POSITIVE_INFINITY)
        BigDecimal(0.0)
    else
        BigDecimal(this)
    return bigDecimal.setScale(countSigns, RoundingMode.HALF_UP).toDouble()
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