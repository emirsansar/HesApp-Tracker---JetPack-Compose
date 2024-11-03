package com.emirsansar.hesapptracker.view.mainScreens.plansScreen

import android.annotation.SuppressLint
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Scaffold
import androidx.compose.material3.Text
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.emirsansar.hesapptracker.R
import com.emirsansar.hesapptracker.manager.AppManager
import com.emirsansar.hesapptracker.model.Plan
import com.emirsansar.hesapptracker.ui.theme.DarkThemeColors
import com.emirsansar.hesapptracker.ui.theme.LightThemeColors
import com.emirsansar.hesapptracker.view.mainScreens.customPlanScreen.CustomPlanScreen
import com.emirsansar.hesapptracker.view.mainScreens.plansScreen.components.AddPlanDialog
import com.emirsansar.hesapptracker.view.mainScreens.sharedComponents.CustomTopBar
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
    val appManager = AppManager.getInstance(context)

    LaunchedEffect(serviceName) {
        plansVM.fetchPlansOfServiceFromFirestore(serviceName)
    }

    Scaffold(
        topBar = {
            CustomTopBar(
                title = stringResource(id = R.string.services),
                isDarkMode = appManager.isDarkMode.value,
                onBackPressed = { navController.popBackStack() },
                onAddButtonClicked = {
                    val intent = Intent(context, CustomPlanScreen::class.java).apply {
                        putExtra("serviceName", serviceName)
                    }
                    context.startActivity(intent)
                }
            )
        },
        backgroundColor = if (appManager.isDarkMode.value) DarkThemeColors.BackgroundColor else LightThemeColors.BackgroundColor,
        content = { paddingValues ->
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                HeaderText(serviceName, appManager.isDarkMode.value)
                PlansText(appManager.isDarkMode.value)

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
                                        Toast.makeText(context, R.string.text_selected_plan_added, Toast.LENGTH_SHORT).show()
                                }
                                showDialog = false
                            },
                            onDismiss = { showDialog = false },
                            isDarkMode = appManager.isDarkMode.value
                        )
                    }
                }
            }
        }
    )
}

@Composable
private fun PlanList(
    planList: List<Plan>,
    setSelectedPlan: (Plan) -> Unit,
    setShowDialog: (Boolean) -> Unit
) {
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
private fun PlanCard(
    planName: String,
    planPrice: Number?,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 16.dp)
            .clickable { onClick() },
        elevation = 4.dp,
        shape = RoundedCornerShape(12.dp),
        backgroundColor = Color.White,
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
                fontSize = 19.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Start
            )

            Text(
                text = "${planPrice ?: 0} ₺",
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF4CAF50),
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.End
            )
        }
    }
}

@Composable
private fun HeaderText(serviceName: String, isDarkMode: Boolean) {
    Text(
        text = serviceName,
        fontSize = 25.sp,
        fontWeight = FontWeight.SemiBold,
        color = if (isDarkMode) Color.White else Color.Black,
        modifier = Modifier.padding(top = 16.dp)
    )
}

@Composable
private fun PlansText(isDarkMode: Boolean){
    Text(
        text = stringResource(id = R.string.label_plan),
        fontSize = 22.sp,
        color = if (isDarkMode) Color.White else Color.Black,
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 24.dp, top = 8.dp)
    )
}