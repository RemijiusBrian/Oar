package dev.ridill.oar.budgetCycles.presentation.cycleHistory

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.zhuinden.flowcombinetuplekt.combineTuple
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.ridill.oar.budgetCycles.domain.repository.BudgetCycleRepository
import dev.ridill.oar.core.domain.util.DateUtil
import dev.ridill.oar.core.domain.util.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.launch
import java.util.Currency
import javax.inject.Inject

@HiltViewModel
class BudgetCyclesViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val repo: BudgetCycleRepository
) : ViewModel(), BudgetCyclesActions {

    val history = repo.getCompletedCycleDetails()
        .cachedIn(viewModelScope)

    private val activeCycle = repo.getActiveCycleDetails()
    private val canCompleteCycle = activeCycle
        .mapLatest { it?.startDate?.isBefore(DateUtil.dateNow()) == true }
        .distinctUntilChanged()
    private val showCycleCompletionWarning = savedStateHandle
        .getStateFlow(SHOW_CYCLE_COMPLETION_WARNING, false)

    private val showCycleOptions = savedStateHandle
        .getStateFlow(SHOW_CYCLE_OPTIONS, false)

    val state = combineTuple(
        activeCycle,
        canCompleteCycle,
        showCycleCompletionWarning,
        showCycleOptions
    ).mapLatest { (
                      activeCycle,
                      canCompleteCycle,
                      showCycleCompletionWarning,
                      showCycleOptions
                  ) ->
        BudgetCyclesState(
            activeCycle = activeCycle,
            showCycleCompleteAction = canCompleteCycle,
            showCycleCompletionWarning = showCycleCompletionWarning,
            showCycleOptions = showCycleOptions
        )
    }.asStateFlow(viewModelScope, BudgetCyclesState())

    override fun onCycleOptionsClick() {
        savedStateHandle[SHOW_CYCLE_OPTIONS] = true
    }

    override fun onCycleOptionsDismiss() {
        savedStateHandle[SHOW_CYCLE_OPTIONS] = false
    }

    override fun onCompleteActiveCycleAction() {
        savedStateHandle[SHOW_CYCLE_COMPLETION_WARNING] = true
    }

    override fun onCompleteActiveCycleDismiss() {
        savedStateHandle[SHOW_CYCLE_COMPLETION_WARNING] = false
    }

    override fun onCompleteActiveCycleConfirm() {
        savedStateHandle[SHOW_CYCLE_COMPLETION_WARNING] = false
        viewModelScope.launch {
            val activeCycle = repo.getActiveCycle() ?: return@launch
            repo.completeCycleNowAndStartNext(activeCycle.id)
        }
    }

    fun onCurrencySelected(currency: Currency) {
        viewModelScope.launch {
            repo.updateCurrencyForActiveCycle(currency)
        }
    }
}

private const val SHOW_CYCLE_OPTIONS = "SHOW_CYCLE_OPTIONS"
private const val SHOW_CYCLE_COMPLETION_WARNING = "SHOW_CYCLE_COMPLETION_WARNING"