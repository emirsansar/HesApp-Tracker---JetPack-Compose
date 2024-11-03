package com.emirsansar.hesapptracker.view.mainScreens.userSubscriptionScreen.components

import androidx.compose.foundation.layout.width
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ExposedDropdownMenuBox
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.emirsansar.hesapptracker.R
import com.emirsansar.hesapptracker.view.mainScreens.userSubscriptionScreen.SortType

// Composable function for SortPicker.
@OptIn(ExperimentalMaterialApi::class)
@Composable
internal fun SortPicker(
    sortType: SortType,
    onSortTypeChanged: (SortType) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedButton(
            onClick = { expanded = true },
            modifier = Modifier.width(250.dp)
        ) {
            when (sortType) {
                SortType.Default -> Text(text = stringResource(id = R.string.sort_option))
                SortType.PriceAscending -> Text(text = stringResource(id = R.string.priceAscending))
                SortType.PriceDescending -> Text(text = stringResource(id = R.string.priceDescending))
                SortType.Alphabetical -> Text(text = stringResource(id = R.string.alphabetically))
            }
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(onClick = {
                onSortTypeChanged(SortType.PriceAscending)
                expanded = false
            }) {
                Text(stringResource(id = R.string.priceAscending))
            }
            DropdownMenuItem(onClick = {
                onSortTypeChanged(SortType.PriceDescending)
                expanded = false
            }) {
                Text(stringResource(id = R.string.priceDescending))
            }
            DropdownMenuItem(onClick = {
                onSortTypeChanged(SortType.Alphabetical)
                expanded = false
            }) {
                Text(stringResource(id = R.string.alphabetically))
            }
        }
    }
}