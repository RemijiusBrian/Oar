package dev.ridill.oar.core.ui.navigation.destinations

import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.paging.compose.collectAsLazyPagingItems
import dev.ridill.oar.R
import dev.ridill.oar.core.ui.components.rememberSnackbarController
import dev.ridill.oar.tags.presentation.allTags.AllTagsScreen
import dev.ridill.oar.tags.presentation.allTags.AllTagsViewModel

data object AllTagsScreenSpec : ScreenSpec {
    override val route: String
        get() = "all_tags"

    override val labelRes: Int
        get() = R.string.destination_all_tags

    @Composable
    override fun Content(
        windowSizeClass: WindowSizeClass,
        navController: NavHostController,
        navBackStackEntry: NavBackStackEntry
    ) {
        val viewModel: AllTagsViewModel = hiltViewModel(navBackStackEntry)
        val searchQueryState = viewModel.searchQueryState
        val tagsLazyPagingItems = viewModel.allTagsPagingData.collectAsLazyPagingItems()
        val state by viewModel.state.collectAsStateWithLifecycle()

        val snackbarController = rememberSnackbarController()

        AllTagsScreen(
            snackbarController = snackbarController,
            tagsLazyPagingItems = tagsLazyPagingItems,
            tagSearchQueryState = searchQueryState,
            state = state,
            actions = viewModel,
            navigateUp = navController::navigateUp,
            navigateToAddEditTag = { navController.navigate(AddEditTagSheetSpec.routeWithArg(it)) }
        )
    }
}