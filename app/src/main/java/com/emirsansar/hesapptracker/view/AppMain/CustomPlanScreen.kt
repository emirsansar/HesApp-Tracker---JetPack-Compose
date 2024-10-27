package com.emirsansar.hesapptracker.view.AppMain

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material.BottomSheetScaffold
import androidx.compose.material.BottomSheetScaffoldState
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.rememberBottomSheetScaffoldState
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.emirsansar.hesapptracker.manager.AppManager
import com.emirsansar.hesapptracker.model.Plan
import com.emirsansar.hesapptracker.ui.theme.DarkThemeColors
import com.emirsansar.hesapptracker.ui.theme.HesAppTrackerTheme
import com.emirsansar.hesapptracker.ui.theme.LightThemeColors
import com.emirsansar.hesapptracker.viewModel.UserSubscriptionViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class CustomPlanScreen : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HesAppTrackerTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color(0xFFe3e5e6)
                ) {
                    val serviceName = intent.getStringExtra("serviceName") ?: ""

                    CustomPlanScreenView(serviceName, onBackPressed = { finish() })
                }
            }
        }
    }
}

@Composable
fun CustomPlanScreenView(
    serviceName: String,
    userSubVM: UserSubscriptionViewModel = UserSubscriptionViewModel(),
    onBackPressed: () -> Unit,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    val scaffoldState = rememberBottomSheetScaffoldState()
    val context = LocalContext.current
    val appManager = AppManager.getInstance(context)

    var planName by remember { mutableStateOf("") }
    var planNameError by remember { mutableStateOf(false) }

    var planPrice by remember { mutableStateOf("") }
    var planPriceError by remember { mutableStateOf(false) }

    var personCount by remember { mutableStateOf("") }
    var personCountError by remember { mutableStateOf(false) }

    var bottomSheetMessage by remember { mutableStateOf("") }
    var bottomSheetSuccess by remember { mutableStateOf(false) }


    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        topBar = {
            TopBarCustomPlanScreen(onBackPressed, appManager.isDarkMode.value)
        },
        sheetContent = {
            BottomSheetContent(
                bottomSheetMessage,
                bottomSheetSuccess,
                coroutineScope,
                scaffoldState,
                onFinish = { onBackPressed },
                appManager.isDarkMode.value
            )
        },
        sheetPeekHeight = 0.dp,
        content = { innerPadding ->
            BodyContent(
                userSubVM = userSubVM,
                serviceName = serviceName,
                planName = planName,
                planPrice = planPrice,
                personCount = personCount,
                planNameError = planNameError,
                planPriceError = planPriceError,
                personCountError = personCountError,
                onPlanNameChange = { value ->
                    planName = value
                    planNameError = value.isEmpty()
                },
                onPlanPriceChange = { value ->
                    planPrice = value
                    planPriceError = value.isEmpty()
                },
                onPersonCountChange = { value ->
                    personCount = value
                    personCountError = value.isEmpty()
                },
                onBottomSheetMessageChange = { value ->
                    bottomSheetMessage = value
                },
                onBottomSheetSuccessChange = { value ->
                    bottomSheetSuccess = value
                },
                modifier = modifier,
                innerPadding = innerPadding,
                coroutineScope = coroutineScope,
                scaffoldState = scaffoldState,
                appManager.isDarkMode.value
            )
        },
        backgroundColor = if (appManager.isDarkMode.value) DarkThemeColors.BackgroundColor else LightThemeColors.BackgroundColor,
    )

}

