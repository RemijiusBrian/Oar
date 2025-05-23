package dev.ridill.oar.core.ui.navigation.destinations

import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import dev.ridill.oar.R

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

    }
}