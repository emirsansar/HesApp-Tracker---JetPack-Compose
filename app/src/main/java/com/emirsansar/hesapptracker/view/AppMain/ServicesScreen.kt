package com.emirsansar.hesapptracker.view.AppMain

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.emirsansar.hesapptracker.model.Service
import com.emirsansar.hesapptracker.viewModel.ServiceViewModel

@Composable
fun ServicesScreen(
    modifier: Modifier, serviceVM:
    ServiceViewModel = ServiceViewModel(),
    navController: NavController
){
    var serviceList by remember { mutableStateOf(emptyList<Service>()) }

    val context = LocalContext.current

    LaunchedEffect(Unit) {
        serviceVM.fetchServicesFromFirestore { services, _ ->
            if (!services.isNullOrEmpty()) serviceList = services
        }
    }

    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Services", fontSize = 30.sp)

        AddCustomServiceButton(context)

        ServiceList(serviceList, navController)
    }

}

@Composable
private fun ServiceList(serviceList: List<Service>, navController: NavController) {
    LazyColumn( modifier = Modifier
        .fillMaxSize()
        .padding(all = 10.dp))
    {
        items(serviceList) { service ->
            ServiceCard(service = service, navController)
        }
    }
}

@Composable
private fun ServiceCard(service: Service, navController: NavController) {
    Card(
        modifier = Modifier
            .padding(all = 10.dp)
            .height(50.dp)
            .fillMaxWidth()
            .clickable {
                navController.navigate("service_plans_screen/${service.serviceName}")
            }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(all = 2.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.Start
        ) {
            Text(text = service.serviceName, fontSize = 20.sp, modifier = Modifier.padding(start = 5.dp))
        }
    }
}

@Composable
private fun AddCustomServiceButton(context: Context) {
    Card(
        modifier = Modifier
            .padding(all = 20.dp)
            .height(50.dp)
            .fillMaxWidth()
            .clickable {
                val intent = Intent(context, CustomServiceScreen::class.java)
                context.startActivity(intent)
            }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(all = 2.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = "Add Custom Service",
                fontSize = 20.sp,
                modifier = Modifier.padding(start = 5.dp)
            )
        }
    }
}