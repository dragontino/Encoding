package com.mathematics.encoding.presentation.view.screens

import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.rounded.ArrowBackIosNew
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mathematics.encoding.R
import com.mathematics.encoding.data.model.CodedSymbol
import com.mathematics.encoding.data.support.averageCodeLength
import com.mathematics.encoding.data.support.entropy
import com.mathematics.encoding.data.support.round
import com.mathematics.encoding.presentation.theme.EncodingAppTheme
import com.mathematics.encoding.presentation.theme.animate
import com.mathematics.encoding.presentation.view.RoundedCornersScreen
import com.mathematics.encoding.presentation.view.navigation.EncodingScreens


@ExperimentalAnimationApi
@ExperimentalFoundationApi
@ExperimentalMaterial3Api
@Composable
internal fun AnimatedVisibilityScope.ResultScreen(
    title: String,
    resultList: List<CodedSymbol>,
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
                modifier = Modifier
                    .padding(40.dp)
                    .align(Alignment.CenterHorizontally)
            )
        } else {
            CodesList(codedSymbols = resultList, defaultMessage, encodedMessage)
        }
    }
}



@ExperimentalFoundationApi
@Composable
internal fun CodesList(
    codedSymbols: List<CodedSymbol>,
    defaultMessage: String = "",
    encodedMessage: String = ""
) {
    val orientation = LocalConfiguration.current.orientation

    if (orientation == Configuration.ORIENTATION_PORTRAIT) {
        Column(
            modifier = Modifier
                .padding(vertical = 16.dp, horizontal = 8.dp)
                .fillMaxWidth(),
        ) {

            Row(
                horizontalArrangement = Arrangement.SpaceAround,
                modifier = Modifier.fillMaxWidth(),
            ) {
                InformationText(
                    name = stringResource(R.string.average_code_length),
                    value = codedSymbols.averageCodeLength.round(2).toString()
                )

                Spacer(Modifier.width(16.dp))

                InformationText(
                    name = stringResource(R.string.entropy),
                    value = codedSymbols.entropy.round(2).toString()
                )
            }

            if (defaultMessage.isNotBlank() && encodedMessage.isNotBlank()) {
                Row(
                    horizontalArrangement = Arrangement.SpaceAround,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    InformationText(
                        name = stringResource(R.string.default_msg),
                        value = defaultMessage
                    )

                    Spacer(Modifier.width(16.dp))

                    InformationText(
                        name = stringResource(R.string.encoded_msg),
                        value = encodedMessage
                    )
                }
            }


            ResultTable(
                rows = codedSymbols,
                borderColor = MaterialTheme.colorScheme.primary.animate(),
                modifier = Modifier.fillMaxWidth()
            )
        }
    } else {
        Row(
            horizontalArrangement = Arrangement.SpaceAround,
            modifier = Modifier
                .padding(horizontal = 8.dp, vertical = 16.dp)
                .fillMaxWidth()
        ) {
            ResultTable(
                rows = codedSymbols,
                borderColor = MaterialTheme.colorScheme.primary.animate(),
                modifier = Modifier.weight(1f)
            )

            Column(modifier = Modifier.weight(1f)) {
                Row(Modifier.fillMaxWidth()) {
                    InformationText(
                        name = stringResource(R.string.average_code_length),
                        value = codedSymbols.averageCodeLength.round(2).toString(),
                    )

                    Spacer(modifier = Modifier.width(16.dp))

                    InformationText(
                        name = stringResource(R.string.entropy),
                        value = codedSymbols.entropy.round(2).toString(),
                    )
                }

                if (defaultMessage.isNotBlank() && encodedMessage.isNotBlank()) {
                    Row(
                        horizontalArrangement = Arrangement.SpaceAround,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        InformationText(
                            name = stringResource(R.string.default_msg),
                            value = defaultMessage
                        )

                        Spacer(Modifier.width(16.dp))

                        InformationText(
                            name = stringResource(R.string.encoded_msg),
                            value = encodedMessage
                        )
                    }
                }
            }
        }
    }
}



@ExperimentalFoundationApi
@Composable
private fun ResultTable(rows: List<CodedSymbol>, borderColor: Color, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .border(
                width = 1.5.dp,
                color = borderColor.animate(),
                shape = MaterialTheme.shapes.small
            )
            .padding(4.dp)
    ) {
        TableRow("Символ", "Вероятность", "Код", fontSize = 17.sp)
        Divider(
            color = borderColor.animate(),
            thickness = 1.5.dp,
            modifier = Modifier
                .padding(vertical = 1.dp)
                .fillMaxWidth()
        )

        LazyColumn {
            items(rows) { symbol ->
                TableRow(
                    symbol.name,
                    symbol.probability.toString(),
                    symbol.code,
                    fontSize = 16.sp
                )
            }
        }
    }
}


@ExperimentalFoundationApi
@Composable
private fun TableRow(vararg items: String, fontSize: TextUnit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly,
        modifier = Modifier
            .animateContentSize(spring(stiffness = Spring.StiffnessVeryLow))
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




@Composable
private fun InformationText(name: String, value: String) {
    Text(
        text = buildAnnotatedString {
            withStyle(
                SpanStyle(fontWeight = FontWeight.Bold)
            ) {
                withStyle(ParagraphStyle(lineHeight = 7.sp)) {
                    append(name)
                }
            }

            append("\t\t")

            withStyle(SpanStyle(color = MaterialTheme.colorScheme.error)) {
                append(value)
            }

        },
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onBackground.animate(),
        modifier = Modifier
            .padding(vertical = 8.dp)
            .padding(start = 6.dp)
    )
}




@ExperimentalFoundationApi
@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun CodesListPreview() {
    EncodingAppTheme {
        CodesList(
            codedSymbols = listOf(
                CodedSymbol("A", 0.3, "01"),
                CodedSymbol("B", 0.5, "1"),
                CodedSymbol("C", 0.2, "00")
            ),
        )
    }
}