package dev.ridill.oar.budgetCycles.presentation.budgetUpdate

import androidx.compose.foundation.text.input.TextFieldState
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.saveable
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.ridill.oar.R
import dev.ridill.oar.budgetCycles.domain.repository.BudgetCycleRepository
import dev.ridill.oar.core.domain.util.EventBus
import dev.ridill.oar.core.domain.util.orZero
import dev.ridill.oar.core.domain.util.textAsFlow
import dev.ridill.oar.core.ui.util.UiText
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UpdateBudgetViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val cycleRepo: BudgetCycleRepository,
    private val eventBus: EventBus<UpdateBudgetEvent>
) : ViewModel() {

    val currentBudget = cycleRepo.getActiveCycleFlow()
        .mapLatest { it?.budget.orZero() }
        .distinctUntilChanged()

    val budgetInputState = savedStateHandle.saveable(
        key = "BUDGET_INPUT_STATE",
        saver = TextFieldState.Saver,
        init = { TextFieldState() }
    )

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
        cycleRepo.updateBudgetForActiveCycle(longValue)
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