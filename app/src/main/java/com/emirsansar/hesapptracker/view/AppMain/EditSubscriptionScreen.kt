package com.emirsansar.hesapptracker.view.AppMain

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.emirsansar.hesapptracker.model.UserSubscription
import com.emirsansar.hesapptracker.ui.theme.HesAppTrackerTheme
import androidx.compose.material3.Button
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import com.emirsansar.hesapptracker.R
import com.emirsansar.hesapptracker.manager.AppManager
import com.emirsansar.hesapptracker.ui.theme.DarkThemeColors
import com.emirsansar.hesapptracker.ui.theme.LightThemeColors
import com.emirsansar.hesapptracker.viewModel.UserSubscriptionViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class EditSubscriptionScreen : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val subscription = intent.getParcelableExtra<UserSubscription>("subscription")

        setContent {
            HesAppTrackerTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    if (subscription != null) {
                        EditSubscriptionScreen(subscription, onBackPressed = { finish() })
                    }
                }
            }
        }
    }
}

@Composable
fun EditSubscriptionScreen(
    subscription: UserSubscription,
    userSubsVM: UserSubscriptionViewModel = UserSubscriptionViewModel(),
    onBackPressed: () -> Unit,
    modifier: Modifier = Modifier
) {
    var planName by remember { mutableStateOf(subscription.planName) }
    var planNameError by remember { mutableStateOf(false) }

    var planPrice by remember { mutableStateOf(subscription.planPrice.toString()) }
    var planPriceError by remember { mutableStateOf(false) }

    var personCount by remember { mutableStateOf(subscription.personCount.toString()) }
    var personCountError by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()
    val bottomSheetState = rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden)
    val context = LocalContext.current
    val appManager = AppManager.getInstance(context)

    Scaffold(
        topBar = { TopBarEditSubscriptionScreen(onBackPressed, appManager.isDarkMode.value) },
        backgroundColor = if (appManager.isDarkMode.value) DarkThemeColors.BackgroundColor else LightThemeColors.BackgroundColor,
        content = { innerPadding ->
            ModalBottomSheetLayout(
                sheetState = bottomSheetState,
                sheetContent = {
                    BottomSheetContent(
                        onConfirm = {
                            editSubscriptionByViewModel(
                                userSubsVM, subscription.serviceName, planName, planPrice, personCount, context
                            )
                            coroutineScope.launch { bottomSheetState.hide() }
                        },
                        onCancel = {
                            coroutineScope.launch { bottomSheetState.hide() }
                        },
                        appManager.isDarkMode.value
                    )
                },
                content = {
                    BodyContent(
                        subscription = subscription,
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
                            if (newValue.all { it.isDigit() }){
                                personCount = newValue
                            }
                            personCountError = newValue.isEmpty()
                        },
                        modifier = modifier,
                        innerPadding = innerPadding,
                        coroutineScope = coroutineScope,
                        bottomSheetState = bottomSheetState,
                        isDarkMode = appManager.isDarkMode.value
                    )
                }
            )
        }
    )
}

// Composable:

