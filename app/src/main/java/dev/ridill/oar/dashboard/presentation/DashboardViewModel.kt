package dev.ridill.oar.dashboard.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.zhuinden.flowcombinetuplekt.combineTuple
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.ridill.oar.R
import dev.ridill.oar.core.domain.notification.NotificationHelper
import dev.ridill.oar.core.domain.util.EventBus
import dev.ridill.oar.core.domain.util.asStateFlow
import dev.ridill.oar.core.ui.navigation.destinations.AddEditTxResult
import dev.ridill.oar.core.ui.util.UiText
import dev.ridill.oar.dashboard.domain.repository.DashboardRepository
import dev.ridill.oar.transactions.domain.model.Transaction
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val repo: DashboardRepository,
    private val notificationHelper: NotificationHelper<Transaction>,
    private val eventBus: EventBus<DashboardEvent>
) : ViewModel() {
    private val signedInUser = repo.getSignedInUser()

    private val budget = repo.getCurrentBudget()
    private val totalDebit = repo.getTotalDebitsForCurrentMonth()
    private val totalCredit = repo.getTotalCreditsForCurrentMonth()

    private val budgetInclCredits = combineTuple(
        budget,
        totalCredit
    ).mapLatest { (budget, credit) ->
        budget + credit
    }.distinctUntilChanged()

    private val balance = combineTuple(
        budgetInclCredits,
        totalDebit
    ).mapLatest { (budgetInclCredits, debits) ->
        budgetInclCredits - debits
    }.distinctUntilChanged()

    private val usageFraction = combineTuple(
        budgetInclCredits,
        totalDebit
    ).mapLatest { (
                      budget,
                      debit
                  ) ->
        debit / budget
    }.mapLatest { it.toFloat() }
        .distinctUntilChanged()

    private val activeSchedules = repo.getSchedulesActiveThisMonth()

    val recentSpendsPagingData = repo.getRecentSpends()
        .cachedIn(viewModelScope)

    val state = combineTuple(
        budgetInclCredits,
        totalDebit,
        balance,
        usageFraction,
        activeSchedules,
        signedInUser,
    ).mapLatest { (
                      budgetInclCredits,
                      spentAmount,
                      balance,
                      usageFraction,
                      activeSchedules,
                      signedInUser,
                  ) ->
        DashboardState(
            balance = balance,
            spentAmount = spentAmount,
            usagePercent = usageFraction,
            monthlyBudgetInclCredits = budgetInclCredits,
            activeSchedules = activeSchedules,
            signedInUser = signedInUser,
        )
    }.asStateFlow(viewModelScope, DashboardState())

    val events = eventBus.eventFlow

    init {
        cancelNotifications()
    }

    fun refreshCurrentDate() = repo.refreshCurrentDate()

    fun onNavResult(result: AddEditTxResult) = viewModelScope.launch {
        val event = when (result) {
            AddEditTxResult.TRANSACTION_DELETED ->
                DashboardEvent.ShowUiMessage(UiText.StringResource(R.string.transaction_deleted))

            AddEditTxResult.TRANSACTION_SAVED ->
                DashboardEvent.ShowUiMessage(UiText.StringResource(R.string.transaction_saved))

            AddEditTxResult.SCHEDULE_SAVED -> DashboardEvent.ScheduleSaved
        }

        eventBus.send(event)
    }

    private fun cancelNotifications() {
        notificationHelper.dismissAllNotifications()
    }

    sealed interface DashboardEvent {
        data class ShowUiMessage(val uiText: UiText) : DashboardEvent
        data object ScheduleSaved : DashboardEvent
    }
}