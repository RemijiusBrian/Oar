package dev.ridill.oar.transactions.presentation.allTransactions

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.clearText
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.saveable
import androidx.paging.cachedIn
import com.zhuinden.flowcombinetuplekt.combineTuple
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.ridill.oar.R
import dev.ridill.oar.core.domain.util.DateUtil
import dev.ridill.oar.core.domain.util.EventBus
import dev.ridill.oar.core.domain.util.UtilConstants
import dev.ridill.oar.core.domain.util.Zero
import dev.ridill.oar.core.domain.util.addOrRemove
import dev.ridill.oar.core.domain.util.asStateFlow
import dev.ridill.oar.core.domain.util.orZero
import dev.ridill.oar.core.domain.util.textAsFlow
import dev.ridill.oar.core.ui.navigation.destinations.AddEditTxResult
import dev.ridill.oar.core.ui.navigation.destinations.NavDestination
import dev.ridill.oar.core.ui.util.UiText
import dev.ridill.oar.tags.domain.repository.TagsRepository
import dev.ridill.oar.transactions.domain.model.AllTransactionsMultiSelectionOption
import dev.ridill.oar.transactions.domain.model.TransactionTypeFilter
import dev.ridill.oar.transactions.domain.repository.AllTransactionsRepository
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.launch
import java.time.Period
import java.time.temporal.TemporalAdjusters
import javax.inject.Inject

