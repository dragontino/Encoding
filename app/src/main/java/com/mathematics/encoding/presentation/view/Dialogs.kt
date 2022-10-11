package com.mathematics.encoding.presentation.view

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Palette
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mathematics.encoding.presentation.model.Themes
import com.mathematics.encoding.presentation.theme.animate
import com.mathematics.encoding.presentation.theme.mediumCornerSize


//@ExperimentalMaterialApi
//class DialogState(
//    initialValue: DialogValue = DialogValue.Hidden,
//    animationSpec: AnimationSpec<Float> = spring(stiffness = stiffness)
//) : SwipeableState<DialogValue>(
//    initialValue,
//    animationSpec
//) {
//    suspend fun show() {
//        animateTo(DialogValue.Showing)
//    }
//
//    suspend fun hide() {
//        animateTo(DialogValue.Hidden)
//    }
//
//    val isShowing: Boolean = currentValue == DialogValue.Showing
//
//    companion object {
//        const val stiffness = Spring.StiffnessLow
//
//        fun saver(animationSpec: AnimationSpec<Float>): Saver<DialogState, *> = Saver(
//            save = { it.currentValue },
//            restore = {
//                DialogState(
//                    initialValue = it,
//                    animationSpec = animationSpec
//                )
//            }
//        )
//    }
//}
//
//
//enum class DialogValue {
//    Showing,
//    Hidden
//}
//
//
//
//@ExperimentalMaterialApi
//@Composable
//fun rememberDialogState(
//    initialValue: DialogValue = DialogValue.Hidden,
//    stiffness: Float = DialogState.stiffness
//): DialogState =
//    rememberDialogState(initialValue, spring(stiffness))
//
//
//@ExperimentalMaterialApi
//@Composable
//fun rememberDialogState(
//    initialValue: DialogValue,
//    animationSpec: AnimationSpec<Float>
//): DialogState {
//    return rememberSaveable(
//        initialValue,
//        animationSpec,
//        saver = DialogState.saver(animationSpec)
//    ) {
//        DialogState(initialValue, animationSpec)
//    }
//}




@ExperimentalMaterialApi
@Composable
fun ThemeDialog(
    theme: Themes,
    onDismiss: () -> Unit,
    updateTheme: (Themes) -> Unit
) {
    val tempTheme by remember { mutableStateOf(theme) }

    AlertDialog(onDismissRequest = {
        onDismiss()
        if (theme != tempTheme)
            updateTheme(tempTheme)
    },
        confirmButton = {
            TextButton(
                onClick = onDismiss,
                colors = ButtonDefaults.textButtonColors(
                    containerColor = Color.Transparent,
                    contentColor = MaterialTheme.colorScheme.primary.animate()
                )
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
                    contentColor = MaterialTheme.colorScheme.primary.animate()
                )
            ) {
                Text("Отменить", style = MaterialTheme.typography.bodyMedium)
            }
        },
        title = {
            Row(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.background.animate())
                    .padding(top = 16.dp, bottom = 8.dp, start = 16.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    text = "Тема",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onBackground.animate(),
                    textAlign = TextAlign.Start,
                    modifier = Modifier.weight(2f)
                )

                Icon(
                    imageVector = Icons.Rounded.Palette,
                    contentDescription = "palette",
                    tint = MaterialTheme.colorScheme.primary.animate(),
                    modifier = Modifier
                        .scale(1.2f)
                        .padding(end = 12.dp)
                )
            }

            Divider(
                modifier = Modifier.fillMaxWidth(),
                color = colorResource(android.R.color.darker_gray),
                thickness = 1.2.dp
            )
        },
        text = {
            Column {
                Themes(theme, updateTheme)
            }
        },
        shape = RoundedCornerShape(mediumCornerSize),
        modifier = Modifier
            .border(
                width = 1.2.dp,
                color = MaterialTheme.colorScheme.primary.animate(),
                shape = RoundedCornerShape(mediumCornerSize)
            ),
        containerColor = MaterialTheme.colorScheme.background.animate(),
        textContentColor = MaterialTheme.colorScheme.onBackground.animate()
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
        }
    )
}




@ExperimentalMaterialApi
@Preview
@Composable
private fun ThemeDialogPreview() {
    ThemeDialog(theme = Themes.Light, onDismiss = {}, updateTheme = {})
}



@ExperimentalMaterialApi
@Preview
@Composable
private fun ConfirmDialogPreview() {
    ConfirmDialog(
        text = "Вы собираетевсыдль дцуьсдлывьс ывюсь",
        closeDialog = {},
        onConfirm = {}
    )
}