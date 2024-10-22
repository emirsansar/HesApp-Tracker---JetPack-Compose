package com.emirsansar.hesapptracker.view.AppMain

import android.annotation.SuppressLint
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
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.emirsansar.hesapptracker.model.Service
import com.emirsansar.hesapptracker.viewModel.ServiceViewModel

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
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

    Scaffold(
        topBar = { TopBarServiceScreen(context) },
        backgroundColor = Color(0xFFe3e5e6),
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                ServiceList(serviceList, navController)
            }
        }
    )

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
private fun TopBarServiceScreen(context: Context) {
    TopAppBar(
        title = {
            Text(text = "Services", fontSize = 21.sp, fontWeight = FontWeight.SemiBold)
        },
        actions = {
            IconButton(onClick = {
                val intent = Intent(context, CustomServiceScreen::class.java)
                context.startActivity(intent)
            }) {
                Icon( imageVector = Icons.Default.AddCircle, contentDescription = "Add Service Button", tint = Color.DarkGray )
            }
        },
        backgroundColor = Color.LightGray,
        modifier = Modifier.fillMaxWidth()
    )
}