package com.mathematics.encoding.presentation.view.mainscreen

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.liveData
import com.mathematics.encoding.data.support.averageCodeLength
import com.mathematics.encoding.data.support.entropy
import com.mathematics.encoding.data.support.round
import com.mathematics.encoding.presentation.model.Settings
import com.mathematics.encoding.presentation.model.SymbolWithCode
import com.mathematics.encoding.presentation.theme.EncodingAppTheme
import com.mathematics.encoding.presentation.theme.animate
import com.mathematics.encoding.presentation.theme.smallCornerSize


@ExperimentalFoundationApi
@Composable
internal fun CodesList(symbolWithCodes: List<SymbolWithCode>, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .background(
                color = MaterialTheme.colorScheme.background.animate(),
                shape = RoundedCornerShape(smallCornerSize)
            )
            .padding(9.dp)
            .fillMaxWidth()
    ) {
        Text(
            "Средняя длина кода = ${symbolWithCodes.averageCodeLength.round(2)}",
            fontSize = MaterialTheme.typography.labelSmall.fontSize,
            fontFamily = MaterialTheme.typography.labelMedium.fontFamily,
            color = MaterialTheme.colorScheme.onBackground.animate(),
            modifier = Modifier.padding(top = 8.dp, bottom = 2.dp, start = 6.dp)
        )
        Text(
            "Энтропия = ${symbolWithCodes.entropy.round(2)}",
            fontSize = MaterialTheme.typography.labelSmall.fontSize,
            fontFamily = MaterialTheme.typography.labelMedium.fontFamily,
            color = MaterialTheme.colorScheme.onBackground.animate(),
            modifier = Modifier.padding(bottom = 8.dp, top = 2.dp, start = 6.dp)
        )

        Column(
            modifier = Modifier
                .border(
                    1.5.dp,
                    MaterialTheme.colorScheme.onBackground.animate(),
                    shape = RoundedCornerShape(smallCornerSize)
                )
                .padding(4.dp)
        ) {
            TableHead("Символ", "Вероятность", "Код")
            Divider(
                color = MaterialTheme.colorScheme.onBackground.animate(),
                thickness = 1.5.dp,
                modifier = Modifier
                    .padding(vertical = 1.dp)
                    .fillMaxWidth()
            )

            LazyColumn {
                items(symbolWithCodes.size) { index ->
                    val symbol = symbolWithCodes[index]
                    TableRow(
                        items = arrayOf(
                            symbol.symbolName,
                            symbol.probability.toString(),
                            symbol.code
                        ),
                        fontSize = 16.sp
                    )
                }
            }
        }
    }
}


@Composable
private fun TableHead(vararg items: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier
            .animateContentSize(spring(stiffness = Spring.StiffnessVeryLow))
            .padding(vertical = 8.dp)
            .fillMaxWidth(),
    ) {
        items.forEach { TableCell(text = it, fontSize = 19.sp) }
    }
}


@ExperimentalFoundationApi
@Composable
private fun LazyItemScope.TableRow(items: Array<String>, fontSize: TextUnit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier
            .animateContentSize(spring(stiffness = Spring.StiffnessVeryLow))
            .animateItemPlacement(spring(stiffness = Spring.StiffnessVeryLow))
            .padding(vertical = 8.dp)
            .fillMaxWidth(),
    ) {
        items.forEach { TableCell(it, fontSize) }
    }
}


@Composable
private fun RowScope.TableCell(text: String, fontSize: TextUnit) {
    SelectionContainer(
        modifier = Modifier
            .padding(horizontal = 0.dp)
            .weight(1f)
    ) {
        Text(
            text = text,
            color = MaterialTheme.colorScheme.onBackground.animate(),
            fontSize = fontSize,
            textAlign = TextAlign.Center,
            maxLines = 1
        )
    }
}




@ExperimentalFoundationApi
@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun CodesListPreview() {
    EncodingAppTheme(settings = liveData { emit(Settings()) }) {
        CodesList(
            symbolWithCodes = listOf(
                SymbolWithCode("A", 0.3, "01"),
                SymbolWithCode("B", 0.5, "1"),
                SymbolWithCode("C", 0.2, "00")
            ),
        )
    }
}