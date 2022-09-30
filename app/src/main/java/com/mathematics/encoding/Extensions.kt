package com.mathematics.encoding

import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue

@ExperimentalMaterialApi
fun ModalBottomSheetState.isExpanded() =
    targetValue == ModalBottomSheetValue.Expanded