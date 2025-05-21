package dev.ridill.oar.tags.presentation.tagSelection

import androidx.compose.foundation.text.input.TextFieldState
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.ridill.oar.core.domain.util.UtilConstants
import dev.ridill.oar.core.domain.util.textAsFlow
import dev.ridill.oar.core.ui.navigation.destinations.TagSelectionSheetSpec
import dev.ridill.oar.tags.domain.repository.TagsRepository
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject

@HiltViewModel
class TagSelectionViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    repo: TagsRepository
) : ViewModel() {

    private val multiSelection = TagSelectionSheetSpec
        .getMultiSelectionFromSavedStateHandle(savedStateHandle)

    val searchQueryState = TextFieldState()

    val selectedIds = savedStateHandle
        .getStateFlow<Set<Long>>(SELECTED_IDS, emptySet())

    val tagsPagingData = searchQueryState.textAsFlow()
        .debounce(UtilConstants.DEBOUNCE_TIMEOUT)
        .flatMapLatest {
            repo.getAllTagsPagingData(it)
        }.cachedIn(viewModelScope)

    init {
        setPreselectedArgIds()
    }

    private fun setPreselectedArgIds() {
        savedStateHandle[SELECTED_IDS] = TagSelectionSheetSpec
            .getPreselectedIdFromSavedStateHandle(savedStateHandle)
    }

    fun onItemClick(id: Long) {
        if (multiSelection) {
            val currentIds = selectedIds.value
            savedStateHandle[SELECTED_IDS] = if (id in currentIds) currentIds - id
            else currentIds + id
        } else {
            savedStateHandle[SELECTED_IDS] = setOf(id)
                .takeIf { selectedIds.value.isEmpty() }
                .orEmpty()
        }
    }
}

private const val SELECTED_IDS = "SELECTED_IDS"