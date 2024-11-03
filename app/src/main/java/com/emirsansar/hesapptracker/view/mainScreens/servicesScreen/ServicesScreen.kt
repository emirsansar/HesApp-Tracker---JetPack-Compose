package com.emirsansar.hesapptracker.view.mainScreens.servicesScreen

import android.annotation.SuppressLint
import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Card
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Icon
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.emirsansar.hesapptracker.R
import com.emirsansar.hesapptracker.manager.AppManager
import com.emirsansar.hesapptracker.model.Service
import com.emirsansar.hesapptracker.ui.theme.DarkThemeColors
import com.emirsansar.hesapptracker.ui.theme.LightThemeColors
import com.emirsansar.hesapptracker.view.mainScreens.customServiceScreen.CustomServiceScreen
import com.emirsansar.hesapptracker.view.mainScreens.sharedComponents.CustomTopBar
import com.emirsansar.hesapptracker.viewModel.ServiceViewModel

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun ServicesScreen(
    modifier: Modifier,
    serviceVM: ServiceViewModel = ServiceViewModel(),
    navController: NavController
){
    var serviceList by remember { mutableStateOf(emptyList<Service>()) }
    val context = LocalContext.current
    val appManager = AppManager.getInstance(context)

    LaunchedEffect(Unit) {
        serviceVM.fetchServicesFromFirestore { services, _ ->
            if (!services.isNullOrEmpty()) serviceList = services
        }
    }

    Scaffold(
        topBar = {
            CustomTopBar(
                title = stringResource(id = R.string.services),
                isDarkMode = appManager.isDarkMode.value,
                onAddButtonClicked = {
                    val intent = Intent(context, CustomServiceScreen::class.java)
                    context.startActivity(intent)
                }
            )
        },
        backgroundColor = if (appManager.isDarkMode.value) DarkThemeColors.BackgroundColor
                          else LightThemeColors.BackgroundColor,
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                ServiceList(
                    serviceList = serviceList,
                    navController = navController
                )
            }
        }
    )

}

@Composable
private fun ServiceList(
    serviceList: List<Service>,
    navController: NavController
) {
    LazyColumn( modifier = Modifier
        .fillMaxSize()
        .padding(all = 10.dp))
    {
        items(serviceList) { service ->
            ServiceCard(
                service = service,
                navController = navController
            )
        }
    }
}

@Composable
private fun ServiceCard(
    service: Service,
    navController: NavController
) {
    Card(
        modifier = Modifier
            .padding(all = 10.dp)
            .height(50.dp)
            .fillMaxWidth()
            .clickable {
                navController.navigate("service_plans_screen/${service.serviceName}")
            },
        backgroundColor = Color.White
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = service.serviceName,
                fontSize = 20.sp,
                modifier = Modifier.padding(start = 5.dp)
            )

            Icon(
                imageVector = Icons.Default.ArrowForward,
                contentDescription = "navigate to service",
                tint = Color.DarkGray,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}