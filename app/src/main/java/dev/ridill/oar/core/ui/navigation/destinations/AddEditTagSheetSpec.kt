package dev.ridill.oar.core.ui.navigation.destinations

import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import dev.ridill.oar.R
import dev.ridill.oar.core.ui.components.CollectFlowEffect
import dev.ridill.oar.core.ui.components.navigateUpWithResult
import dev.ridill.oar.tags.presentation.addEditTag.AddEditTagSheet
import dev.ridill.oar.tags.presentation.addEditTag.AddEditTagViewModel

data object AddEditTagSheetSpec : BottomSheetSpec {
    override val labelRes: Int
        get() = R.string.destination_add_edit_transaction

    override val route: String
        get() = "add_edit_tag/{$ARG_TAG_ID}?$ARG_PREFILLED_NAME={$ARG_PREFILLED_NAME}"

    override val arguments: List<NamedNavArgument>
        get() = listOf(
            navArgument(ARG_TAG_ID) {
                type = NavType.LongType
                nullable = false
                defaultValue = NavDestination.ARG_INVALID_ID_LONG
            },
            navArgument(ARG_PREFILLED_NAME) {
                type = NavType.StringType
                nullable = true
                defaultValue = null
            },
        )

    fun routeWithArg(
        tagId: Long? = null,
        prefilledName: String? = null
    ): String = route
        .replace(
            oldValue = "{$ARG_TAG_ID}",
            newValue = (tagId ?: NavDestination.ARG_INVALID_ID_LONG).toString()
        )
        .replace(
            oldValue = "{$ARG_PREFILLED_NAME}",
            newValue = prefilledName.orEmpty()
        )

    fun getTagIdFromSavedStateHandle(savedStateHandle: SavedStateHandle): Long =
        savedStateHandle.get<Long>(ARG_TAG_ID) ?: NavDestination.ARG_INVALID_ID_LONG

    private fun isArgEditMode(navBackStackEntry: NavBackStackEntry): Boolean =
        navBackStackEntry.arguments?.getLong(ARG_TAG_ID) != NavDestination.ARG_INVALID_ID_LONG

    fun getPrefilledNameFromSavedStateHandle(savedStateHandle: SavedStateHandle): String =
        savedStateHandle.get<String?>(ARG_PREFILLED_NAME).orEmpty()

    const val SAVED_TAG_ID = "SAVED_TAG_ID"

    @Composable
    override fun Content(
        windowSizeClass: WindowSizeClass,
        navController: NavHostController,
        navBackStackEntry: NavBackStackEntry
    ) {
        val viewModel: AddEditTagViewModel = hiltViewModel(navBackStackEntry)
        val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
        val input = viewModel.tagInput.collectAsStateWithLifecycle()
        val nameState = viewModel.nameInputState
        val error by viewModel.tagInputError.collectAsStateWithLifecycle()
        val showDeleteTagConfirmation by viewModel.showTagDeleteConfirmation.collectAsStateWithLifecycle()

        val isEditMode = isArgEditMode(navBackStackEntry)

        CollectFlowEffect(
            flow = viewModel.events
        ) { event ->
            when (event) {
                is AddEditTagViewModel.AddEditTagEvent.TagSaved -> {
                    navController.navigateUpWithResult(
                        SAVED_TAG_ID,
                        event.tagId
                    )
                }

                AddEditTagViewModel.AddEditTagEvent.TagDeleted -> {
                    navController.navigateUp()
                }
            }
        }

        AddEditTagSheet(
            isLoading = isLoading,
            nameState = nameState,
            selectedColorCode = { input.value.colorCode },
            excluded = { input.value.excluded },
            errorMessage = error,
            isEditMode = isEditMode,
            onDismiss = navController::navigateUp,
            showDeleteTagConfirmation = showDeleteTagConfirmation,
            actions = viewModel
        )
    }
}

private const val ARG_TAG_ID = "ARG_TAG_ID"
private const val ARG_PREFILLED_NAME = "ARG_PREFILLED_NAME"