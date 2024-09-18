package com.emirsansar.hesapptracker.view.AppMain

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
import androidx.compose.material3.Button
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.emirsansar.hesapptracker.model.Service
import com.emirsansar.hesapptracker.viewModel.AuthenticationViewModel
import com.emirsansar.hesapptracker.viewModel.ServiceViewModel

@Composable
fun ServicesScreen(modifier: Modifier, serviceVM: ServiceViewModel = ServiceViewModel()) {

    var servicesList by remember { mutableStateOf(emptyList<Service>()) }

    LaunchedEffect(Unit) {
        serviceVM.fetchServicesFromFirestore { services, exception ->
            if (exception == null) {
                if (services != null) {
                    servicesList = services
                }
            }
        }
    }

    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            items(servicesList) { service ->
                Card(
                    modifier = Modifier
                        .padding(all = 10.dp)
                        .height(50.dp)
                        .fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.Start
                    ) {
                        Text(text = service.serviceName, fontSize = 20.sp)
                    }
                }
            }
        }
    }

}

