package com.mathematics.encoding.presentation.view.screens

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalAnimationApi
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.rounded.ArrowBackIosNew
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mathematics.encoding.data.support.averageCodeLength
import com.mathematics.encoding.data.support.entropy
import com.mathematics.encoding.data.support.round
import com.mathematics.encoding.presentation.model.SymbolWithCode
import com.mathematics.encoding.presentation.theme.EncodingAppTheme
import com.mathematics.encoding.presentation.theme.animate
import com.mathematics.encoding.presentation.theme.smallCornerSize
import com.mathematics.encoding.presentation.view.RoundedCornersScreen
import com.mathematics.encoding.presentation.view.navigation.EncodingScreens


@ExperimentalAnimationApi
@ExperimentalFoundationApi
@ExperimentalMaterial3Api
@Composable
internal fun AnimatedVisibilityScope.ResultScreen(
    title: String,
    resultList: List<SymbolWithCode>,
    navigateTo: (route: String) -> Unit,
    popBackStack: () -> Unit,
    defaultMessage: String = "",
    encodedMessage: String = ""
) {
    RoundedCornersScreen(
        title = title,
        navigationIcon = {
            IconButton(onClick = popBackStack) {
                Icon(
                    imageVector = Icons.Rounded.ArrowBackIosNew,
                    contentDescription = "back"
                )
            }
        },
        actionIcon = {
            IconButton(
                onClick = {
                    navigateTo(EncodingScreens.Settings.route)
                }
            ) {
                Icon(
                    imageVector = Icons.Outlined.Settings,
                    contentDescription = "settings",
                    modifier = it
                )
            }
        },
        showVersion = false
    ) {
        if (resultList.isEmpty()) {
            Text(
                text = "Список пуст...",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onBackground.animate(),
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        } else {
            CodesList(symbolWithCodes = resultList, defaultMessage, encodedMessage)
        }
    }
}



@ExperimentalFoundationApi
@Composable
internal fun CodesList(
    symbolWithCodes: List<SymbolWithCode>,
    defaultMessage: String = "",
    encodedMessage: String = ""
) {
    val tableBorderColor = MaterialTheme.colorScheme.primary

    Column(
        modifier = Modifier
            .background(
                color = MaterialTheme.colorScheme.background.animate(),
                shape = RoundedCornerShape(smallCornerSize)
            )
            .padding(9.dp)
            .fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .border(
                    width = 1.5.dp,
                    color = tableBorderColor.animate(),
                    shape = RoundedCornerShape(smallCornerSize)
                )
                .padding(4.dp)
        ) {
            TableHead("Символ", "Вероятность", "Код")
            Divider(
                color = tableBorderColor.animate(),
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

        Text(
            text = buildAnnotatedString {
                withStyle(
                    SpanStyle(
                        fontWeight = FontWeight.Bold,
                        textDecoration = TextDecoration.Underline
                    )
                ) {
                    append("Средняя длина кода")
                }

                append("\t\t")

                withStyle(SpanStyle(color = MaterialTheme.colorScheme.error)) {
                    append(symbolWithCodes.averageCodeLength.round(2).toString())
                }

            },
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground.animate(),
            modifier = Modifier
                .padding(vertical = 8.dp)
                .padding(start = 6.dp)
        )
        Text(
            text = buildAnnotatedString {
                withStyle(
                    SpanStyle(
                        fontWeight = FontWeight.Bold,
                        textDecoration = TextDecoration.Underline
                    )
                ) {
                    append("Энтропия")
                }

                append("\t\t")

                withStyle(SpanStyle(color = MaterialTheme.colorScheme.error)) {
                    append(symbolWithCodes.entropy.round(2).toString())
                }

            },
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground.animate(),
            modifier = Modifier
                .padding(vertical = 8.dp)
                .padding(start = 6.dp)
        )


//        if (defaultMessage.isNotBlank() && encodedMessage.isNotEmpty()) {
//            arrayOf(
//                "Введённый текст: $defaultMessage",
//                "Закодированный текст: $encodedMessage"
//            ).forEach {
//                Text(
//                    text = it,
//                    style = MaterialTheme.typography.bodyLarge,
//                    color = MaterialTheme.colorScheme.onBackground.animate(),
//                    modifier = Modifier
//                        .padding(vertical = 8.dp)
//                        .padding(start = 6.dp)
//                )
//
//            }
//        }
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
    EncodingAppTheme {
        CodesList(
            symbolWithCodes = listOf(
                SymbolWithCode("A", 0.3, "01"),
                SymbolWithCode("B", 0.5, "1"),
                SymbolWithCode("C", 0.2, "00")
            ),
        )
    }
}