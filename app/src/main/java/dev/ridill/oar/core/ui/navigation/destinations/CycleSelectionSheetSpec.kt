package dev.ridill.oar.core.ui.navigation.destinations

import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.paging.compose.collectAsLazyPagingItems
import dev.ridill.oar.R
import dev.ridill.oar.budgetCycles.presentation.cycleSelection.CycleSelectionSheet
import dev.ridill.oar.budgetCycles.presentation.cycleSelection.CycleSelectionViewModel
import dev.ridill.oar.core.ui.components.navigateUpWithResult

object CycleSelectionSheetSpec : BottomSheetSpec {

    override val route: String = "cycle_selection?$ARG_PRE_SELECTED_ID={$ARG_PRE_SELECTED_ID}"

    override val labelRes: Int = R.string.destination_budget_cycle_selection

    override val arguments: List<NamedNavArgument>
        get() = listOf(
            navArgument(ARG_PRE_SELECTED_ID) {
                type = NavType.LongType
                nullable = false
                defaultValue = NavDestination.ARG_INVALID_ID_LONG
            }
        )

    fun routeWithArgs(
        preselectedId: Long? = null
    ): String = route
        .replace(
            oldValue = "{$ARG_PRE_SELECTED_ID}",
            newValue = (preselectedId ?: NavDestination.ARG_INVALID_ID_LONG).toString()
        )

    const val SELECTED_CYCLE_ID = "SELECTED_CYCLE_ID"

    fun getPreselectedIdFromSavedStateHandle(savedStateHandle: SavedStateHandle): Long? =
        savedStateHandle.get<Long>(ARG_PRE_SELECTED_ID)
            ?.takeIf { it > NavDestination.ARG_INVALID_ID_LONG }

    @Composable
    override fun Content(
        windowSizeClass: WindowSizeClass,
        navController: NavHostController,
        navBackStackEntry: NavBackStackEntry
    ) {
        val viewModel: CycleSelectionViewModel = hiltViewModel(navBackStackEntry)
        val queryState = viewModel.query
        val cyclesLazyPagingItems = viewModel.cyclesPagingData.collectAsLazyPagingItems()
        val selectedId by viewModel.selectedId.collectAsStateWithLifecycle()

        CycleSelectionSheet(
            queryState = queryState,
            cyclesLazyPagingItems = cyclesLazyPagingItems,
            selectedId = selectedId,
            onCycleSelect = viewModel::onCycleSelect,
            onDismiss = navController::navigateUp,
            onConfirm = {
                navController.navigateUpWithResult(SELECTED_CYCLE_ID, selectedId)
            }
        )
    }
}

private const val ARG_PRE_SELECTED_ID = "ARG_PRE_SELECTED_ID"