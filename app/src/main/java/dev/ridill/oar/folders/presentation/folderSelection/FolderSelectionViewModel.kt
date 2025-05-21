package dev.ridill.oar.folders.presentation.folderSelection

import androidx.compose.foundation.text.input.TextFieldState
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.ridill.oar.core.domain.util.UtilConstants
import dev.ridill.oar.core.domain.util.textAsFlow
import dev.ridill.oar.core.ui.navigation.destinations.FolderSelectionSheetSpec
import dev.ridill.oar.folders.domain.repository.FolderListRepository
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject

@HiltViewModel
class FolderSelectionViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val repo: FolderListRepository
) : ViewModel() {

    val searchQueryState = TextFieldState()

    val selectedFolderId = savedStateHandle
        .getStateFlow<Long?>(SELECTED_FOLDER_ID, null)

    val folderListPaged = searchQueryState.textAsFlow()
        .debounce(UtilConstants.DEBOUNCE_TIMEOUT)
        .flatMapLatest {
            repo.getFoldersListPaged(it)
        }.cachedIn(viewModelScope)

    init {
        savedStateHandle[SELECTED_FOLDER_ID] = FolderSelectionSheetSpec
            .getPreselectedIdFromSavedStateHandle(savedStateHandle)
    }

    fun onFolderSelect(folderId: Long) {
        savedStateHandle[SELECTED_FOLDER_ID] = folderId
            .takeIf { it != selectedFolderId.value }
    }
}

private const val SELECTED_FOLDER_ID = "SELECTED_FOLDER_ID"