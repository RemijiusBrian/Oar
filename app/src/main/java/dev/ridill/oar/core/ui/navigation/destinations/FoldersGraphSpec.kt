package dev.ridill.oar.core.ui.navigation.destinations

import dev.ridill.oar.R

object FoldersGraphSpec : NavGraphSpec, BottomNavDestination {
    override val route: String
        get() = "transaction_folders_graph"

    override val labelRes: Int
        get() = R.string.destination_folders_graph

    override val iconRes: Int
        get() = R.drawable.ic_outlined_folder

    override val precedence: Int
        get() = 2

    override val children: List<NavDestination>
        get() = listOf(
            AllFoldersScreenSpec,
            FolderDetailsScreenSpec,
            AddEditFolderSheetSpec
        )
}