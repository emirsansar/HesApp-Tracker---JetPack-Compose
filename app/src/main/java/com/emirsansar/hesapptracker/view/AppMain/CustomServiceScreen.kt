package com.emirsansar.hesapptracker.view.AppMain

import android.content.Context
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material.BottomSheetScaffold
import androidx.compose.material.BottomSheetScaffoldState
import androidx.compose.material.TextButton
import androidx.compose.material3.Button
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.rememberBottomSheetScaffoldState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
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
            TopBarCustomServiceScreen(onBackPressed, appManager.isDarkMode.value)
        },
        backgroundColor = if (appManager.isDarkMode.value) DarkThemeColors.BackgroundColor else LightThemeColors.BackgroundColor,
        sheetContent = {
            BottomSheetContent(
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
                scaffoldState = scaffoldState
            )
        }
    )

}

@Composable
private fun TopBarCustomServiceScreen(onBackPressed: () -> Unit, isDarkMode: Boolean) {
    TopAppBar(
        title = { Text(stringResource(id = R.string.services), fontSize = 20.sp,
            color = if (isDarkMode) Color.White else Color.Black) },
        navigationIcon = {
            IconButton(onClick = { onBackPressed() }) {
                Icon(Icons.Filled.ArrowBack, contentDescription = "Back",
                    tint = if (isDarkMode) Color.White else Color.Black )
            }
        },
        backgroundColor = if (isDarkMode) DarkThemeColors.BarColor else LightThemeColors.BarColor,
    )
}

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
    var showDialog by remember { mutableStateOf(false) }
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
            fontSize = 24.sp,
            color = if (appManager.isDarkMode.value) Color.White else Color.Black,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        CustomOutlinedTextField(
            value = serviceName,
            label = stringResource(id = R.string.label_service_name),
            error = serviceNameError,
            onValueChange = onServiceNameChange,
            appManager.isDarkMode.value
        )

        CustomOutlinedTextField(
            value = planName,
            label = stringResource(id = R.string.label_plan_name),
            error = planNameError,
            onValueChange = onPlanNameChange,
            appManager.isDarkMode.value
        )

        CustomOutlinedTextField(
            value = planPrice,
            label = stringResource(id = R.string.label_plan_price),
            error = planPriceError,
            onValueChange = onPlanPriceChange,
            appManager.isDarkMode.value,
            keyboardType = KeyboardType.Decimal
        )

        CustomOutlinedTextField(
            value = personCount,
            label = stringResource(id = R.string.label_user_count),
            error = personCountError,
            onValueChange = onPersonCountChange,
            appManager.isDarkMode.value,
            keyboardType = KeyboardType.Number,
        )

        Button(
            onClick = {
                focusManager.clearFocus()

                val price = planPrice.toDoubleOrNull()
                val count = personCount.toIntOrNull()

                if (price != null && count != null) {
                    alertMessage = context.getString(R.string.text_service_details, serviceName, planName, planPrice, personCount)
                    showDialog = true
                } else {
                    alertMessage = context.getString(R.string.label_error_invalid_format)
                    showAlertError = true
                }
            },
            enabled = !serviceNameError && !planNameError && !planPriceError && !personCountError,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = stringResource(id = R.string.button_add_service))
        }

        // Confirmation Dialog
        if (showDialog) {
            ConfirmationDialog(
                onDismissRequest = { showDialog = false },
                onConfirm = {
                    showDialog = false
                    handleAddService(
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
                alertMessage = context.getString(R.string.text_question_adding_service) + "\n$alertMessage",
                isDarkMode = appManager.isDarkMode.value
            )
        }

        // Error Alert Dialog
        if (showAlertError) {
            ErrorDialog(
                onDismissRequest = { showAlertError = false },
                alertMessage = alertMessage,
                isDarkMode = appManager.isDarkMode.value
            )
        }

        if (serviceNameError || planNameError || planPriceError || personCountError) {
            Text(
                text = stringResource(id = R.string.error_fill_all_fields),
                color = Color.Red, fontSize = 12.sp, modifier = Modifier.padding(top = 8.dp)
            )
        }

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomOutlinedTextField(
    value: String,
    label: String,
    error: Boolean,
    onValueChange: (String) -> Unit,
    isDarkMode: Boolean,
    keyboardType: KeyboardType = KeyboardType.Text,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp),
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

@Composable
private fun BottomSheetContent(
    bottomSheetMessage: String,
    isSuccess: Boolean,
    coroutineScope: CoroutineScope,
    scaffoldState: BottomSheetScaffoldState,
    isDarkMode: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = if (isDarkMode) DarkThemeColors.DrawerContentColor
                else LightThemeColors.DrawerContentColor
            )
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = if (isSuccess) stringResource(id = R.string.label_success) else stringResource(id = R.string.label_error),
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
            }
        }) {
            Text(text = stringResource(id = R.string.button_ok), fontSize = 16.sp, fontWeight = FontWeight.Medium)
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
                    text = stringResource(id = R.string.button_confirm),
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Green
                )
            } 
        },
        dismissButton = {
            TextButton(onClick = { onDismiss() }) {
                Text(
                    text = stringResource(id = R.string.button_cancel),
                    fontWeight = FontWeight.Medium,
                    color = Color.Red
                )
            } 
        },
        title = {
            Text(
                text = stringResource(id = R.string.label_adding_service),
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium,
                color = if (isDarkMode) Color.White else Color.Black
            ) 
        },
        text = { Text(text = alertMessage,
                      color = if(isDarkMode) Color.White else Color.Black)
        },
        shape = RoundedCornerShape(16.dp),
        containerColor = if (isDarkMode) DarkThemeColors.DrawerContentColor
                         else LightThemeColors.DrawerContentColor
    )
    
}

@Composable
private fun ErrorDialog(
    onDismissRequest: () -> Unit,
    alertMessage: String,
    isDarkMode: Boolean
) {
    AlertDialog(
        onDismissRequest = { onDismissRequest() },
        confirmButton = {
            TextButton(onClick = { onDismissRequest() }) {
                Text(
                    text = stringResource(id = R.string.button_ok),
                    fontWeight = FontWeight.SemiBold,
                    color = if(isDarkMode) Color.White else Color.Black
                )
            }
        },
        title = {
            Text(
                text = stringResource(id = R.string.text_invalid_price_format),
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium,
                color = if(isDarkMode) Color.White else Color.Black
            )
        },
        text = {
            Text(text = alertMessage, color =  if(isDarkMode) Color.White else Color.Black)
        },
        shape = RoundedCornerShape(16.dp),
        containerColor = if (isDarkMode) DarkThemeColors.DrawerContentColor
                         else LightThemeColors.DrawerContentColor
    )
}


private fun handleAddService(
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
                onBottomSheetMessageChange(R.string.text_selected_plan_added.toString())
                onBottomSheetSuccessChange(true)
            } else {
                onBottomSheetMessageChange(R.string.text_error_selected_plan_added.toString())
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