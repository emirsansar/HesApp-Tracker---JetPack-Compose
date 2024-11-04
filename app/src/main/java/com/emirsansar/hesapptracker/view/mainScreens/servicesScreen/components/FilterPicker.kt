package com.emirsansar.hesapptracker.view.mainScreens.servicesScreen.components

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material.AlertDialog
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ExposedDropdownMenuBox
import androidx.compose.material.ExposedDropdownMenuDefaults
import androidx.compose.material.RadioButton
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.emirsansar.hesapptracker.R
import com.emirsansar.hesapptracker.ui.theme.DarkThemeColors
import com.emirsansar.hesapptracker.ui.theme.LightThemeColors

enum class FilterType {
    All,
    Shopping,
    SeriesMovies,
    SelfDevelopment,
    Music,
    Game,
    Sport,
    Storage
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
internal fun FilterPicker(
    selectedType: FilterType,
    onFilterTypeChanged: (FilterType) -> Unit,
    onConfirm: () -> Unit,
    onDismissRequest: () -> Unit,
    isDarkMode: Boolean,
    context: Context
) {
    var selectedText by remember { mutableStateOf(getFilterTypeString(selectedType, context)) }
    var expandedMenu by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text(text = stringResource(id = R.string.label_filter_options), fontSize = 16.sp,
            color = if (isDarkMode) Color.White else Color.Black)
        },
        text = {
            Column() {
                ExposedDropdownMenuBox(
                    expanded = expandedMenu,
                    onExpandedChange = {  expandedMenu = !expandedMenu }
                ) {
                    DropdownTextField(
                        value = selectedText,
                        expanded = expandedMenu,
                        isDarkMode = isDarkMode
                    )

                    ExposedDropdownMenu(
                        expanded = expandedMenu,
                        onDismissRequest = { expandedMenu = false },
                        modifier = Modifier
                            .heightIn(max = 220.dp)
                            .background(if (isDarkMode) Color.LightGray else Color.White)
                    ) {
                        FilterType.values().forEach { type ->
                            DropdownMenuItem(
                                onClick = {
                                    onFilterTypeChanged(type)
                                    selectedText = getFilterTypeString(type, context)
                                    expandedMenu = false
                                },
                            ) {
                                FilterItem(
                                    type = type,
                                    selectedType = selectedType,
                                    onFilterTypeChanged = {
                                        onFilterTypeChanged(type)
                                        selectedText = getFilterTypeString(type, context)
                                        expandedMenu = false
                                    },
                                    context = context
                                )
                            }
                        }
                    }
                }
            }
        },
        backgroundColor = if (isDarkMode) DarkThemeColors.BackgroundColor else LightThemeColors.BackgroundColor,
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(stringResource(id = R.string.button_confirm), fontSize = 15.sp, color = Color.Green)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text(stringResource(id = R.string.button_cancel), fontSize = 14.sp, color = Color.Red)
            }
        }
    )

}

@Composable
private fun FilterItem(
    type: FilterType,
    selectedType: FilterType,
    onFilterTypeChanged: (FilterType) -> Unit,
    context: Context
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
    ) {
        RadioButton(
            selected = type == selectedType,
            onClick = { onFilterTypeChanged(type) }
        )
        
        Text(
            text = getFilterTypeString(type, context),
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun DropdownTextField(
    value: String,
    expanded: Boolean,
    isDarkMode: Boolean
) {
    TextField(
        readOnly = true,
        value = value,
        onValueChange = {},
        trailingIcon = {
            DropdownIcon(
                expanded = expanded,
                isDarkMode = isDarkMode
            )
        },
        colors = ExposedDropdownMenuDefaults.textFieldColors(
            textColor = if (isDarkMode) Color.White else Color.Black,
            focusedIndicatorColor = Color.Cyan
        ),
        textStyle = TextStyle(fontSize = 15.sp)
    )
}

@Composable
private fun DropdownIcon(
    expanded: Boolean,
    isDarkMode: Boolean
) {
    Icon(
        imageVector = if (expanded) Icons.Default.KeyboardArrowUp
                      else Icons.Default.KeyboardArrowDown,
        contentDescription = "Dropdown Icon",
        tint = if (isDarkMode) Color.White else Color.Black
    )
}


// Functions:

// Returns the string resource associated with each FilterType.
fun getFilterTypeString(type: FilterType, context: Context): String {
    return when (type) {
        FilterType.Shopping -> context.getString(R.string.Shopping)
        FilterType.SeriesMovies -> context.getString(R.string.SeriesMovies)
        FilterType.SelfDevelopment -> context.getString(R.string.SelfDevelopment)
        FilterType.Music -> context.getString(R.string.Music)
        FilterType.Game -> context.getString(R.string.Game)
        FilterType.Sport -> context.getString(R.string.Sport)
        FilterType.Storage -> context.getString(R.string.Storage)
        FilterType.All -> context.getString(R.string.All)
    }
}
