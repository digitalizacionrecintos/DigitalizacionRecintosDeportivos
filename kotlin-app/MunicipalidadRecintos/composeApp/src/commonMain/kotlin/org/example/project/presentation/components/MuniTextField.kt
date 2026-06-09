package org.example.project.presentation.components

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.VisualTransformation
import org.example.project.presentation.theme.MuniColors
import org.example.project.presentation.theme.MuniShapes

@Composable
fun MuniTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    modifier: Modifier = Modifier
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text(placeholder, color = MuniColors.lightGray) },
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        visualTransformation = visualTransformation,
        keyboardOptions = keyboardOptions,
        singleLine = true,
        shape = MuniShapes.textField,
        colors =
            TextFieldDefaults.colors(
                focusedContainerColor = MuniColors.surfaceCard,
                unfocusedContainerColor = MuniColors.surfaceCard.copy(alpha = 0.9f),
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                cursorColor = MuniColors.primaryBlue,
                focusedTextColor = MuniColors.primaryBlue,
                unfocusedTextColor = MuniColors.darkGray
            ),
        modifier = modifier
    )
}
