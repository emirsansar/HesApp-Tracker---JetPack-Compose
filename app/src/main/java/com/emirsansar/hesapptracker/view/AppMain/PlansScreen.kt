package com.emirsansar.hesapptracker.view.AppMain

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.AlertDialog
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.OutlinedButton
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material3.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.emirsansar.hesapptracker.model.Plan
import com.emirsansar.hesapptracker.viewModel.PlanViewModel
import com.emirsansar.hesapptracker.viewModel.UserSubscriptionViewModel

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun PlansScreen(
    serviceName: String,
    modifier: Modifier,
    navController: NavController,
    plansVM: PlanViewModel = PlanViewModel(),
    userSubsVM: UserSubscriptionViewModel = UserSubscriptionViewModel()
) {
    val fetchedPlanList by plansVM.planList.observeAsState(emptyList())

    var selectedPlan by remember { mutableStateOf<Plan?>(null) }
    var showDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current

    LaunchedEffect(serviceName) {
        plansVM.fetchPlansOfServiceFromFirestore(serviceName)
    }

    Surface(
        modifier = modifier.fillMaxSize(),
        color = Color(0xFFe3e5e6)
    ) {
        Scaffold(
            topBar = { TopBarPlansScreen(serviceName, navController, context) },
            backgroundColor = Color.Transparent,
            content = { paddingValues ->
                Column(
                    modifier = modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    HeaderText(serviceName)
                    PlansText()

                    PlanList(
                        fetchedPlanList,
                        setSelectedPlan = { plan -> selectedPlan = plan },
                        setShowDialog = { visible -> showDialog = visible }
                    )

                    if (showDialog) {
                        selectedPlan?.let {
                            AddPlanDialog(
                                plan = it,
                                onConfirm = { personCount ->
                                    userSubsVM.addPlanToUserOnFirestore(serviceName, it, personCount) { success ->
                                        if (success)
                                            Toast.makeText(context, "Plan has been added successfully.", Toast.LENGTH_SHORT).show()
                                    }
                                    showDialog = false
                                },
                                onDismiss = { showDialog = false }
                            )
                        }
                    }
                }
            }
        )
    }

}

@Composable
private fun PlanList(
    planList: List<Plan>,
    setSelectedPlan: (Plan) -> Unit,
    setShowDialog: (Boolean) -> Unit)
{
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        items(planList) { plan ->
            PlanCard(
                planName = plan.planName,
                planPrice = plan.planPrice,
                onClick = {
                    setSelectedPlan(plan)
                    setShowDialog(true)
                }
            )
        }
    }
}

@Composable
private fun PlanCard(planName: String, planPrice: Number?, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 16.dp)
            .clickable { onClick() },
        elevation = 4.dp,
        backgroundColor = Color.White,
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = planName,
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Start
            )

            Text(
                text = "${planPrice ?: 0} ₺",
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF4CAF50),
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.End
            )
        }
    }
}


@Composable
private fun TopBarPlansScreen(serviceName: String, navController: NavController, context: Context) {
    TopAppBar(
        title = {
            Text(
                text = "Services",
                fontSize = 20.sp, fontWeight = FontWeight.Medium
            )
        },
        navigationIcon = {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back"
                )
            }
        },
        actions = {
            IconButton(onClick = {
                val intent = Intent(context, CustomPlanScreen::class.java).apply {
                    putExtra("serviceName", serviceName)
                }
                context.startActivity(intent)
            }) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Custom Plan Button",
                    tint = Color.DarkGray
                )
            }
        },
        backgroundColor = Color.LightGray,
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
private fun AddPlanDialog(
    plan: Plan,
    onConfirm: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    var personCount by remember { mutableStateOf("1") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Add Plan", fontSize = 20.sp, fontWeight = FontWeight.Bold) },
        text = {
            Column {
                Text(
                    text = "Plan Details:\n" +
                            "- Name: '${plan.planName}'\n" +
                            "- Price: '${plan.planPrice}'",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(text = "How many users?", fontSize = 16.sp)
                TextField(
                    value = personCount,
                    onValueChange = {
                        if (it.all { char -> char.isDigit() }) {
                            personCount = it
                        }
                    },
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Number
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    colors = TextFieldDefaults.textFieldColors(
                        backgroundColor = Color.Transparent,
                        focusedIndicatorColor = Color.Blue,
                        unfocusedIndicatorColor = Color.Gray
                    )
                )
            }
        },
        buttons = {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                OutlinedButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 15.dp)
                ) {
                    Text("Cancel")
                }

                Spacer(modifier = Modifier.width(10.dp))

                OutlinedButton(
                    onClick = {
                        personCount.toIntOrNull()?.let { count ->
                            onConfirm(count)
                        }
                        onDismiss()
                    },
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 15.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color.White,
                        backgroundColor = Color.Green
                    )
                ) {
                    Text("Confirm")
                }
            }
        }
    )
}

@Composable
private fun HeaderText(serviceName: String) {
    Text(
        text = serviceName,
        fontSize = 25.sp,
        fontWeight = FontWeight.Medium,
        modifier = Modifier.padding(top = 16.dp)
    )
}

@Composable
private fun PlansText(){
    Text(
        text = "Plans:",
        fontSize = 22.sp,
        modifier = Modifier
            .fillMaxWidth().padding(start = 24.dp, top = 8.dp)
    )
}