@Composable
private fun TopBarCustomPlanScreen(onBackPressed: () -> Unit, isDarkMode: Boolean) {
    TopAppBar(
        title = { Text("Plans", fontSize = 20.sp, fontWeight = FontWeight.Medium,
            color = if (isDarkMode) Color.White else Color.Black) },
        navigationIcon = {
            IconButton(onClick = { onBackPressed() }) {
                Icon(Icons.Filled.ArrowBack, contentDescription = "Back",
                    tint = if (isDarkMode) Color.White else Color.Black)
            }
        },
        backgroundColor = if (isDarkMode) DarkThemeColors.BarColor else LightThemeColors.BarColor,
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
private fun BodyContent(
    userSubVM: UserSubscriptionViewModel,
    serviceName: String,
    planName: String,
    planPrice: String,
    personCount: String,
    planNameError: Boolean,
    planPriceError: Boolean,
    personCountError: Boolean,
    onPlanNameChange: (String) -> Unit,
    onPlanPriceChange: (String) -> Unit,
    onPersonCountChange: (String) -> Unit,
    onBottomSheetMessageChange: (String) -> Unit,
    onBottomSheetSuccessChange: (Boolean) -> Unit,
    modifier: Modifier,
    innerPadding: PaddingValues,
    coroutineScope: CoroutineScope,
    scaffoldState: BottomSheetScaffoldState,
    isDarkMode: Boolean
) {
    val focusManager = LocalFocusManager.current

    var showDialog by remember { mutableStateOf(false) }
    var alertMessage by remember { mutableStateOf("") }
    var showAlertError by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(innerPadding)
            .padding(16.dp)
            .clickable(
                onClick = { focusManager.clearFocus() },
                indication = null,
                interactionSource = remember { MutableInteractionSource() }),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(top = 10.dp ,bottom = 16.dp))
        {
            Text(
                text = "$serviceName's",
                fontSize = 22.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (isDarkMode) Color.White else Color.Black
            )
            Text(
                text = "Custom Plan:",
                fontSize = 21.sp,
                fontWeight = FontWeight.Medium,
                color = if (isDarkMode) Color.White else Color.Black
            )
        }

        CustomOutlinedTextField(
            value = planName,
            label = "Plan Name",
            error = planNameError,
            onValueChange = onPlanNameChange,
            isDarkMode
        )

        CustomOutlinedTextField(
            value = planPrice,
            label = "Plan Price",
            error = planPriceError,
            onValueChange = onPlanPriceChange,
            isDarkMode,
            keyboardType = KeyboardType.Decimal,
        )

        CustomOutlinedTextField(
            value = personCount,
            label = "Person Count",
            error = personCountError,
            onValueChange = onPersonCountChange,
            isDarkMode,
            keyboardType = KeyboardType.Number
        )

        Button(
            onClick = {
                focusManager.clearFocus()

                val price = planPrice.toDoubleOrNull()
                val count = personCount.toIntOrNull()

                if (price != null && count != null) {
                    alertMessage = "-Service: $serviceName\n-Plan: $planName\n-Price: $planPrice\n-Persons: $personCount"
                    showDialog = true
                } else {
                    alertMessage = "Please check the entered values. Plan price must be a valid number and person count must be an integer."
                    showAlertError = true
                }
            },
            enabled = !planNameError && !planPriceError && !personCountError,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Add Plan")
        }

        // Confirmation Dialog
        if (showDialog) {
            ConfirmationDialog(
                onDismissRequest = { showDialog = false },
                onConfirm = {
                    showDialog = false
                    handleAddPlan(
                        planName = planName,
                        planPrice = planPrice,
                        serviceName = serviceName,
                        personCount = personCount,
                        userSubVM = userSubVM,
                        coroutineScope = coroutineScope,
                        onBottomSheetMessageChange = onBottomSheetMessageChange,
                        onBottomSheetSuccessChange = onBottomSheetSuccessChange,
                        scaffoldState = scaffoldState
                    )
                },
                onDismiss = { showDialog = false },
                alertMessage = "Do you want to add this plan?\n$alertMessage",
                isDarkMode = isDarkMode
            )
        }

        // Error Alert Dialog
        if (showAlertError) {
            ErrorDialog(
                onDismissRequest = { showAlertError = false },
                alertMessage = alertMessage
            )
        }

        if (planNameError || planPriceError || personCountError) {
            Text(
                text = "Please fill in all fields.",
                color = Color.Red, fontSize = 12.sp, modifier = Modifier.padding(top = 8.dp)
            )
        }

    }
}

@Composable
private fun BottomSheetContent(
    bottomSheetMessage: String,
    isSuccess: Boolean,
    coroutineScope: CoroutineScope,
    scaffoldState: BottomSheetScaffoldState,
    onFinish: () -> Unit,
    isDarkMode: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = if (isDarkMode) DarkThemeColors.DrawerContentColor
                                else LightThemeColors.DrawerContentColor )
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = if (isSuccess) "Success" else "Error",
            fontSize = 22.sp,
            color = if (isSuccess) Color.Green else Color.Red,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Text(
            text = bottomSheetMessage,
            fontSize = 16.sp,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        TextButton(onClick = {
            coroutineScope.launch {
                scaffoldState.bottomSheetState.collapse()
                onFinish()
            }
        }) {
            Text(text = "OK", fontSize = 16.sp, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
private fun ConfirmationDialog(
    onDismissRequest: () -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    alertMessage: String,
    isDarkMode: Boolean
) {
    AlertDialog(
        onDismissRequest = { onDismissRequest() },
        confirmButton = {
            TextButton(onClick = {
                onConfirm()
            }) {
                Text(
                    text = "Confirm",
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Green
                )
            }
        },
        dismissButton = {
            TextButton(onClick = { onDismiss() }) {
                Text(
                    text = "Cancel",
                    fontWeight = FontWeight.Medium,
                    color = Color.Red
                )
            }
        },
        title = {
            Text(
                text = "Confirm Adding Plan",
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium
            )
        },
        text = { Text(alertMessage) },
        shape = RoundedCornerShape(16.dp),
        containerColor = if (isDarkMode) DarkThemeColors.DrawerContentColor
                         else LightThemeColors.DrawerContentColor
    )

}

@Composable
private fun ErrorDialog(
    onDismissRequest: () -> Unit,
    alertMessage: String
) {
    AlertDialog(
        onDismissRequest = { onDismissRequest() },
        confirmButton = {
            TextButton(onClick = { onDismissRequest() }) {
                Text(
                    text = "OK",
                    fontWeight = FontWeight.SemiBold
                )
            }
        },
        title = {
            Text(
                text = "Invalid Input",
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium
            )
        },
        text = {
            Text(alertMessage)
        }
    )
}

private fun handleAddPlan(
    planName: String,
    planPrice: String,
    serviceName: String,
    personCount: String,
    userSubVM: UserSubscriptionViewModel,
    coroutineScope: CoroutineScope,
    onBottomSheetMessageChange: (String) -> Unit,
    onBottomSheetSuccessChange: (Boolean) -> Unit,
    scaffoldState: BottomSheetScaffoldState
) {
    val plan = Plan(planName, planPrice.toDouble())

    userSubVM.addPlanToUserOnFirestore(serviceName, plan, personCount.toInt()) { success ->
        coroutineScope.launch {
            if (success) {
                onBottomSheetMessageChange("Subscription successfully added.")
                onBottomSheetSuccessChange(true)
            } else {
                onBottomSheetMessageChange("An error occurred while adding the subscription.")
                onBottomSheetSuccessChange(false)
            }
            scaffoldState.bottomSheetState.expand()
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    HesAppTrackerTheme {
        CustomPlanScreen()
    }
}