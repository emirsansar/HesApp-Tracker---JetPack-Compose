package com.emirsansar.hesapptracker.view.AppMain

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.emirsansar.hesapptracker.viewModel.PlanViewModel

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun PlansScreen(
    serviceName: String,
    modifier: Modifier,
    navController: NavController,
    plansVM: PlanViewModel = PlanViewModel()
) {
    val fetchedPlanList by plansVM.planList.observeAsState(emptyList())

    LaunchedEffect(Unit) {
        plansVM.fetchPlansOfServiceFromFirestore(serviceName)
    }

    Scaffold(
        topBar = { appBar(navController) },
        content = { paddingValues ->
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = serviceName, fontSize = 30.sp, modifier = Modifier.padding(top = 5.dp))

                Text(text = "Plans:", fontSize = 20.sp, modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 20.dp, top = 8.dp)
                        .align(Alignment.Start)
                )

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    items(fetchedPlanList) { plan ->
                        PlanCard(
                            planName = plan.planName,
                            planPrice = plan.planPrice,
                            onClick = { /* Satıra tıklanınca yapılacak işlemler */ }
                        )
                    }
                }

            }
        }
    )
}

@Composable
fun PlanCard(planName: String, planPrice: Number?, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 16.dp)
            .clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = planName,
            fontSize = 18.sp,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Start
        )

        Text(
            text = "$planPrice",
            fontSize = 18.sp,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.End
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun appBar(navController: NavController) {
    TopAppBar(
        title = {
            Text(
                text = "Services",
                fontSize = 20.sp
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
        },
        modifier = Modifier.fillMaxWidth()
    )
}