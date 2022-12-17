package com.mathematics.encoding.presentation.view.navigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Notes
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontFamily
import com.mathematics.encoding.R
import com.mathematics.encoding.presentation.theme.VarelaFont
import androidx.compose.ui.text.font.FontWeight

typealias TabIconComposable = @Composable (isSelected: Boolean) -> Unit

internal enum class TabItems(@StringRes val title: Int, val icon: TabIconComposable) {
    TextInput(
        title = R.string.text_input,
        icon = {
            Icon(
                imageVector = Icons.Rounded.Notes,
                contentDescription = "text",
            )
        }
    ),

    SymbolsProbabilities(
        title = R.string.symbols_probabilities,
        icon = {
            Text(
                text = "0.5",
                style = MaterialTheme.typography.labelMedium.copy(
                    fontFamily = FontFamily(VarelaFont),
                    fontWeight = if (it) FontWeight.Bold else null
                )
            )
        }
    ),

//    SymbolsFrequencies(
//        title = R.string.symbols_frequencies,
//        icon = {
//            Text(
//                text = "15",
//                style = MaterialTheme.typography.labelMedium.copy(
//                    fontFamily = FontFamily(VarelaFont)
//                )
//            )
//        }
//    )
}
