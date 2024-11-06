package com.emirsansar.hesapptracker.view.mainScreens.customPlanScreen

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.BottomSheetScaffold
import androidx.compose.material.BottomSheetScaffoldState
import androidx.compose.material.rememberBottomSheetScaffoldState
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.emirsansar.hesapptracker.R
import com.emirsansar.hesapptracker.manager.AppManager
import com.emirsansar.hesapptracker.model.Plan
import com.emirsansar.hesapptracker.ui.theme.DarkThemeColors
import com.emirsansar.hesapptracker.ui.theme.HesAppTrackerTheme
import com.emirsansar.hesapptracker.ui.theme.LightThemeColors
import com.emirsansar.hesapptracker.view.mainScreens.editSubscriptionScreen.isValidPriceInput
import com.emirsansar.hesapptracker.view.mainScreens.customPlanScreen.components.AddCustomPlanDialog
import com.emirsansar.hesapptracker.view.mainScreens.customPlanScreen.components.BottomSheetContentForPlansScreen
import com.emirsansar.hesapptracker.view.mainScreens.sharedComponents.CustomOutlinedTextField
import com.emirsansar.hesapptracker.view.mainScreens.sharedComponents.CustomTopBar
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
            CustomTopBar(
                title = stringResource(id = R.string.label_plans),
                isDarkMode = appManager.isDarkMode.value,
                onBackPressed = onBackPressed
            )
        },
        sheetContent = {
            sequenceOf(
                BottomSheetContentForPlansScreen(
                    isSuccess = bottomSheetSuccess,
                    coroutineScope = coroutineScope,
                    scaffoldState = scaffoldState,
                    onFinish = { onBackPressed },
                    isDarkMode = appManager.isDarkMode.value
                )
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
                onPlanNameChange = { newValue ->
                    planName = newValue
                    planNameError = newValue.isEmpty()
                },
                onPlanPriceChange = { newValue ->
                    if (isValidPriceInput(newValue)){
                        planPrice = newValue
                    }
                    planPriceError = newValue.isEmpty()
                },
                onPersonCountChange = { newValue ->
                    if (newValue.all { it.isDigit() }) {
                        personCount = newValue
                    }
                    personCountError = newValue.isEmpty()
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
                context = context,
                isDarkMode = appManager.isDarkMode.value
            )
        },
        backgroundColor = if (appManager.isDarkMode.value) DarkThemeColors.BackgroundColor else LightThemeColors.BackgroundColor,
    )

}

// Components:

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
    context: Context,
    isDarkMode: Boolean
) {
    val focusManager = LocalFocusManager.current

    var showConfirmationDialog by remember { mutableStateOf(false) }
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
                text = serviceName,
                fontSize = 23.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (isDarkMode) Color.White else Color.Black
            )

            Text(
                text = stringResource(id = R.string.label_custom_plan),
                fontSize = 22.sp,
                fontWeight = FontWeight.Medium,
                color = if (isDarkMode) Color.White else Color.Black,
                modifier = Modifier.padding(top = 5.dp)
            )
        }

        CustomOutlinedTextField(
            value = planName,
            label = stringResource(id = R.string.label_plan_name),
            error = planNameError,
            onValueChange = onPlanNameChange,
            isDarkMode = isDarkMode,
            modifier = Modifier.fillMaxWidth(0.95f).padding(bottom = 8.dp)
        )

        CustomOutlinedTextField(
            value = planPrice,
            label = stringResource(id = R.string.label_plan_price),
            error = planPriceError,
            onValueChange = onPlanPriceChange,
            isDarkMode = isDarkMode,
            keyboardType = KeyboardType.Decimal,
            modifier = Modifier.fillMaxWidth(0.95f).padding(bottom = 8.dp)
        )

        CustomOutlinedTextField(
            value = personCount,
            label = stringResource(id = R.string.label_user_count),
            error = personCountError,
            onValueChange = onPersonCountChange,
            isDarkMode = isDarkMode,
            keyboardType = KeyboardType.Number,
            modifier = Modifier.fillMaxWidth(0.95f).padding(bottom = 8.dp)
        )

        Button(
            onClick = {
                focusManager.clearFocus()

                val price = planPrice.replace(',', '.').toDoubleOrNull()
                val count = personCount.toIntOrNull()

                if (price != null && count != null) {
                    alertMessage = context.getString(R.string.text_plan_details_with_person_count, planName, price.toString(), count.toString())
                    showConfirmationDialog = true
                } else {
                    alertMessage = context.getString(R.string.label_error_invalid_format)
                    showAlertError = true
                }
            },
            enabled = !planNameError && !planPriceError && !personCountError,
            modifier = Modifier.fillMaxWidth(0.95f)
        ) {
            Text(text = stringResource(id = R.string.button_add))
        }

        if (planNameError || planPriceError || personCountError) {
            Text(
                text = stringResource(id = R.string.error_fill_all_fields),
                color = Color.Red, fontSize = 12.sp, modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
    
    if (showConfirmationDialog) {
        AddCustomPlanDialog(
            onDismissRequest = { showConfirmationDialog = false },
            onConfirm = {
                showConfirmationDialog = false
                handleAddPlan(
                    planName = planName,
                    planPrice = planPrice,
                    serviceName = serviceName,
                    personCount = personCount,
                    userSubVM = userSubVM,
                    context = context,
                    coroutineScope = coroutineScope,
                    onBottomSheetMessageChange = onBottomSheetMessageChange,
                    onBottomSheetSuccessChange = onBottomSheetSuccessChange,
                    scaffoldState = scaffoldState
                )
            },
            onDismiss = { showConfirmationDialog = false },
            alertMessage = alertMessage,
            isDarkMode = isDarkMode
        )
    }
}


// Functions:

private fun handleAddPlan(
    planName: String,
    planPrice: String,
    serviceName: String,
    personCount: String,
    userSubVM: UserSubscriptionViewModel,
    context: Context,
    coroutineScope: CoroutineScope,
    onBottomSheetMessageChange: (String) -> Unit,
    onBottomSheetSuccessChange: (Boolean) -> Unit,
    scaffoldState: BottomSheetScaffoldState
) {
    val plan = Plan(planName, planPrice.toDouble())

    userSubVM.addPlanToUserOnFirestore(serviceName, plan, personCount.toInt()) { success ->
        coroutineScope.launch {
            if (success) {
                onBottomSheetMessageChange(context.getString(R.string.text_selected_plan_added))
                onBottomSheetSuccessChange(true)
            } else {
                onBottomSheetMessageChange(context.getString(R.string.text_error_selected_plan_added))
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