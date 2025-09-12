package dev.ridill.oar.core.ui.navigation.destinations

import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.paging.compose.collectAsLazyPagingItems
import dev.ridill.oar.R
import dev.ridill.oar.budgetCycles.presentation.cycleHistory.BudgetCyclesScreenContent
import dev.ridill.oar.budgetCycles.presentation.cycleHistory.BudgetCyclesViewModel
import dev.ridill.oar.core.ui.components.FloatingWindowNavigationResultEffect
import dev.ridill.oar.core.ui.components.OnLifecycleStartEffect
import dev.ridill.oar.core.ui.util.LocalCurrencyPreference
import java.util.Currency

data object BudgetCyclesScreenSpec : ScreenSpec {
    override val route: String
        get() = "budget_cycles"

    override val labelRes: Int
        get() = R.string.destination_budget_cycles

    @Composable
    override fun Content(
        windowSizeClass: WindowSizeClass,
        navController: NavHostController,
        navBackStackEntry: NavBackStackEntry
    ) {
        val viewModel: BudgetCyclesViewModel = hiltViewModel(navBackStackEntry)
        val state by viewModel.state.collectAsStateWithLifecycle()
        val cycleHistory = viewModel.history.collectAsLazyPagingItems()
        val currencyPreference = LocalCurrencyPreference.current

        OnLifecycleStartEffect {
            viewModel.refreshCurrentDate()
        }

        FloatingWindowNavigationResultEffect<Currency>(
            resultKey = CurrencySelectionSheetSpec.SELECTED_CURRENCY,
            navBackStackEntry = navBackStackEntry,
            viewModel,
            onResult = viewModel::onCurrencySelected
        )

        BudgetCyclesScreenContent(
            state = state,
            history = cycleHistory,
            actions = viewModel,
            navigateUp = navController::navigateUp,
            navigateToUpdateBudget = { navController.navigate(UpdateBudgetSheetSpec.route) },
            navigateToCurrencySelection = {
                navController.navigate(
                    CurrencySelectionSheetSpec.routeWithArg(
                        preSelectedCurrencyCode = currencyPreference.currencyCode
                    )
                )
            },
        )
    }
}