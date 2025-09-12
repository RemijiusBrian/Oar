package dev.ridill.oar.core.ui.navigation.destinations

import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.paging.compose.collectAsLazyPagingItems
import dev.ridill.oar.R
import dev.ridill.oar.core.ui.components.navigateUpWithResult
import dev.ridill.oar.settings.presentation.currencyUpdate.CurrencySelectionSheet
import dev.ridill.oar.settings.presentation.currencyUpdate.CurrencySelectionViewModel

data object CurrencySelectionSheetSpec : BottomSheetSpec {
    override val route: String
        get() = "currency_selection?$ARG_PRE_SELECTED_CURR_CODE={$ARG_PRE_SELECTED_CURR_CODE}"

    override val labelRes: Int
        get() = R.string.destination_select_currency

    override val arguments: List<NamedNavArgument>
        get() = listOf(
            navArgument(ARG_PRE_SELECTED_CURR_CODE) {
                type = NavType.StringType
                nullable = true
                defaultValue = null
            }
        )

    fun routeWithArg(
        preSelectedCurrencyCode: String? = null
    ): String = route
        .replace(
            oldValue = "{$ARG_PRE_SELECTED_CURR_CODE}",
            newValue = preSelectedCurrencyCode.orEmpty()
        )

    const val SELECTED_CURRENCY = "SELECTED_CURRENCY"

    fun getPreselectedCurrencyCodeArg(navBackStackEntry: NavBackStackEntry): String? =
        navBackStackEntry.arguments?.getString(ARG_PRE_SELECTED_CURR_CODE)

    @Composable
    override fun Content(
        windowSizeClass: WindowSizeClass,
        navController: NavHostController,
        navBackStackEntry: NavBackStackEntry
    ) {
        val viewModel: CurrencySelectionViewModel = hiltViewModel(navBackStackEntry)
        val searchQueryState = viewModel.searchQueryState
        val currenciesLazyPagingItems = viewModel.currencyPagingData.collectAsLazyPagingItems()
        val preSelectedCurrencyCode = getPreselectedCurrencyCodeArg(navBackStackEntry)

        CurrencySelectionSheet(
            searchQueryState = searchQueryState,
            selectedCode = preSelectedCurrencyCode,
            currenciesPagingData = currenciesLazyPagingItems,
            onDismiss = navController::popBackStack,
            onConfirm = { navController.navigateUpWithResult(SELECTED_CURRENCY, it) }
        )
    }
}

private const val ARG_PRE_SELECTED_CURR_CODE = "ARG_PRE_SELECTED_CURR_CODE"