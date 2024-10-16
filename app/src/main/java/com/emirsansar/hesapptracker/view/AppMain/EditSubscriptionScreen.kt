package com.emirsansar.hesapptracker.view.AppMain

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
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
import com.emirsansar.hesapptracker.view.AppMain.ui.theme.HesAppTrackerTheme
import androidx.compose.material3.Button
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.KeyboardType
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
    modifier: Modifier = Modifier) {

    var planName by remember { mutableStateOf(subscription.planName) }
    var planNameError by remember { mutableStateOf(false) }

    var planPrice by remember { mutableStateOf(subscription.planPrice.toString()) }
    var planPriceError by remember { mutableStateOf(false) }

    var personCount by remember { mutableStateOf(subscription.personCount.toString()) }
    var personCountError by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()
    val bottomSheetState = rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden)
    val context = LocalContext.current

    Scaffold(
        topBar = { AppBar(onBackPressed) },
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
                        }
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
                        bottomSheetState = bottomSheetState
                    )
                }
            )
        }
    )

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AppBar(onBackPressed: () -> Unit) {
    TopAppBar(
        title = { Text("My Subscriptions", fontSize = 20.sp) },
        navigationIcon = {
            IconButton(onClick = { onBackPressed() }) {
                Icon(Icons.Filled.ArrowBack, contentDescription = "Back", tint = Color.Gray)
            }
        }
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
    bottomSheetState: ModalBottomSheetState
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
            modifier = Modifier.padding(bottom = 16.dp)
        )

        OutlinedTextField(
            value = planName,
            onValueChange = onPlanNameChange,
            label = { Text("Plan Name") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = if (planNameError) Color.Red else MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = if (planNameError) Color.Red else Color.Gray
            )
        )

        OutlinedTextField(
            value = planPrice,
            onValueChange = { newValue ->
                if (newValue.all { it.isDigit() || it == '.' || it == ',' }) {
                    onPlanPriceChange(newValue)
                }
            },
            label = { Text("Plan Price") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = if (planPriceError) Color.Red else MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = if (planPriceError) Color.Red else Color.Gray
            ),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
        )

        OutlinedTextField(
            value = personCount,
            onValueChange = { newValue ->
                if (newValue.all { it.isDigit() }) {
                    onPersonCountChange(newValue)
                }
            },
            label = { Text("Person Count") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = if (personCountError) Color.Red else MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = if (personCountError) Color.Red else Color.Gray
            ),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
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
                fontSize = 12.sp,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}

@Composable
private fun BottomSheetContent(
    onConfirm: () -> Unit,
    onCancel: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Text(
            text = "Are you sure you want to change your subscription information?",
            fontSize = 18.sp,
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
                modifier = Modifier
                    .clickable(onClick = onCancel)
                    .padding(16.dp)
            )

            Text(
                text = "Confirm",
                color = Color.Green,
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