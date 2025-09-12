package dev.ridill.oar.core.ui.navigation.destinations

import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.paging.compose.collectAsLazyPagingItems
import dev.ridill.oar.R
import dev.ridill.oar.core.ui.components.FloatingWindowNavigationResultEffect
import dev.ridill.oar.core.ui.components.NavigationResultEffect
import dev.ridill.oar.core.ui.components.rememberSnackbarController
import dev.ridill.oar.folders.presentation.allFolders.AllFoldersScreen
import dev.ridill.oar.folders.presentation.allFolders.AllFoldersViewModel

data object AllFoldersScreenSpec : ScreenSpec {
    override val route: String
        get() = "all_folders"

    override val labelRes: Int
        get() = R.string.destination_all_folders

    @Composable
    override fun Content(
        windowSizeClass: WindowSizeClass,
        navController: NavHostController,
        navBackStackEntry: NavBackStackEntry
    ) {
        val viewModel: AllFoldersViewModel = hiltViewModel(navBackStackEntry)
        val foldersPagingItems = viewModel.folderListPagingData.collectAsLazyPagingItems()

        val snackbarController = rememberSnackbarController()
        val context = LocalContext.current

        FloatingWindowNavigationResultEffect<Long>(
            resultKey = AddEditFolderSheetSpec.ACTION_FOLDER_SAVED,
            navBackStackEntry = navBackStackEntry,
            viewModel,
            navController
        ) { id ->
            navController.navigate(
                FolderDetailsScreenSpec.routeWithArgs(id)
            )
        }

        NavigationResultEffect<String>(
            resultKey = FolderDetailsScreenSpec.ACTION_FOLDER_DETAILS,
            navBackStackEntry = navBackStackEntry,
            viewModel,
            snackbarController,
            context
        ) {
            when (it) {
                FolderDetailsScreenSpec.RESULT_FOLDER_DELETED -> R.string.transaction_folder_deleted
                else -> null
            }?.let { resId ->
                snackbarController.showSnackbar(context.getString(resId))
            }
        }

        AllFoldersScreen(
            snackbarController = snackbarController,
            foldersPagingItems = foldersPagingItems,
            navigateToFolderDetails = {
                navController.navigate(
                    FolderDetailsScreenSpec.routeWithArgs(it)
                )
            },
            navigateUp = navController::navigateUp,
            navigateToAddFolder = {
                navController.navigate(AddEditFolderSheetSpec.routeWithArg())
            }
        )
    }
}