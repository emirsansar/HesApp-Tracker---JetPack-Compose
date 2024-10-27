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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
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

@OptIn(ExperimentalMaterial3Api::class)
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

@Composable
private fun TopBarEditSubscriptionScreen(
    onBackPressed: () -> Unit,
    isDarkMode: Boolean
) {
    TopAppBar(
        title = { Text("My Subscriptions", fontSize = 20.sp,
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

@OptIn(ExperimentalMaterial3Api::class)
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
            text = "Editing: ${subscription.serviceName}",
            fontSize = 24.sp,
            color = if (isDarkMode) Color.White else Color.Black,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        CustomOutlinedTextField(
            value = planName,
            label = "Plan Name",
            error = planNameError,
            onValueChange =  onPlanNameChange,
            isDarkMode = isDarkMode
        )

        CustomOutlinedTextField(
            value = planPrice,
            label = "Plan Price",
            error = planPriceError,
            onValueChange =  { newValue ->
                if (newValue.all { it.isDigit() || it == '.' || it == ',' }) {
                    onPlanPriceChange(newValue)
                }
            },
            isDarkMode = isDarkMode
        )

        CustomOutlinedTextField(
            value = personCount,
            label = "Person Count",
            error = personCountError,
            onValueChange =  { newValue ->
                if (newValue.all { it.isDigit() }) {
                    onPersonCountChange(newValue)
                }
            },
            isDarkMode = isDarkMode
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
            Text(text = "Save Changes")
        }

        if (planNameError || planPriceError || personCountError) {
            Text(
                text = "Please fill in all fields.",
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
            text = "Are you sure you want to change your subscription information?",
            fontSize = 16.sp,
            modifier = Modifier.padding(horizontal = 20.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
        ) {
            Text(
                text = "Cancel",
                color = Color.Red,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier
                    .clickable(onClick = onCancel)
                    .padding(16.dp)
            )

            Text(
                text = "Confirm",
                color = Color.Green,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier
                    .clickable(onClick = onConfirm)
                    .padding(16.dp)
            )
        }
    }
}

private fun editSubscriptionByViewModel(
    userSubsVM: UserSubscriptionViewModel,
    serviceName: String,
    planName: String,
    planPrice: String,
    userCount: String,
    context: Context)
{
    val userSub = UserSubscription(serviceName, planName, planPrice.toDouble(), userCount.toInt())

    userSubsVM.updateSubscription(userSub) { success ->
        println(success)
        if (success) {
            Toast.makeText(context, "The subscription has been updated successfully.", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "The subscription could not be updated.", Toast.LENGTH_SHORT).show()
        }
    }
}

@Preview(showBackground = true)
@Composable
fun EditSubscriptionScreenPreview() {
    HesAppTrackerTheme {
        EditSubscriptionScreen()
    }
}