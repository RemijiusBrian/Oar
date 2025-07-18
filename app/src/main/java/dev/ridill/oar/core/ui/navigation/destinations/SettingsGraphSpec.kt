package dev.ridill.oar.core.ui.navigation.destinations

import dev.ridill.oar.R

object SettingsGraphSpec : NavGraphSpec, BottomNavDestination {

    override val route: String
        get() = "settings_graph"

    override val labelRes: Int
        get() = R.string.destination_settings

    override val iconRes: Int
        get() = R.drawable.ic_outlined_settings

    override val precedence: Int
        get() = 0

    override val children: List<NavDestination>
        get() = listOf(
            SettingsScreenSpec,
            UpdateBudgetSheetSpec,
            BackupSettingsScreenSpec,
            BackupEncryptionScreenSpec,
            SecuritySettingsScreenSpec,
            BudgetCyclesScreenSpec
        )
}