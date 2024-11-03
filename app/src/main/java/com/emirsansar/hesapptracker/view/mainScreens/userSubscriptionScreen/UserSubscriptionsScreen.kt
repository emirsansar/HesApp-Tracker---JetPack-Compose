package com.emirsansar.hesapptracker.view.mainScreens.userSubscriptionScreen

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Scaffold
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.emirsansar.hesapptracker.R
import com.emirsansar.hesapptracker.manager.AppManager
import com.emirsansar.hesapptracker.model.UserSubscription
import com.emirsansar.hesapptracker.ui.theme.DarkThemeColors
import com.emirsansar.hesapptracker.ui.theme.LightThemeColors
import com.emirsansar.hesapptracker.view.mainScreens.editSubscriptionScreen.EditSubscriptionScreen
import com.emirsansar.hesapptracker.view.mainScreens.userSubscriptionScreen.components.SortPicker
import com.emirsansar.hesapptracker.view.mainScreens.sharedComponents.CustomTopBar
import com.emirsansar.hesapptracker.viewModel.UserSubscriptionViewModel
import kotlinx.coroutines.delay

// Enum class to define sorting options
enum class SortType {
    Default,
    PriceAscending,
    PriceDescending,
    Alphabetical
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
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
    var isSortPickerExpanded by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val appManager = AppManager.getInstance(context)

    LaunchedEffect(Unit) {
        userSubsVM.fetchUserSubscriptionsFromFirestore()
    }

    LaunchedEffect(fetchedUserSubList) {
        displayedUserSubList.clear()
        displayedUserSubList.addAll(fetchedUserSubList)
    }

    LaunchedEffect(appManager.isAnySubscriptionEdited.value) {
        if (appManager.isAnySubscriptionEdited.value) {
            userSubsVM.fetchUserSubscriptionsFromFirestore()

            delay(700)
            appManager.setIsAnySubscriptionEdited(false)
        }
    }

    Scaffold(
        topBar = {
            CustomTopBar(
                title = stringResource(id = R.string.your_subscriptions),
                isDarkMode = appManager.isDarkMode.value,
                onSortButtonClicked = {
                    isSortPickerExpanded = !isSortPickerExpanded
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
                // This box is for SortPicker.
                Box(
                    modifier = Modifier
                        .padding(top = 5.dp)
                        .animateContentSize(animationSpec = tween(durationMillis = 400))
                ) {
                    if (isSortPickerExpanded) {
                        SortPicker(
                            sortType = sortType,
                            onSortTypeChanged = { selectedSortType ->
                                sortType = selectedSortType
                                displayedUserSubList.clear()
                                displayedUserSubList.addAll(sortSubscriptions(fetchedUserSubList, selectedSortType))
                                isSortPickerExpanded = false
                            }
                        )
                    }
                }

                when (fetchingSubsState) {
                    UserSubscriptionViewModel.FetchingSubscriptionsState.SUCCESS -> {
                        if (fetchedUserSubList.isNotEmpty()) {
                            SubscriptionList(
                                subscriptionList = displayedUserSubList,
                                onEdit = { subscription ->
                                    navigateToEditScreen(context, subscription)
                                },
                                onRemove = { subscription ->
                                    removeSubscription(context, userSubsVM, subscription, displayedUserSubList)
                                },
                                appManager.isDarkMode.value
                            )
                        } else {
                            CenteredText(
                                text = stringResource(id = R.string.text_no_subscriptions),
                                isDarkMode = appManager.isDarkMode.value
                            )
                        }
                    }

                    UserSubscriptionViewModel.FetchingSubscriptionsState.FAILURE -> {
                        CenteredText(stringResource(id = R.string.error_fetching_subscription),
                            isDarkMode = appManager.isDarkMode.value
                        )
                    }

                    else -> {
                        CenteredCircularProgress()
                    }
                }
            }
        }
    )
}

// Components:

// Composable function to display a list of subscriptions in a LazyColumn.
@Composable
private fun SubscriptionList(
    subscriptionList: List<UserSubscription>,
    onEdit: (UserSubscription) -> Unit,
    onRemove: (UserSubscription) -> Unit,
    isDarkMode: Boolean
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(all = 10.dp)
    ) {
        items(subscriptionList) { sub ->
            SubscriptionCard(
                sub = sub,
                onEditClick = { onEdit(sub) },
                onRemove = { onRemove(sub) },
                isDarkMode = isDarkMode
            )
        }
    }
}

// Composable function to display a card with subscription details.
@Composable
private fun SubscriptionCard(
    sub: UserSubscription,
    onEditClick: () -> Unit,
    onRemove: () -> Unit,
    isDarkMode: Boolean
) {
    var expandedMenu by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .padding(all = 10.dp)
            .fillMaxWidth()
            .clickable { expandedMenu = true },
        backgroundColor = Color.White
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

            Text(
                text = String.format("%.2f", sub.planPrice / sub.personCount.toDouble()),
                fontSize = 20.sp
            )
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
private fun SubscriptionMenu(
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
            Text(stringResource(id = R.string.button_edit))
        }
        DropdownMenuItem(onClick = {
            onDismiss()
            onRemove()
        }) {
            Text(stringResource(id = R.string.button_remove))
        }
    }
}

@Composable
private fun CenteredText(text: String, isDarkMode: Boolean) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = text, fontSize = 20.sp,
            color = if (isDarkMode) Color.LightGray else Color.DarkGray)
    }
}

@Composable
private fun CenteredCircularProgress() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator(
            modifier = Modifier.size(30.dp),
            color = Color.Blue
        )
    }
}

// Functions:

// Function to sort subscriptions based on selected sort type.
private fun sortSubscriptions(userSubList: List<UserSubscription>, sortType: SortType): List<UserSubscription> {
    return when (sortType) {
        SortType.Default -> userSubList
        SortType.PriceAscending -> userSubList.sortedBy { it.planPrice / it.personCount.toDouble() }
        SortType.PriceDescending -> userSubList.sortedByDescending { it.planPrice / it.personCount.toDouble() }
        SortType.Alphabetical -> userSubList.sortedBy { it.serviceName }
    }
}

private fun removeSubscription(context: Context, userSubsVM: UserSubscriptionViewModel, sub: UserSubscription, displayedUserSubList: MutableList<UserSubscription>) {
    userSubsVM.removeSubscriptionFromUser(sub) { success ->
        if (success) {
            Toast.makeText(context, R.string.text_subscription_removed_successfully, Toast.LENGTH_SHORT).show()
            displayedUserSubList.remove(sub)
        } else {
            Toast.makeText(context, R.string.text_subscription_remove_failed, Toast.LENGTH_SHORT).show()
        }
    }
}

private fun navigateToEditScreen(context: Context, sub: UserSubscription){
    val intent = Intent(context, EditSubscriptionScreen::class.java).apply {
        putExtra("subscription", sub)
    }
    context.startActivity(intent)
}