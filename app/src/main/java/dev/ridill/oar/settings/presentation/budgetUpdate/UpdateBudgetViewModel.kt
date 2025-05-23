package dev.ridill.oar.settings.presentation.budgetUpdate

import androidx.compose.foundation.text.input.TextFieldState
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.ridill.oar.R
import dev.ridill.oar.core.domain.util.EventBus
import dev.ridill.oar.core.domain.util.textAsFlow
import dev.ridill.oar.core.ui.util.UiText
import dev.ridill.oar.settings.domain.repositoty.BudgetPreferenceRepository
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UpdateBudgetViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val repo: BudgetPreferenceRepository,
    private val eventBus: EventBus<UpdateBudgetEvent>
) : ViewModel() {

    val currentBudget = repo.getBudgetPreferenceForMonth()
        .distinctUntilChanged()

    val budgetInputState = TextFieldState()

    val budgetInputError = savedStateHandle
        .getStateFlow<UiText?>(BUDGET_INPUT_ERROR, null)

    val events = eventBus.eventFlow

    init {
        collectBudgetInputState()
    }

    fun onConfirm() = viewModelScope.launch {
        val longValue = budgetInputState.text.toString().toLongOrNull() ?: -1L
        if (longValue <= -1L) {
            savedStateHandle[BUDGET_INPUT_ERROR] = UiText.StringResource(
                R.string.error_invalid_amount,
                true
            )
            return@launch
        }
        repo.saveBudgetPreference(longValue)
        eventBus.send(UpdateBudgetEvent.BudgetUpdated)
    }

    private fun collectBudgetInputState() {
        budgetInputState.textAsFlow()
            .onEach {
                savedStateHandle[BUDGET_INPUT_ERROR] = null
            }.launchIn(viewModelScope)

    }

    sealed interface UpdateBudgetEvent {
        data object BudgetUpdated : UpdateBudgetEvent
    }
}

private const val BUDGET_INPUT_ERROR = "BUDGET_INPUT_ERROR"