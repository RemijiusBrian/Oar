package dev.ridill.oar.core.ui.navigation.destinations

import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import dev.ridill.oar.R
import dev.ridill.oar.core.domain.util.Zero
import dev.ridill.oar.core.ui.components.CollectFlowEffect
import dev.ridill.oar.core.ui.components.navigateUpWithResult
import dev.ridill.oar.core.ui.util.TextFormat
import dev.ridill.oar.budgetCycles.presentation.budgetUpdate.UpdateBudgetSheet
import dev.ridill.oar.budgetCycles.presentation.budgetUpdate.UpdateBudgetViewModel

data object UpdateBudgetSheetSpec : BottomSheetSpec {

    override val route: String
        get() = "update_budget"

    override val labelRes: Int
        get() = R.string.destination_update_budget

    const val UPDATE_BUDGET_RESULT = "UPDATE_BUDGET_RESULT"
    const val RESULT_BUDGET_UPDATED = "RESULT_BUDGET_UPDATED"

    @Composable
    override fun Content(
        windowSizeClass: WindowSizeClass,
        navController: NavHostController,
        navBackStackEntry: NavBackStackEntry
    ) {
        val viewModel: UpdateBudgetViewModel = hiltViewModel(navBackStackEntry)
        val currentBudget by viewModel.currentBudget.collectAsStateWithLifecycle(Long.Zero)
        val inputState = viewModel.budgetInputState
        val inputError by viewModel.budgetInputError.collectAsStateWithLifecycle()

        CollectFlowEffect(viewModel.events) { event ->
            when (event) {
                UpdateBudgetViewModel.UpdateBudgetEvent.BudgetUpdated -> {
                    navController.navigateUpWithResult(
                        key = UPDATE_BUDGET_RESULT,
                        result = RESULT_BUDGET_UPDATED
                    )
                }
            }
        }

        UpdateBudgetSheet(
            placeholder = TextFormat.number(currentBudget),
            inputState = inputState,
            onConfirm = viewModel::onConfirm,
            onDismiss = navController::navigateUp,
            errorMessage = inputError
        )
    }
}