@HiltViewModel
class AllTransactionsViewModel @Inject constructor(
    private val transactionRepo: AllTransactionsRepository,
    private val tagsRepo: TagsRepository,
    private val savedStateHandle: SavedStateHandle,
    private val eventBus: EventBus<AllTransactionsEvent>
) : ViewModel(), AllTransactionsActions {

    private val searchModeActive = savedStateHandle.getStateFlow(SEARCH_MODE_ACTIVE, false)
    val searchQueryState = savedStateHandle.saveable(
        key = "SEARCH_QUERY_STATE",
        saver = TextFieldState.Saver,
        init = { TextFieldState() }
    )
    private val dateLimits = transactionRepo.getDateLimits()
        .distinctUntilChanged()
        .asStateFlow(viewModelScope, DateUtil.dateNow() to DateUtil.dateNow())

    private val dateRangeSteps = dateLimits.mapLatest { (start, end) ->
        val period = Period.between(start, end)
        period.toTotalMonths() + 1L
    }.distinctUntilChanged()
    private val dateLimitsAsClosedFloatRange = dateRangeSteps
        .mapLatest { Float.Zero.rangeTo(it.toFloat()) }
        .distinctUntilChanged()

    private val selectedDateRangeFloats = savedStateHandle
        .getStateFlow<Pair<Float, Float>?>(SELECTED_DATE_RANGE_FLOATS, null)

    private val selectedDates = combineTuple(
        dateLimits.mapLatest { it.first },
        dateLimitsAsClosedFloatRange,
        selectedDateRangeFloats
    ).mapLatest { (limitStartDate, limitsFloatRange, selectedRange) ->
        val selectedStart = selectedRange?.first ?: limitsFloatRange.start
        val selectedEnd = selectedRange?.second ?: limitsFloatRange.endInclusive
        val selectedStartDate = limitStartDate
            .plusMonths(selectedStart.toLong())
            .withDayOfMonth(1)
        val selectedEndDate = limitStartDate
            .plusMonths(selectedEnd.toLong().orZero())
            .with(TemporalAdjusters.lastDayOfMonth())

        selectedStartDate to selectedEndDate
    }.distinctUntilChanged()

    private val selectedDateRangeAsClosedFloatRange = combineTuple(
        dateLimitsAsClosedFloatRange,
        selectedDateRangeFloats
    ).mapLatest { (limits, selectedFloats) ->
        val lowerLimit = limits.start
        val upperLimit = limits.endInclusive
        (selectedFloats?.first?.coerceAtLeast(lowerLimit) ?: lowerLimit)
            .rangeTo(
                (selectedFloats?.second?.coerceAtMost(upperLimit) ?: upperLimit)
            )
    }.distinctUntilChanged()

    private val transactionTypeFilter = savedStateHandle
        .getStateFlow(TRANSACTION_TYPE_FILTER, TransactionTypeFilter.ALL)

    private val showExcludedTransactions = transactionRepo.getShowExcludedOption()

    private val selectedTagIds = savedStateHandle
        .getStateFlow<Set<Long>>(SELECTED_TAG_IDS, emptySet())
    private val selectedTags = selectedTagIds.flatMapLatest { ids ->
        tagsRepo.getTagsListFlowByIds(ids)
    }

    private val selectedTransactionIds = savedStateHandle
        .getStateFlow<Set<Long>>(SELECTED_TRANSACTION_IDS, emptySet())
    private val transactionMultiSelectionModeActive = selectedTransactionIds
        .mapLatest { it.isNotEmpty() }
        .distinctUntilChanged()

    val transactionsPagingData = combineTuple(
        selectedDates,
        transactionTypeFilter,
        showExcludedTransactions,
        selectedTagIds
    ).flatMapLatest { (
                          dateRange,
                          typeFilter,
                          showExcluded,
                          tagIds
                      ) ->
        transactionRepo.getAllTransactionsPaged(
            dateRange = dateRange,
            transactionType = TransactionTypeFilter.mapToTransactionType(typeFilter),
            showExcluded = showExcluded,
            tagIds = tagIds
        )
    }.cachedIn(viewModelScope)

    val searchResults = searchQueryState.textAsFlow()
        .debounce(UtilConstants.DEBOUNCE_TIMEOUT)
        .flatMapLatest { query ->
            transactionRepo.getSearchResults(query)
        }.cachedIn(viewModelScope)

    private val isDateFilterActive = combineTuple(
        dateLimitsAsClosedFloatRange,
        selectedDateRangeAsClosedFloatRange
    ).mapLatest { (limits, selected) -> limits != selected }
        .distinctUntilChanged()
    private val isTagFilterActive = selectedTagIds.mapLatest { it.isNotEmpty() }
        .distinctUntilChanged()
    private val isTransactionTypeFilterActive = transactionTypeFilter
        .mapLatest { it != TransactionTypeFilter.ALL }
        .distinctUntilChanged()

    private val areAnyFiltersActive = combineTuple(
        isDateFilterActive,
        isTagFilterActive,
        isTransactionTypeFilterActive,
        transactionMultiSelectionModeActive
    ).mapLatest { (
                      dateFilterActive,
                      tagFilterActive,
                      transactionTypeFilterActive,
                      multiSelectionModeActive
                  ) ->
        dateFilterActive
                || tagFilterActive
                || transactionTypeFilterActive
                || multiSelectionModeActive
    }.distinctUntilChanged()

    private val aggregatesList = combineTuple(
        areAnyFiltersActive,
        selectedDates,
        transactionTypeFilter,
        selectedTagIds,
        showExcludedTransactions,
        selectedTransactionIds
    ).flatMapLatest { (
                          filtersActive,
                          dateRange,
                          typeFilter,
                          selectedTagIds,
                          addExcluded,
                          selectedTxIds
                      ) ->
        if (!filtersActive) flowOf(emptyList())
        else transactionRepo.getAmountAggregate(
            dateRange = dateRange,
            type = TransactionTypeFilter.mapToTransactionType(typeFilter),
            tagIds = selectedTagIds,
            addExcluded = addExcluded,
            selectedTxIds = selectedTxIds
        )
    }.distinctUntilChanged()

    private val transactionListLabel = transactionTypeFilter.mapLatest { type ->
        when {
            type != TransactionTypeFilter.ALL -> UiText.StringResource(type.labelRes)
            else -> UiText.StringResource(R.string.all_transactions)
        }
    }.distinctUntilChanged()

    private val showDeleteTransactionConfirmation = savedStateHandle
        .getStateFlow(SHOW_DELETE_TRANSACTION_CONFIRMATION, false)

    private val showAggregationConfirmation = savedStateHandle
        .getStateFlow(SHOW_AGGREGATION_CONFIRMATION, false)

    private val showMultiSelectionOptions = savedStateHandle
        .getStateFlow(SHOW_MULTI_SELECTION_OPTIONS, false)

    private val showFilterOptions = savedStateHandle
        .getStateFlow(SHOW_FILTER_OPTIONS, false)

    val state = combineTuple(
        searchModeActive,
        dateLimitsAsClosedFloatRange,
        dateRangeSteps,
        selectedDateRangeAsClosedFloatRange,
        selectedDates,
        transactionTypeFilter,
        aggregatesList,
        transactionListLabel,
        selectedTransactionIds,
        transactionMultiSelectionModeActive,
        showDeleteTransactionConfirmation,
        showExcludedTransactions,
        showAggregationConfirmation,
        showMultiSelectionOptions,
        showFilterOptions,
        selectedTags,
        areAnyFiltersActive
    ).mapLatest { (
                      searchModeActive,
                      dateLimitsAsClosedFloatRange,
                      dateRangeSteps,
                      selectedDateRangeAsClosedFloatRange,
                      selectedDates,
                      transactionTypeFilter,
                      aggregatesList,
                      transactionListLabel,
                      selectedTransactionIds,
                      transactionMultiSelectionModeActive,
                      showDeleteTransactionConfirmation,
                      showExcludedTransactions,
                      showAggregationConfirmation,
                      showMultiSelectionOptions,
                      showFilterOptions,
                      selectedTags,
                      areAnyFiltersActive
                  ) ->
        AllTransactionsState(
            searchModeActive = searchModeActive,
            dateLimitsFloatRange = dateLimitsAsClosedFloatRange,
            dateRangeSteps = dateRangeSteps,
            selectedDateRange = selectedDateRangeAsClosedFloatRange,
            selectedDates = selectedDates,
            selectedTransactionTypeFilter = transactionTypeFilter,
            aggregatesList = aggregatesList,
            transactionListLabel = transactionListLabel,
            selectedTransactionIds = selectedTransactionIds,
            transactionMultiSelectionModeActive = transactionMultiSelectionModeActive,
            showDeleteTransactionConfirmation = showDeleteTransactionConfirmation,
            showExcludedTransactions = showExcludedTransactions,
            showAggregationConfirmation = showAggregationConfirmation,
            showMultiSelectionOptions = showMultiSelectionOptions,
            showFilterOptions = showFilterOptions,
            selectedTagFilters = selectedTags,
            areAnyFiltersActive = areAnyFiltersActive
        )
    }.asStateFlow(viewModelScope, AllTransactionsState())

    val events = eventBus.eventFlow

    override fun onSearchClick() {
        savedStateHandle[SEARCH_MODE_ACTIVE] = true
    }

    override fun onSearchModeToggle(active: Boolean) {
        savedStateHandle[SEARCH_MODE_ACTIVE] = active
        if (!active) {
            searchQueryState.clearText()
        }
    }

    override fun onClearSearchQuery() {
        searchQueryState.clearText()
    }

    override fun onClearAllFiltersClick() {
        savedStateHandle[SELECTED_DATE_RANGE_FLOATS] = null
        savedStateHandle[TRANSACTION_TYPE_FILTER] = TransactionTypeFilter.ALL
        savedStateHandle[SELECTED_TAG_IDS] = emptySet<Long>()
        savedStateHandle[SHOW_FILTER_OPTIONS] = false
    }

    override fun onDateFilterRangeChange(range: ClosedFloatingPointRange<Float>) {
        savedStateHandle[SELECTED_DATE_RANGE_FLOATS] = range.start to range.endInclusive
    }


    override fun onDateFilterClear() {
        savedStateHandle[SELECTED_DATE_RANGE_FLOATS] = null
    }

    override fun onTypeFilterSelect(filter: TransactionTypeFilter) {
        savedStateHandle[TRANSACTION_TYPE_FILTER] = filter
    }

    override fun onShowExcludedToggle(showExcluded: Boolean) {
        viewModelScope.launch {
            transactionRepo.toggleShowExcludedOption(showExcluded)
        }
    }

    override fun onTransactionLongPress(id: Long) {
        savedStateHandle[SELECTED_TRANSACTION_IDS] = selectedTransactionIds.value + id
    }

    override fun onTransactionSelectionChange(id: Long) {
        savedStateHandle[SELECTED_TRANSACTION_IDS] = selectedTransactionIds.value.addOrRemove(id)
    }

    override fun onDismissMultiSelectionMode() {
        dismissMultiSelectionMode()
    }

    private fun dismissMultiSelectionMode() {
        savedStateHandle[SELECTED_TRANSACTION_IDS] = emptySet<Long>()
    }

    override fun onMultiSelectionOptionsClick() {
        savedStateHandle[SHOW_MULTI_SELECTION_OPTIONS] = true
    }

    override fun onMultiSelectionOptionsDismiss() {
        savedStateHandle[SHOW_MULTI_SELECTION_OPTIONS] = false
    }

    override fun onMultiSelectionOptionSelect(option: AllTransactionsMultiSelectionOption) {
        savedStateHandle[SHOW_MULTI_SELECTION_OPTIONS] = false
        val selectedTransactionIds = selectedTransactionIds.value.ifEmpty { return }
        viewModelScope.launch {
            when (option) {
                AllTransactionsMultiSelectionOption.DELETE -> {
                    savedStateHandle[SHOW_DELETE_TRANSACTION_CONFIRMATION] = true
                }

                AllTransactionsMultiSelectionOption.ASSIGN_TAG -> {
                    savedStateHandle[TAG_RESULT_PURPOSE] = TAG_RESULT_FOR_ASSIGNMENT
                    eventBus.send(AllTransactionsEvent.NavigateToTagSelection(false, emptySet()))
                }

                AllTransactionsMultiSelectionOption.REMOVE_TAG -> {
                    removeTagForTransactions(selectedTransactionIds)
                }

                AllTransactionsMultiSelectionOption.EXCLUDE_FROM_EXPENDITURE -> {
                    toggleTransactionExclusion(selectedTransactionIds, true)
                }

                AllTransactionsMultiSelectionOption.INCLUDE_IN_EXPENDITURE -> {
                    toggleTransactionExclusion(selectedTransactionIds, false)
                }

                AllTransactionsMultiSelectionOption.ADD_TO_FOLDER -> {
                    showFolderSelection()
                }

                AllTransactionsMultiSelectionOption.REMOVE_FROM_FOLDERS -> {
                    removeTransactionsFromFolders(selectedTransactionIds)
                }

                AllTransactionsMultiSelectionOption.AGGREGATE_TOGETHER -> {
                    savedStateHandle[SHOW_AGGREGATION_CONFIRMATION] = true
                }

                AllTransactionsMultiSelectionOption.CHANGE_CYCLE -> {
                    eventBus.send(AllTransactionsEvent.ChooseCycleForTransactions)
                }
            }
        }
    }

    fun onTagSelectionResult(ids: Set<Long>) = viewModelScope.launch {
        when (savedStateHandle.get<String>(TAG_RESULT_PURPOSE)) {
            TAG_RESULT_FOR_ASSIGNMENT -> {
                ids.firstOrNull()
                    ?.let { assignTagToTransactions(it) }
            }

            TAG_RESULT_FOR_FILTER -> {
                savedStateHandle[SELECTED_TAG_IDS] = ids
            }

            else -> error("Invalid tag result purpose")
        }
    }

    private suspend fun assignTagToTransactions(selectedId: Long) {
        transactionRepo.setTagIdToTransactions(selectedId, selectedTransactionIds.value)
        dismissMultiSelectionMode()
        eventBus.send(AllTransactionsEvent.ShowUiMessage(UiText.StringResource(R.string.tag_assigned_to_transactions)))
    }

    private suspend fun removeTagForTransactions(ids: Set<Long>) {
        transactionRepo.setTagIdToTransactions(null, ids)
        dismissMultiSelectionMode()
        eventBus.send(AllTransactionsEvent.ShowUiMessage(UiText.StringResource(R.string.tag_removed_from_transactions)))
    }

    private suspend fun toggleTransactionExclusion(ids: Set<Long>, excluded: Boolean) {
        transactionRepo.toggleTransactionExclusionByIds(
            ids = ids,
            excluded = excluded
        )
        dismissMultiSelectionMode()
        eventBus.send(
            AllTransactionsEvent.ShowUiMessage(
                UiText.StringResource(
                    if (excluded) R.string.transactions_excluded_from_expenditure
                    else R.string.transactions_included_in_expenditure
                )
            )
        )
    }

    override fun onChangeTagFiltersClick() {
        viewModelScope.launch {
            savedStateHandle[TAG_RESULT_PURPOSE] = TAG_RESULT_FOR_FILTER
            eventBus.send(AllTransactionsEvent.NavigateToTagSelection(true, selectedTagIds.value))
        }
    }

    override fun onClearTagFilterClick() {
        savedStateHandle[SELECTED_TAG_IDS] = emptySet<Long>()
    }

    private suspend fun showFolderSelection() {
        eventBus.send(AllTransactionsEvent.NavigateToFolderSelection)
    }

    private suspend fun removeTransactionsFromFolders(ids: Set<Long>) {
        transactionRepo.removeTransactionsFromFolders(ids)
        dismissMultiSelectionMode()
        eventBus.send(AllTransactionsEvent.ShowUiMessage(UiText.StringResource(R.string.transactions_removed_from_their_folders)))
    }

    fun onFolderSelect(folderId: Long) {
        if (folderId == NavDestination.ARG_INVALID_ID_LONG) return
        viewModelScope.launch {
            val selectedIds = selectedTransactionIds.value
            transactionRepo.addTransactionsToFolderByIds(
                ids = selectedIds,
                folderId = folderId
            )
            eventBus.send(
                AllTransactionsEvent.ShowUiMessage(
                    UiText.PluralResource(
                        R.plurals.transaction_added_to_folder_message,
                        selectedIds.size
                    )
                )
            )
            dismissMultiSelectionMode()
        }
    }

    fun onAddEditTxNavResult(result: AddEditTxResult) = viewModelScope.launch {
        val event = when (result) {
            AddEditTxResult.TRANSACTION_DELETED ->
                AllTransactionsEvent.ShowUiMessage(
                    UiText.PluralResource(
                        R.plurals.transaction_deleted,
                        1
                    )
                )

            AddEditTxResult.TRANSACTION_SAVED ->
                AllTransactionsEvent.ShowUiMessage(UiText.StringResource(R.string.transaction_saved))

            AddEditTxResult.SCHEDULE_SAVED -> AllTransactionsEvent.ScheduleSaved
        }

        eventBus.send(event)
    }

    override fun onDeleteTransactionDismiss() {
        savedStateHandle[SHOW_DELETE_TRANSACTION_CONFIRMATION] = false
    }

    override fun onDeleteTransactionConfirm() {
        val selectedIds = selectedTransactionIds.value
        if (selectedIds.isEmpty()) {
            savedStateHandle[SHOW_DELETE_TRANSACTION_CONFIRMATION] = false
            return
        }
        viewModelScope.launch {
            deleteTransactions(selectedIds)
            savedStateHandle[SHOW_DELETE_TRANSACTION_CONFIRMATION] = false
            dismissMultiSelectionMode()
        }
    }

    private suspend fun deleteTransactions(ids: Set<Long>) {
        transactionRepo.deleteTransactionsByIds(ids)
        eventBus.send(AllTransactionsEvent.ShowUiMessage(UiText.StringResource(R.string.transactions_deleted)))
    }

    override fun onAggregationDismiss() {
        savedStateHandle[SHOW_AGGREGATION_CONFIRMATION] = false
    }

    override fun onAggregationConfirm() {
        viewModelScope.launch {
            val selectedIds = selectedTransactionIds.value
            val dateTimeNow = DateUtil.now()
            val insertedId = transactionRepo.aggregateTogether(
                ids = selectedIds,
                dateTime = dateTimeNow
            )
            savedStateHandle[SHOW_AGGREGATION_CONFIRMATION] = false
            dismissMultiSelectionMode()
            eventBus.send(AllTransactionsEvent.NavigateToAddEditTx(insertedId))
        }
    }

    override fun onFilterOptionsClick() {
        savedStateHandle[SHOW_FILTER_OPTIONS] = true
    }

    override fun onFilterOptionsDismiss() {
        savedStateHandle[SHOW_FILTER_OPTIONS] = false
    }

    fun onCycleSelect(id: Long) = viewModelScope.launch {
        val selectedIds = selectedTransactionIds.value
        transactionRepo.updateCycleForTransactions(selectedIds, id)
        dismissMultiSelectionMode()
        eventBus.send(
            AllTransactionsEvent.ShowUiMessage(
                UiText.PluralResource(
                    R.plurals.cycle_update_for_transaction,
                    selectedIds.size
                )
            )
        )
    }

    sealed interface AllTransactionsEvent {
        data class ShowUiMessage(val uiText: UiText) : AllTransactionsEvent
        data class NavigateToTagSelection(
            val multiSelection: Boolean,
            val preSelectedIds: Set<Long>
        ) : AllTransactionsEvent

        data class NavigateToAddEditTx(val id: Long) : AllTransactionsEvent
        data object NavigateToFolderSelection : AllTransactionsEvent
        data object ScheduleSaved : AllTransactionsEvent
        data object ChooseCycleForTransactions : AllTransactionsEvent
    }
}

