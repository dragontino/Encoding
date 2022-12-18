package com.mathematics.encoding.presentation.view

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ChatBubble
import androidx.compose.material.icons.rounded.Palette
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mathematics.encoding.R
import com.mathematics.encoding.data.model.Themes
import com.mathematics.encoding.presentation.theme.EncodingAppTheme
import com.mathematics.encoding.presentation.theme.animate

@Composable
fun ThemeDialog(
    theme: Themes,
    onDismiss: () -> Unit,
    updateTheme: (Themes) -> Unit
) {
    val tempTheme by remember { mutableStateOf(theme) }

    AlertDialog(
        onDismissRequest = {
            onDismiss()
            if (theme != tempTheme)
                updateTheme(tempTheme)
        },
        confirmButton = {
            TextButton(
                onClick = onDismiss,
                colors = ButtonDefaults.textButtonColors(
                    containerColor = Color.Transparent,
                    contentColor = MaterialTheme.colorScheme.primary.animate(),
                ),
            ) {
                Text("Сохранить", style = MaterialTheme.typography.bodyMedium)
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onDismiss()
                    if (theme != tempTheme)
                        updateTheme(tempTheme)
                },
                colors = ButtonDefaults.textButtonColors(
                    containerColor = Color.Transparent,
                    contentColor = MaterialTheme.colorScheme.primary.animate(),
                ),
            ) {
                Text("Отменить", style = MaterialTheme.typography.bodyMedium)
            }
        },
        title = {
            Row(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.background.animate())
                    .padding(top = 16.dp, bottom = 8.dp, start = 16.dp)
                    .fillMaxWidth(),
            ) {
                Text(
                    text = "Тема",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onBackground.animate(),
                    textAlign = TextAlign.Start,
                    modifier = Modifier.weight(2f),
                )

                Icon(
                    imageVector = Icons.Rounded.Palette,
                    contentDescription = "palette",
                    tint = MaterialTheme.colorScheme.primary.animate(),
                    modifier = Modifier
                        .scale(1.2f)
                        .padding(end = 12.dp),
                )
            }

            Divider(
                modifier = Modifier.fillMaxWidth(),
                color = colorResource(android.R.color.darker_gray),
                thickness = 1.2.dp,
            )
        },
        text = {
            Column(Modifier.verticalScroll(rememberScrollState())) {
                Themes(theme, updateTheme)
            }
        },
        shape = MaterialTheme.shapes.medium,
        modifier = Modifier
            .border(
                width = 1.2.dp,
                color = MaterialTheme.colorScheme.primary.animate(),
                shape = MaterialTheme.shapes.medium,
            ),
        containerColor = MaterialTheme.colorScheme.background.animate(),
        textContentColor = MaterialTheme.colorScheme.onBackground.animate(),
    )
}





@ExperimentalMaterialApi
@Composable
fun ConfirmDialog(
    text: String,
    closeDialog: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = closeDialog,
        dismissButton = {
            TextButton(
                onClick = closeDialog,
                colors = ButtonDefaults.textButtonColors(
                    containerColor = Color.Transparent,
                    contentColor = MaterialTheme.colorScheme.primary.animate()
                )
            ) {
                Text(text = "Отменить", style = MaterialTheme.typography.bodyMedium)
            }

        },
        confirmButton = {
            TextButton(
                onClick = {
                    closeDialog()
                    onConfirm()
                },
                colors = ButtonDefaults.textButtonColors(
                    contentColor = MaterialTheme.colorScheme.primary.animate(),
                    containerColor = Color.Transparent
                )
            ) {
                Text(text = "Продолжить", style = MaterialTheme.typography.bodyMedium)
            }
        },
        title = {
            Text(
                text = "Подтверждение действия",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onBackground.animate()
            )
        },
        text = {
            Text(
                text = text,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground.animate()
            )
        },
        containerColor = MaterialTheme.colorScheme.background.animate(),
        textContentColor = MaterialTheme.colorScheme.onBackground.animate(),
        titleContentColor = MaterialTheme.colorScheme.onBackground.animate()
    )
}



@Composable
fun FeedbackDialog(
    closeDialog: () -> Unit,
    content: @Composable ColumnScope.() -> Unit,
) {
    AlertDialog(
        onDismissRequest = closeDialog,
        confirmButton = {
            TextButton(
                onClick = closeDialog,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = MaterialTheme.colorScheme.primary.animate()
                )
            ) {
                Text(
                    text = stringResource(R.string.close),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.verticalScroll(rememberScrollState())
            ) {
                content()
            }
        },
        title = {
            Text(
                text = stringResource(R.string.feedback),
                style = MaterialTheme.typography.labelLarge
            )
        },
        icon = {
            Icon(
                imageVector = Icons.Rounded.ChatBubble,
                contentDescription = "feedback",
                modifier = Modifier.scale(1.3f)
            )
        },
        containerColor = MaterialTheme.colorScheme.background.animate(),
        textContentColor = MaterialTheme.colorScheme.onBackground.animate(),
        iconContentColor = MaterialTheme.colorScheme.primary.animate(),
        titleContentColor = MaterialTheme.colorScheme.onBackground.animate()
    )
}


@Composable
internal fun ColumnScope.FeedbackButton(title: String, onClick: () -> Unit) {
    OutlinedButton(
        onClick = onClick,
        shape = RoundedCornerShape(15.dp),
        border = BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.primary.animate()
        ),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = MaterialTheme.colorScheme.onBackground.animate()
        ),
        modifier = Modifier
            .align(Alignment.CenterHorizontally)
            .padding(4.dp)
            .fillMaxWidth()
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium,
        )
    }
}




@ExperimentalMaterialApi
@Preview
@Composable
private fun ThemeDialogPreview() {
    EncodingAppTheme {
        ThemeDialog(theme = Themes.Light, onDismiss = {}, updateTheme = {})
    }
}



@ExperimentalMaterialApi
@Preview
@Composable
private fun ConfirmDialogPreview() {
    EncodingAppTheme {
        ConfirmDialog(
            text = "Вы собираетевсыдль дцуьсдлывьс ывюсь",
            closeDialog = {},
            onConfirm = {}
        )
    }
}