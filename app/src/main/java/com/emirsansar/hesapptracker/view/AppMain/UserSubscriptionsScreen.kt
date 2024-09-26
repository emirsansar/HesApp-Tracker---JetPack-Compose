package com.emirsansar.hesapptracker.view.AppMain

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ExposedDropdownMenuBox
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.emirsansar.hesapptracker.model.UserSubscription
import com.emirsansar.hesapptracker.viewModel.UserSubscriptionViewModel
import com.google.firebase.auth.FirebaseAuth

// Enum class to define sorting options
enum class SortType {
    Default,
    PriceAscending,
    PriceDescending,
    Alphabetical
}

@Composable
fun UserSubscriptionsScreen(
    modifier: Modifier, userSubsVM:
    UserSubscriptionViewModel = UserSubscriptionViewModel())
{
    val fetchedUserSubList by userSubsVM.userSubscriptionList.observeAsState(emptyList())
    // This holds the list to be displayed after sorting, ensuring the original fetched list remains unchanged.
    var displayedUserSubList = remember { mutableStateListOf<UserSubscription>() }

    var sortType by remember { mutableStateOf(SortType.Default) }
    val fetchingSubsState by userSubsVM.fetchingSubscriptionsState.observeAsState(UserSubscriptionViewModel.FetchingSubscriptionsState.IDLE)

    val userEmail = FirebaseAuth.getInstance().currentUser!!.email
    val context = LocalContext.current

    LaunchedEffect(userEmail) {
        userSubsVM.fetchUserSubscriptionsFromFirestore(userEmail!!)
    }

    LaunchedEffect(fetchedUserSubList) {
        displayedUserSubList.clear()
        displayedUserSubList.addAll(fetchedUserSubList)
    }


    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally)
    {
        Text(text = "My Subscriptions", fontSize = 30.sp)

        SortPicker(
            sortType = sortType,
            onSortTypeChanged = { selectedSortType ->
                sortType = selectedSortType
                displayedUserSubList.clear()
                displayedUserSubList.addAll(sortSubscriptions(fetchedUserSubList, selectedSortType))
            }
        )

        when (fetchingSubsState) {
            UserSubscriptionViewModel.FetchingSubscriptionsState.SUCCESS -> {
                if (fetchedUserSubList.isNotEmpty()) {
                    SubscriptionList(
                        subscriptionList = displayedUserSubList,
                        onEdit = { },
                        onRemove = { subscription ->
                            removeSubscription(context, userSubsVM, subscription, displayedUserSubList)
                        }
                    )
                } else {
                    CenteredText("You currently have no subscriptions.")
                }
            }

            UserSubscriptionViewModel.FetchingSubscriptionsState.FAILURE -> {
                CenteredText("An error occurred while fetching subscriptions.\nPlease try again.")
            }

            else -> {
                CenteredCircularProgress()
            }
        }

    }
}


// Composable:

// Composable function to display a list of subscriptions in a LazyColumn.
@Composable
fun SubscriptionList(subscriptionList: List<UserSubscription>, onEdit: () -> Unit, onRemove: (UserSubscription) -> Unit,) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(all = 10.dp)
    ) {
        items(subscriptionList) { sub ->
            SubscriptionCard(
                sub = sub,
                onEditClick = onEdit,
                onRemove = { onRemove(sub) }
            )
        }
    }
}

// Composable function to display a card with subscription details.
@Composable
fun SubscriptionCard(sub: UserSubscription, onEditClick: () -> Unit, onRemove: () -> Unit) {
    var expandedMenu by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .padding(all = 10.dp)
            .fillMaxWidth()
            .clickable { expandedMenu = true }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.Start
            ) {
                Text(text = sub.serviceName, fontSize = 20.sp)
                Text(text = sub.planName)
            }

            Text(text = sub.planPrice.toString(), fontSize = 20.sp)
        }

        SubscriptionMenu(
            expanded = expandedMenu,
            onDismiss = { expandedMenu = false },
            onEdit = { onEditClick() },
            onRemove = { onRemove() }
        )
    }
}

@Composable
fun SubscriptionMenu(
    expanded: Boolean,
    onDismiss: () -> Unit,
    onEdit: () -> Unit,
    onRemove: () -> Unit
) {
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = { onDismiss() }
    ) {
        DropdownMenuItem(onClick = {
            onDismiss()
            onEdit()
        }) {
            Text("Edit")
        }
        DropdownMenuItem(onClick = {
            onDismiss()
            onRemove()
        }) {
            Text("Remove")
        }
    }
}

// Composable function for SortPicker.
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SortPicker(
    sortType: SortType,
    onSortTypeChanged: (SortType) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedButton(
            onClick = { expanded = true },
            modifier = Modifier.width(250.dp)
        ) {
            when (sortType) {
                SortType.Default -> Text(text = "Sort: ...")
                SortType.PriceAscending -> Text(text = "Sort: Price Ascending")
                SortType.PriceDescending -> Text(text = "Sort: Price Descending")
                SortType.Alphabetical -> Text(text = "Sort: Alphabetical")
            }
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(onClick = {
                onSortTypeChanged(SortType.PriceAscending)
                expanded = false
            }) {
                Text("Price Ascending")
            }
            DropdownMenuItem(onClick = {
                onSortTypeChanged(SortType.PriceDescending)
                expanded = false
            }) {
                Text("Price Descending")
            }
            DropdownMenuItem(onClick = {
                onSortTypeChanged(SortType.Alphabetical)
                expanded = false
            }) {
                Text("Alphabetical")
            }
        }
    }
}

// Function to sort subscriptions based on selected sort type.
fun sortSubscriptions(userSubList: List<UserSubscription>, sortType: SortType): List<UserSubscription> {
    return when (sortType) {
        SortType.Default -> userSubList
        SortType.PriceAscending -> userSubList.sortedBy { it.planPrice }
        SortType.PriceDescending -> userSubList.sortedByDescending { it.planPrice }
        SortType.Alphabetical -> userSubList.sortedBy { it.serviceName }
    }
}

@Composable
fun CenteredText(text: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = text, fontSize = 20.sp, color = Color.Gray)
    }
}

@Composable
fun CenteredCircularProgress() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator(
            modifier = Modifier.size(30.dp),
            color = Color.Blue
        )
    }
}

// Functions:

private fun removeSubscription(context: Context, userSubsVM: UserSubscriptionViewModel, sub: UserSubscription, displayedUserSubList: MutableList<UserSubscription>) {
    userSubsVM.removeSubscriptionFromUser(sub) { success ->
        if (success) {
            Toast.makeText(context, "${sub.serviceName} removed successfully.", Toast.LENGTH_SHORT).show()
            displayedUserSubList.remove(sub)
        } else {
            Toast.makeText(context, "${sub.serviceName} could not be removed", Toast.LENGTH_SHORT).show()
        }
    }
}