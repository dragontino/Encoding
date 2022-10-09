package com.mathematics.encoding.presentation.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.mathematics.encoding.presentation.theme.animate

@Composable
fun BottomSheet(
    title: String = "",
    bottomSheetContent: @Composable ColumnScope.() -> Unit
) {
    Column {
        if (title.isNotBlank()) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onBackground.animate(),
                textAlign = TextAlign.Start,
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.background.animate())
                    .padding(top = 16.dp, bottom = 12.dp, start = 16.dp)
                    .fillMaxWidth()
            )

            Divider(
                modifier = Modifier.fillMaxWidth(),
                color = colorResource(android.R.color.darker_gray),
                thickness = 1.2.dp
            )
        }

        bottomSheetContent()
    }
}