package dev.ridill.oar.core.ui.navigation.destinations

import dev.ridill.oar.R

data object AddEditTransactionGraphSpec : NavGraphSpec {

    override val route: String
        get() = "add_edit_transaction_graph"

    override val children: List<NavDestination>
        get() = listOf(
            AddEditTransactionScreenSpec,
            AmountTransformationSheetSpec
        )
    override val labelRes: Int
        get() = R.string.destination_add_edit_transaction_graph
}