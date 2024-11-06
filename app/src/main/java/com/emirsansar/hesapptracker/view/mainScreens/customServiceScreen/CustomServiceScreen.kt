package com.emirsansar.hesapptracker.view.mainScreens.customServiceScreen

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
import androidx.compose.material3.Button
import androidx.compose.material.rememberBottomSheetScaffoldState
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
import com.emirsansar.hesapptracker.view.mainScreens.customServiceScreen.components.BottomSheetContentCustomService
import com.emirsansar.hesapptracker.view.mainScreens.customServiceScreen.components.AddCustomServiceDialog
import com.emirsansar.hesapptracker.view.mainScreens.editSubscriptionScreen.isValidPriceInput
import com.emirsansar.hesapptracker.view.mainScreens.sharedComponents.CustomOutlinedTextField
import com.emirsansar.hesapptracker.view.mainScreens.sharedComponents.CustomTopBar
import com.emirsansar.hesapptracker.viewModel.UserSubscriptionViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


class CustomServiceScreen : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HesAppTrackerTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color(0xFFe3e5e6)
                ) {
                    CustomServiceScreenView(onBackPressed = { finish() })
                }
            }
        }
    }
}

@Composable
fun CustomServiceScreenView(
    userSubVM: UserSubscriptionViewModel = UserSubscriptionViewModel(),
    onBackPressed: () -> Unit,
    modifier: Modifier = Modifier)
{
    val coroutineScope = rememberCoroutineScope()
    val scaffoldState = rememberBottomSheetScaffoldState()
    val context = LocalContext.current
    val appManager = AppManager.getInstance(context)

    var serviceName by remember { mutableStateOf("") }
    var serviceNameError by remember { mutableStateOf(false) }

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
                title = stringResource(id = R.string.services),
                isDarkMode = appManager.isDarkMode.value,
                onBackPressed = onBackPressed
            )

        },
        backgroundColor = if (appManager.isDarkMode.value) DarkThemeColors.BackgroundColor else LightThemeColors.BackgroundColor,
        sheetContent = {
            BottomSheetContentCustomService(
                bottomSheetMessage,
                bottomSheetSuccess,
                coroutineScope,
                scaffoldState,
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
                serviceNameError = serviceNameError,
                planNameError = planNameError,
                planPriceError = planPriceError,
                personCountError = personCountError,
                onServiceNameChange = { value ->
                    serviceName = value
                    serviceNameError = value.isEmpty()
                },
                onPlanNameChange = { value ->
                    planName = value
                    planNameError = value.isEmpty()
                },
                onPlanPriceChange = { value ->
                    if (isValidPriceInput(value)){
                        planPrice = value
                    }
                    planPriceError = value.isEmpty()
                },
                onPersonCountChange = { value ->
                    if (value.all { it.isDigit() }){
                        personCount = value
                    }
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
                scaffoldState = scaffoldState
            )
        }
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
    serviceNameError: Boolean,
    planNameError: Boolean,
    planPriceError: Boolean,
    personCountError: Boolean,
    onServiceNameChange: (String) -> Unit,
    onPlanNameChange: (String) -> Unit,
    onPlanPriceChange: (String) -> Unit,
    onPersonCountChange: (String) -> Unit,
    onBottomSheetMessageChange: (String) -> Unit,
    onBottomSheetSuccessChange: (Boolean) -> Unit,
    modifier: Modifier,
    innerPadding: PaddingValues,
    coroutineScope: CoroutineScope,
    scaffoldState: BottomSheetScaffoldState
) {
    val focusManager = LocalFocusManager.current
    var showConfirmationDialog by remember { mutableStateOf(false) }
    var alertMessage by remember { mutableStateOf("") }
    var showAlertError by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val appManager = AppManager.getInstance(context)

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
        Text(
            text = stringResource(id = R.string.label_custom_service),
            fontSize = 23.sp,
            color = if (appManager.isDarkMode.value) Color.White else Color.Black,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        CustomOutlinedTextField(
            value = serviceName,
            label = stringResource(id = R.string.label_service_name),
            error = serviceNameError,
            onValueChange = onServiceNameChange,
            isDarkMode = appManager.isDarkMode.value,
            modifier = Modifier.fillMaxWidth(0.95f).padding(bottom = 8.dp)
        )

        CustomOutlinedTextField(
            value = planName,
            label = stringResource(id = R.string.label_plan_name),
            error = planNameError,
            onValueChange = onPlanNameChange,
            isDarkMode = appManager.isDarkMode.value,
            modifier = Modifier.fillMaxWidth(0.95f).padding(bottom = 8.dp)
        )

        CustomOutlinedTextField(
            value = planPrice,
            label = stringResource(id = R.string.label_plan_price),
            error = planPriceError,
            onValueChange = onPlanPriceChange,
            isDarkMode = appManager.isDarkMode.value,
            keyboardType = KeyboardType.Decimal,
            modifier = Modifier.fillMaxWidth(0.95f).padding(bottom = 8.dp)
        )

        CustomOutlinedTextField(
            value = personCount,
            label = stringResource(id = R.string.label_user_count),
            error = personCountError,
            onValueChange = onPersonCountChange,
            isDarkMode = appManager.isDarkMode.value,
            keyboardType = KeyboardType.Number,
            modifier = Modifier.fillMaxWidth(0.95f).padding(bottom = 8.dp)
        )

        Button(
            onClick = {
                focusManager.clearFocus()

                val price = planPrice.replace(',', '.').toDoubleOrNull()
                val count = personCount.toIntOrNull()

                if (price != null && count != null) {
                    alertMessage = context.getString(R.string.text_service_details, serviceName, planName, planPrice, personCount)
                    showConfirmationDialog = true
                } else {
                    alertMessage = context.getString(R.string.text_invalid_price_format)
                    showAlertError = true
                }
            },
            enabled = !serviceNameError && !planNameError && !planPriceError && !personCountError,
            modifier =  Modifier.fillMaxWidth(0.95f)
        ) {
            Text(text = stringResource(id = R.string.button_add_service))
        }

        if (serviceNameError || planNameError || planPriceError || personCountError) {
            Text(
                text = stringResource(id = R.string.error_fill_all_fields),
                color = Color.Red, fontSize = 12.sp, modifier = Modifier.padding(top = 8.dp)
            )
        }

    }

    if (showConfirmationDialog) {
        AddCustomServiceDialog(
            onDismissRequest = { showConfirmationDialog = false },
            onConfirm = {
                showConfirmationDialog = false
                handleAddService(
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
            alertMessage = context.getString(R.string.text_question_adding_service) + "\n$alertMessage",
            isDarkMode = appManager.isDarkMode.value
        )
    }
}


// Functions:

private fun handleAddService(
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
                onBottomSheetSuccessChange(false)
            }
            scaffoldState.bottomSheetState.expand()
        }
    }
}


@Preview(showBackground = true)
@Composable
fun CustomServiceScreenPreview() {
    HesAppTrackerTheme {
        CustomServiceScreen()
    }
}