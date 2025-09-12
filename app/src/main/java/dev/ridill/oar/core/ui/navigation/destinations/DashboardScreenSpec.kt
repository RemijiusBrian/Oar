package dev.ridill.oar.core.ui.navigation.destinations

import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.paging.compose.collectAsLazyPagingItems
import dev.ridill.oar.R
import dev.ridill.oar.core.ui.components.CollectFlowEffect
import dev.ridill.oar.core.ui.components.NavigationResultEffect
import dev.ridill.oar.core.ui.components.rememberSnackbarController
import dev.ridill.oar.dashboard.presentation.DashboardScreen
import dev.ridill.oar.dashboard.presentation.DashboardViewModel

data object DashboardScreenSpec : ScreenSpec {
    override val route: String
        get() = "dashboard"

    override val labelRes: Int
        get() = R.string.destination_dashboard

    @Composable
    override fun Content(
        windowSizeClass: WindowSizeClass,
        navController: NavHostController,
        navBackStackEntry: NavBackStackEntry
    ) {
        val viewModel: DashboardViewModel = hiltViewModel(navBackStackEntry)
        val state by viewModel.state.collectAsStateWithLifecycle()
        val recentSpendsLazyPagingItems =
            viewModel.recentSpendsPagingData.collectAsLazyPagingItems()

        val snackbarController = rememberSnackbarController()
        val context = LocalContext.current

        NavigationResultEffect(
            resultKey = AddEditTxResult::name.name,
            navBackStackEntry = navBackStackEntry,
            viewModel,
            onResult = viewModel::onNavResult
        )

        CollectFlowEffect(
            flow = viewModel.events,
            snackbarController,
            context
        ) { event ->
            when (event) {
                DashboardViewModel.DashboardEvent.ScheduleSaved -> {
                    snackbarController.showSnackbar(
                        message = context.getString(R.string.schedule_saved),
                        actionLabel = context.getString(R.string.action_view),
                        onSnackbarResult = { result ->
                            if (result == SnackbarResult.ActionPerformed) {
                                navController.navigate(SchedulesGraphSpec.route)
                            }
                        }
                    )
                }

                is DashboardViewModel.DashboardEvent.ShowUiMessage -> {
                    snackbarController.showSnackbar(event.uiText.asString(context))
                }
            }
        }

        DashboardScreen(
            snackbarController = snackbarController,
            recentSpends = recentSpendsLazyPagingItems,
            state = state,
            navigateToAllTransactions = {
                navController.navigate(AllTransactionsScreenSpec.route)
            },
            navigateToAddEditTransaction = { id, isSchedule ->
                navController.navigate(
                    AddEditTransactionScreenSpec.routeWithArg(
                        transactionId = id,
                        isScheduleTxMode = isSchedule
                    )
                )
            },
            navigateToBottomNavDestination = {
                navController.navigate(it.route)
            }
        )
    }
}