private const val SEARCH_MODE_ACTIVE = "SEARCH_MODE_ACTIVE"
private const val SELECTED_DATE_RANGE_FLOATS = "SELECTED_DATE_RANGE_FLOATS"
private const val TRANSACTION_TYPE_FILTER = "TRANSACTION_TYPE_FILTER"
private const val SELECTED_TAG_IDS = "SELECTED_TAG_IDS"
private const val SELECTED_TRANSACTION_IDS = "SELECTED_TRANSACTION_IDS"
private const val SHOW_DELETE_TRANSACTION_CONFIRMATION = "SHOW_DELETE_TRANSACTION_CONFIRMATION"
private const val SHOW_AGGREGATION_CONFIRMATION = "SHOW_AGGREGATION_CONFIRMATION"
private const val SHOW_MULTI_SELECTION_OPTIONS = "SHOW_MULTI_SELECTION_OPTIONS"
private const val SHOW_FILTER_OPTIONS = "SHOW_FILTER_OPTIONS"

private const val TAG_RESULT_PURPOSE = "TAG_RESULT_PURPOSE"
private const val TAG_RESULT_FOR_ASSIGNMENT = "TAG_RESULT_FOR_ASSIGNMENT"
private const val TAG_RESULT_FOR_FILTER = "TAG_RESULT_FOR_FILTER"