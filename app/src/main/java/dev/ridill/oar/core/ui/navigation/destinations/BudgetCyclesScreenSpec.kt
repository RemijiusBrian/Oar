package dev.ridill.oar.core.ui.navigation.destinations

import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.paging.compose.collectAsLazyPagingItems
import dev.ridill.oar.R
import dev.ridill.oar.budgetCycles.presentation.cycleHistory.BudgetCyclesScreenContent
import dev.ridill.oar.budgetCycles.presentation.cycleHistory.BudgetCyclesViewModel

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

        BudgetCyclesScreenContent(
            state = state,
            history = cycleHistory,
            actions = viewModel,
            navigateUp = navController::navigateUp
        )
    }
}