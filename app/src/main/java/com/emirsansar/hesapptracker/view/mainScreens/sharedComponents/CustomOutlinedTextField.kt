package com.emirsansar.hesapptracker.view.mainScreens.sharedComponents

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomOutlinedTextField(
    value: String,
    label: String,
    error: Boolean,
    onValueChange: (String) -> Unit,
    isDarkMode: Boolean,
    keyboardType: KeyboardType = KeyboardType.Text,
    modifier: Modifier? = null
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = modifier ?: Modifier.fillMaxWidth().padding(bottom = 8.dp),
        colors = TextFieldDefaults.outlinedTextFieldColors(
            textColor = if (isDarkMode) Color.White else Color.Black,
            focusedBorderColor = if (error) Color.Red else MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = if (error) Color.Red
            else if (isDarkMode) Color.Gray else Color.DarkGray,
            focusedLabelColor = if (error) Color.Red
            else if (isDarkMode) Color.LightGray else Color.DarkGray,
            unfocusedLabelColor = if (error) Color.Red
            else if (isDarkMode) Color.LightGray else Color.DarkGray,
        ),
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomOutlinedTextFieldForAuthScreens(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    isDarkMode: Boolean,
    modifier: Modifier = Modifier,
    visualTransformation: PasswordVisualTransformation? = null,
    isError: Boolean? = null
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        visualTransformation = visualTransformation ?: VisualTransformation.None,
        modifier = modifier,
        colors = TextFieldDefaults.outlinedTextFieldColors(
            textColor = if (isDarkMode) Color.White else Color.Black,
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = if (isError != null && isError) Color.Red
            else if (isDarkMode) Color.Gray else Color.DarkGray,
            focusedLabelColor = if (isDarkMode) Color.LightGray else Color.DarkGray,
            unfocusedLabelColor = if (isDarkMode) Color.LightGray else Color.DarkGray,
            cursorColor = if (isDarkMode) Color.LightGray else Color.DarkGray
        ),
        textStyle = TextStyle(fontSize = 16.sp)
    )
}