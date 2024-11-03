package com.emirsansar.hesapptracker.view.mainScreens.sharedComponents

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.emirsansar.hesapptracker.R
import com.emirsansar.hesapptracker.ui.theme.DarkThemeColors
import com.emirsansar.hesapptracker.ui.theme.LightThemeColors

@Composable
fun CustomTopBar(
    title: String,
    isDarkMode: Boolean,
    onBackPressed: (() -> Unit)? = null,
    onSortButtonClicked: (() -> Unit)? = null,
    onAddButtonClicked: (() -> Unit)? = null
) {
    TopAppBar(
        title = {
            Text(
                text = title,
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (isDarkMode) Color.White else Color.Black
            )
        },
        navigationIcon = onBackPressed?.let {
            {
                IconButton(onClick = it) {
                    Icon(
                        Icons.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = if (isDarkMode) Color.White else Color.Black
                    )
                }
            }
        },
        actions = {
            onSortButtonClicked?.let {
                IconButton(onClick = it) {
                    Icon(
                        painter = painterResource(id = R.drawable.icon_sorting),
                        contentDescription = "Sort Subscription Button",
                        tint = if (isDarkMode) Color.White else Color.Black
                    )
                }
            }
            onAddButtonClicked?.let {
                IconButton(onClick = it) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add Custom Plan Button",
                        tint = if (isDarkMode) Color.White else Color.Black
                    )
                }
            }
        },
        backgroundColor = if (isDarkMode) DarkThemeColors.BarColor else LightThemeColors.BarColor,
        modifier = Modifier.fillMaxWidth()
    )
}
