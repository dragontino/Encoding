package com.mathematics.encoding.presentation.view.mainscreen

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mathematics.encoding.R
import com.mathematics.encoding.presentation.theme.animate
import com.mathematics.encoding.presentation.theme.mediumCornerSize

@ExperimentalAnimationApi
@ExperimentalMaterial3Api
@Composable
internal fun AnimatedVisibilityScope.TextInput(
    considerGap: Boolean,
    updateConsiderGap: (Boolean) -> Unit,
    onValueChange: (value: Any) -> Unit,
    calculateCodes: (text: String) -> Unit
) {
    var text by remember { mutableStateOf("") }

    Column(Modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = text,
            onValueChange = {
                onValueChange(it)
                text = it
            },
            placeholder = {
                Text(
                    "Введите текст...",
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            shape = RoundedCornerShape(mediumCornerSize),
            textStyle = MaterialTheme.typography.bodyMedium,
            colors = TextFieldDefaults.outlinedTextFieldColors(
                textColor = MaterialTheme.colorScheme.onBackground.animate(),
                placeholderColor = MaterialTheme.colorScheme.onBackground.animate(),
                cursorColor = MaterialTheme.colorScheme.primary.animate(),
                focusedBorderColor = MaterialTheme.colorScheme.primary.animate(),
                unfocusedBorderColor = MaterialTheme.colorScheme.onBackground.animate()
            ),
            modifier = Modifier
                .animateEnterExit(
                    enter = scaleIn(spring(stiffness = Spring.StiffnessLow)),
                    exit = scaleOut(spring(stiffness = Spring.StiffnessLow))
                )
                .padding(bottom = 4.dp, top = 16.dp)
                .padding(horizontal = 16.dp)
                .fillMaxWidth()
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(bottom = 16.dp, top = 4.dp)
                .padding(horizontal = 16.dp)
                .fillMaxWidth(),
        ) {
            Checkbox(
                checked = considerGap,
                onCheckedChange = {
                    onValueChange(it)
                    updateConsiderGap(it)
                },
                colors = CheckboxDefaults.colors(
                    checkedColor = MaterialTheme.colorScheme.primary.animate(),
                    checkmarkColor = MaterialTheme.colorScheme.onPrimary.animate(),
                    uncheckedColor = MaterialTheme.colorScheme.onBackground.animate()
                ),
                modifier = Modifier
                    .scale(1.1f)
                    .padding(start = 8.dp)
            )

            Text(
                "Учитывать пробел",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.onBackground.animate(),
                    fontSize = 17.sp
                ),
                modifier = Modifier.padding(start = 6.dp)
            )
        }

        TextButton(
            text = stringResource(R.string.calculate_codes),
            shape = RoundedCornerShape(mediumCornerSize),
            onClick = { calculateCodes(text) },
            modifier = Modifier
                .padding(horizontal = 8.dp, vertical = 16.dp)
                .fillMaxWidth()
        )
    }
}


@Preview
@Composable
private fun TextInputPreview() {

}