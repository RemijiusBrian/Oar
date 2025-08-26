package dev.ridill.oar.budgetCycles.presentation.cycleSelection

import androidx.compose.foundation.text.input.TextFieldState
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.saveable
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.ridill.oar.budgetCycles.domain.repository.BudgetCycleRepository
import dev.ridill.oar.core.domain.util.UtilConstants
import dev.ridill.oar.core.domain.util.textAsFlow
import dev.ridill.oar.core.ui.navigation.destinations.CycleSelectionSheetSpec
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject

@HiltViewModel
class CycleSelectionViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val repo: BudgetCycleRepository
) : ViewModel() {

    val query = savedStateHandle.saveable(
        key = "SEARCH_QUERY",
        saver = TextFieldState.Saver,
        init = { TextFieldState() }
    )

    val selectedId = savedStateHandle.getStateFlow<Long?>(
        key = SELECTED_ID,
        initialValue = CycleSelectionSheetSpec.getPreselectedIdFromSavedStateHandle(savedStateHandle)
    )

    val cyclesPagingData = query.textAsFlow()
        .debounce(UtilConstants.DEBOUNCE_TIMEOUT)
        .flatMapLatest { query ->
            repo.getCyclesSelectorsPagingData(query)
        }

    fun onCycleSelect(id: Long) {
        savedStateHandle[SELECTED_ID] = id
    }
}

private const val SELECTED_ID = "SELECTED_ID"