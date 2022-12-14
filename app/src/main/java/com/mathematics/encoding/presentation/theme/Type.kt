package com.mathematics.encoding.presentation.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// Set of Material typography styles to start with
val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = FontFamily(RobotoFont),
        fontWeight = FontWeight.Normal,
        fontSize = 20.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    bodyMedium = TextStyle(
        fontSize = 16.sp,
        fontFamily = FontFamily(RobotoFont)
    ),
    bodySmall = TextStyle(
        fontSize = 13.sp,
        fontFamily = FontFamily(RobotoFont),
    ),
    labelLarge = TextStyle(
        fontFamily = FontFamily(RobotoFont),
        fontSize = 24.sp
    ),
    labelMedium = TextStyle(
        fontFamily = FontFamily(RobotoFont),
        fontSize = 20.sp
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily(RobotoFont),
        fontSize = 16.sp
    )
    /* Other default text styles to override
    titleLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
    */
)