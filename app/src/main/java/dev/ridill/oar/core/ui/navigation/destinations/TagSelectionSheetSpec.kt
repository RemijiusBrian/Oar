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
import dev.ridill.oar.core.domain.util.Empty
import dev.ridill.oar.core.domain.util.NewLine
import dev.ridill.oar.core.domain.util.orFalse
import dev.ridill.oar.core.ui.components.NavigationResultEffect
import dev.ridill.oar.core.ui.components.navigateUpWithResult
import dev.ridill.oar.tags.presentation.tagSelection.TagSelectionSheet
import dev.ridill.oar.tags.presentation.tagSelection.TagSelectionViewModel

data object TagSelectionSheetSpec : BottomSheetSpec {

    override val route: String
        get() = """
            tag_selection
            /{$ARG_MULTI_SELECTION}
            ?$ARG_PRE_SELECTED_IDS={$ARG_PRE_SELECTED_IDS}
        """.trimIndent()
            .replace(String.NewLine, String.Empty)

    override val labelRes: Int
        get() = R.string.destination_tag_selection

    override val arguments: List<NamedNavArgument>
        get() = listOf(
            navArgument(ARG_MULTI_SELECTION) {
                type = NavType.BoolType
                nullable = false
                defaultValue = false
            },
            navArgument(ARG_PRE_SELECTED_IDS) {
                type = NavType.LongArrayType
                nullable = false
                defaultValue = longArrayOf()
            }
        )

    fun routeWithArgs(
        multiSelection: Boolean,
        preselectedIds: Set<Long> = emptySet()
    ) = buildString {
        val routeWithoutArrayParams = route
            .replace(
                oldValue = "{$ARG_MULTI_SELECTION}",
                newValue = multiSelection.toString()
            )
            .replace(
                oldValue = "$ARG_PRE_SELECTED_IDS={$ARG_PRE_SELECTED_IDS}",
                newValue = String.Empty
            )
        append(routeWithoutArrayParams)
        preselectedIds.forEach {
            append("&$ARG_PRE_SELECTED_IDS=$it")
        }
    }

    fun getMultiSelectionFromSavedStateHandle(savedStateHandle: SavedStateHandle): Boolean =
        savedStateHandle.get<Boolean>(ARG_MULTI_SELECTION).orFalse()

    private fun getMultiSelectionArg(navBackStackEntry: NavBackStackEntry): Boolean =
        navBackStackEntry.arguments?.getBoolean(ARG_MULTI_SELECTION).orFalse()

    fun getPreselectedIdFromSavedStateHandle(savedStateHandle: SavedStateHandle): Set<Long> =
        savedStateHandle.get<LongArray?>(ARG_PRE_SELECTED_IDS)
            ?.toSet()
            .orEmpty()

    const val SELECTED_TAG_IDS = "SELECTED_TAG_IDS"

    @Composable
    override fun Content(
        windowSizeClass: WindowSizeClass,
        navController: NavHostController,
        navBackStackEntry: NavBackStackEntry
    ) {
        val viewModel: TagSelectionViewModel = hiltViewModel(navBackStackEntry)
        val queryState = viewModel.searchQueryState
        val multiSelection = getMultiSelectionArg(navBackStackEntry)
        val selectedIds by viewModel.selectedIds.collectAsStateWithLifecycle()
        val tagsLazyPagingItems = viewModel.tagsPagingData.collectAsLazyPagingItems()

        NavigationResultEffect<Long>(
            resultKey = AddEditTagSheetSpec.SAVED_TAG_ID,
            navBackStackEntry = navBackStackEntry,
            viewModel,
            onResult = viewModel::onItemClick
        )

        TagSelectionSheet(
            queryState = queryState,
            multiSelection = multiSelection,
            tagsLazyPagingItems = tagsLazyPagingItems,
            onDismiss = navController::navigateUp,
            selectedIds = selectedIds,
            onItemClick = viewModel::onItemClick,
            onConfirm = {
                navController.navigateUpWithResult(
                    key = SELECTED_TAG_IDS,
                    result = selectedIds
                )
            },
            navigateToAddEditTag = {
                navController.navigate(AddEditTagSheetSpec.routeWithArg())
            }
        )
    }
}

private const val ARG_MULTI_SELECTION = "ARG_MULTI_SELECTION"
private const val ARG_PRE_SELECTED_IDS = "ARG_PRE_SELECTED_IDS"