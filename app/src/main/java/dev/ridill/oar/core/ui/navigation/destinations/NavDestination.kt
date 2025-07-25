package dev.ridill.oar.core.ui.navigation.destinations

import androidx.annotation.StringRes

sealed interface NavDestination {
    companion object {
        val allDestinations: List<NavDestination>
            get() = listOf(
                OnboardingScreenSpec,
                DashboardScreenSpec,
                AddEditTransactionGraphSpec,
                AllTransactionsScreenSpec,
                FoldersGraphSpec,
                TagsGraphSpec,
                SchedulesGraphSpec,
                SettingsGraphSpec,
                FolderSelectionSheetSpec,
                AddEditTagSheetSpec,
                TagSelectionSheetSpec,
                CurrencySelectionSheetSpec,
                CycleSelectionSheetSpec
            )

        const val DEEP_LINK_URI = "dev.ridill.oar://app"
        const val ARG_INVALID_ID_LONG = -1L
    }

    val route: String

    @get:StringRes
    val labelRes: Int
}