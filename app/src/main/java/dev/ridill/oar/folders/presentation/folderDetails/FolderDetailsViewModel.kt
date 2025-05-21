package dev.ridill.oar.folders.presentation.folderDetails

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.zhuinden.flowcombinetuplekt.combineTuple
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.ridill.oar.core.domain.util.DateUtil
import dev.ridill.oar.core.domain.util.EventBus
import dev.ridill.oar.core.domain.util.asStateFlow
import dev.ridill.oar.core.domain.util.orFalse
import dev.ridill.oar.core.domain.util.orZero
import dev.ridill.oar.core.ui.navigation.destinations.FolderDetailsScreenSpec
import dev.ridill.oar.core.ui.util.UiText
import dev.ridill.oar.folders.domain.model.AggregateType
import dev.ridill.oar.folders.domain.repository.FolderDetailsRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FolderDetailsViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val repo: FolderDetailsRepository,
    private val eventBus: EventBus<FolderDetailsEvent>
) : ViewModel(), FolderDetailsActions {

    private val folderIdArg = FolderDetailsScreenSpec
        .getFolderIdArgFromSavedStateHandle(savedStateHandle)

    private val folderIdFlow = MutableStateFlow(folderIdArg)
    private val folderDetails = repo.getFolderDetailsById(folderIdArg)
    private val folderName = folderDetails
        .mapLatest { it?.name.orEmpty() }
        .distinctUntilChanged()
    private val createdTimestamp = folderDetails
        .mapLatest { it?.createdTimestamp ?: DateUtil.now() }
        .distinctUntilChanged()
    private val excluded = folderDetails
        .mapLatest { it?.excluded.orFalse() }
        .distinctUntilChanged()
    private val aggregateAmount = folderDetails
        .mapLatest { it?.aggregate.orZero() }
        .distinctUntilChanged()

    //    private val currency = folderDetails
//        .mapLatest { it?.currency ?: LocaleUtil.defaultCurrency }
//        .distinctUntilChanged()
    private val aggregateType = folderDetails
        .mapLatest { it?.aggregateType ?: AggregateType.BALANCED }
        .distinctUntilChanged()

    val transactionPagingData = repo.getTransactionsInFolderPaged(folderIdArg)
        .cachedIn(viewModelScope)

    private val shouldShowActionPreview = repo.shouldShowActionPreview()

    private val showDeleteConfirmation = savedStateHandle
        .getStateFlow(SHOW_DELETE_CONFIRMATION, false)

    val state = combineTuple(
        folderName,
        createdTimestamp,
        excluded,
        aggregateAmount,
//        currency,
        aggregateType,
        shouldShowActionPreview,
        showDeleteConfirmation
    ).mapLatest { (
                      name,
                      createdTimestamp,
                      excluded,
                      aggregateAmount,
//                currency,
                      aggregateType,
                      shouldShowActionPreview,
                      showDeleteConfirmation
                  ) ->
        FolderDetailsState(
            folderName = name,
            createdTimestamp = createdTimestamp,
            isExcluded = excluded,
            aggregateAmount = aggregateAmount,
//            currency = currency,
            aggregateType = aggregateType,
            shouldShowActionPreview = shouldShowActionPreview,
            showDeleteConfirmation = showDeleteConfirmation
        )
    }.asStateFlow(viewModelScope, FolderDetailsState())

    val events = eventBus.eventFlow

    override fun onDeleteClick() {
        savedStateHandle[SHOW_DELETE_CONFIRMATION] = true
    }

    override fun onDeleteDismiss() {
        savedStateHandle[SHOW_DELETE_CONFIRMATION] = false
    }

    override fun onDeleteFolderOnlyClick() {
        viewModelScope.launch {
            val id = folderIdFlow.value
            repo.deleteFolderById(id)
            savedStateHandle[SHOW_DELETE_CONFIRMATION] = false
            eventBus.send(FolderDetailsEvent.FolderDeleted)
        }
    }

    override fun onDeleteFolderAndTransactionsClick() {
        viewModelScope.launch {
            val id = folderIdFlow.value
            repo.deleteFolderWithTransactions(id)
            savedStateHandle[SHOW_DELETE_CONFIRMATION] = false
            eventBus.send(FolderDetailsEvent.FolderDeleted)
        }
    }

    private var actionPreviewDisableJob: Job? = null
    override fun onTransactionSwipeActionRevealed() {
        actionPreviewDisableJob?.cancel()
        actionPreviewDisableJob = viewModelScope.launch {
            if (shouldShowActionPreview.first()) {
                repo.disableActionPreview()
            }
        }
    }

    override fun onRemoveTransactionFromFolderClick(id: Long) {
        viewModelScope.launch {
            repo.removeTransactionFromFolderById(id)
            eventBus.send(FolderDetailsEvent.TransactionRemovedFromGroup(id))
        }
    }

    fun onRemoveTransactionUndo(txId: Long) = viewModelScope.launch {
        repo.addTransactionToFolder(txId, folderIdArg)
    }

    sealed interface FolderDetailsEvent {
        data class ShowUiMessage(val uiText: UiText) : FolderDetailsEvent
        data object FolderDeleted : FolderDetailsEvent
        data class TransactionRemovedFromGroup(val txId: Long) : FolderDetailsEvent
    }
}

private const val SHOW_DELETE_CONFIRMATION = "SHOW_DELETE_CONFIRMATION"