@Composable
private fun TopBarEditSubscriptionScreen(
    onBackPressed: () -> Unit,
    isDarkMode: Boolean
) {
    TopAppBar(
        title = { Text(text = stringResource(id = R.string.your_subscriptions), fontSize = 20.sp,
            color = if (isDarkMode) Color.White else Color.Black ) },
        navigationIcon = {
            IconButton(onClick = { onBackPressed() }) {
                Icon(Icons.Filled.ArrowBack, contentDescription = "Back",
                    tint =  if (isDarkMode) Color.White else Color.Black)
            }
        },
        backgroundColor = if (isDarkMode) DarkThemeColors.BarColor else LightThemeColors.BarColor,
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
private fun BodyContent(
    subscription: UserSubscription,
    planName: String,
    planPrice: String,
    personCount: String,
    planNameError: Boolean,
    planPriceError: Boolean,
    personCountError: Boolean,
    onPlanNameChange: (String) -> Unit,
    onPlanPriceChange: (String) -> Unit,
    onPersonCountChange: (String) -> Unit,
    modifier: Modifier,
    innerPadding: PaddingValues,
    coroutineScope: CoroutineScope,
    bottomSheetState: ModalBottomSheetState,
    isDarkMode: Boolean
) {
    val focusManager = LocalFocusManager.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(innerPadding)
            .padding(16.dp)
            .clickable { focusManager.clearFocus() },
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(id = R.string.label_edit_subscription) + subscription.serviceName,
            fontSize = 24.sp,
            color = if (isDarkMode) Color.White else Color.Black,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        CustomOutlinedTextField(
            value = planName,
            label = stringResource(id = R.string.label_new_plan_name),
            error = planNameError,
            onValueChange =  onPlanNameChange,
            isDarkMode = isDarkMode
        )

        CustomOutlinedTextField(
            value = planPrice,
            label = stringResource(id = R.string.label_new_plan_price),
            error = planPriceError,
            onValueChange = { newValue ->
                if (isValidPriceInput(newValue)) {
                    onPlanPriceChange(newValue)
                }
            },
            isDarkMode = isDarkMode,
            keyboardType = KeyboardType.Decimal
        )

        CustomOutlinedTextField(
            value = personCount,
            label = stringResource(id = R.string.label_new_user_count),
            error = personCountError,
            onValueChange =  { newValue ->
                if (newValue.all { it.isDigit() }) {
                    onPersonCountChange(newValue)
                }
            },
            isDarkMode = isDarkMode,
            keyboardType = KeyboardType.Number
        )

        Button(
            onClick = {
                focusManager.clearFocus()
                coroutineScope.launch {
                    bottomSheetState.show()
                }
            },
            enabled = !planNameError && !planPriceError && !personCountError,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = stringResource(id = R.string.button_save_changes))
        }

        if (planNameError || planPriceError || personCountError) {
            Text(
                text = stringResource(id = R.string.error_fill_all_fields),
                color = Color.Red,
                fontSize = 15.sp,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}

@Composable
private fun BottomSheetContent(
    onConfirm: () -> Unit,
    onCancel: () -> Unit,
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
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Text(
            text = stringResource(id = R.string.text_question_changing_service),
            fontSize = 16.sp,
            modifier = Modifier.padding(horizontal = 20.dp),
            color = if (isDarkMode) Color.White else Color.Black
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
        ) {
            Text(
                text = stringResource(id = R.string.button_cancel),
                color = Color.Red,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier
                    .clickable(onClick = onCancel)
                    .padding(16.dp)
            )

            Text(
                text = stringResource(id = R.string.button_confirm),
                color = Color.Green,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier
                    .clickable(onClick = onConfirm)
                    .padding(16.dp)
            )
        }
    }
}

// Functions:

private fun editSubscriptionByViewModel(
    userSubsVM: UserSubscriptionViewModel,
    serviceName: String,
    planName: String,
    planPrice: String,
    userCount: String,
    context: Context
) {
    val priceControl = planPrice.replace(',', '.').toDouble()

    val userSub = UserSubscription(serviceName, planName, priceControl, userCount.toInt())

    userSubsVM.updateSubscription(userSub) { success ->
        if (success) {
            Toast.makeText(context, R.string.text_subscription_updated_successfully, Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, R.string.text_subscription_update_failed, Toast.LENGTH_SHORT).show()
        }
    }
}

// Validates a price input string to ensure it contains only digits, a single dot or comma,
//and allows at most two digits after the last separator.
fun isValidPriceInput(input: String): Boolean {
    if (input.all { it.isDigit() || it == '.' || it == ',' }) {
        val dotCount = input.count { it == '.' }
        val commaCount = input.count { it == ',' }

        if (dotCount > 1 || commaCount > 1 || (dotCount == 1 && commaCount == 1)) return false

        val afterSeparator = if (dotCount == 1) {
            input.substringAfterLast('.')
        } else if (commaCount == 1) {
            input.substringAfterLast(',')
        } else {
            ""
        }

        if (afterSeparator.length > 2) return false

        return true
    }
    return false
}

@Preview(showBackground = true)
@Composable
fun EditSubscriptionScreenPreview() {
    HesAppTrackerTheme {
        EditSubscriptionScreen()
    }
}