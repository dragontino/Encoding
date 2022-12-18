package com.mathematics.encoding.presentation.theme

import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

val Shapes = Shapes(
    small = RoundedCornerShape(7.dp),
    medium = RoundedCornerShape(13.dp),
    large = RoundedCornerShape(20.dp)
)


fun CornerBasedShape.removeBottomCorners() = copy(
    bottomStart = CornerSize(0),
    bottomEnd = CornerSize(0)
)