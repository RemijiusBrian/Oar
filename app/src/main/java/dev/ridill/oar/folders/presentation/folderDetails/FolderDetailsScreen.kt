package dev.ridill.oar.folders.presentation.folderDetails

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.DeleteForever
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.LastBaseline
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.paging.compose.LazyPagingItems
import dev.ridill.oar.R
import dev.ridill.oar.budgetCycles.domain.model.BudgetCycleEntry
import dev.ridill.oar.budgetCycles.domain.model.CycleIndicator
import dev.ridill.oar.core.domain.util.DateUtil
import dev.ridill.oar.core.domain.util.One
import dev.ridill.oar.core.ui.components.AmountWithTypeIndicator
import dev.ridill.oar.core.ui.components.BackArrowButton
import dev.ridill.oar.core.ui.components.ConfirmationDialog
import dev.ridill.oar.core.ui.components.ExcludedIcon
import dev.ridill.oar.core.ui.components.ListLabel
import dev.ridill.oar.core.ui.components.ListSeparator
import dev.ridill.oar.core.ui.components.MultiActionConfirmationDialog
import dev.ridill.oar.core.ui.components.OarPlainTooltip
import dev.ridill.oar.core.ui.components.OarScaffold
import dev.ridill.oar.core.ui.components.SnackbarController
import dev.ridill.oar.core.ui.components.SpacerExtraSmall
import dev.ridill.oar.core.ui.components.SpacerSmall
import dev.ridill.oar.core.ui.components.SwipeActionsContainer
import dev.ridill.oar.core.ui.components.TitleLargeText
import dev.ridill.oar.core.ui.components.VerticalNumberSpinnerContent
import dev.ridill.oar.core.ui.components.icons.CalendarClock
import dev.ridill.oar.core.ui.components.listEmptyIndicator
import dev.ridill.oar.core.ui.navigation.destinations.FolderDetailsScreenSpec
import dev.ridill.oar.core.ui.theme.PaddingScrollEnd
import dev.ridill.oar.core.ui.theme.spacing
import dev.ridill.oar.core.ui.util.TextFormat
import dev.ridill.oar.core.ui.util.isEmpty
import dev.ridill.oar.core.ui.util.mergedContentDescription
import dev.ridill.oar.folders.domain.model.AggregateType
import dev.ridill.oar.transactions.domain.model.TagIndicator
import dev.ridill.oar.transactions.domain.model.TransactionEntry
import dev.ridill.oar.transactions.domain.model.TransactionListItemUIModel
import dev.ridill.oar.transactions.domain.model.TransactionType
import dev.ridill.oar.transactions.presentation.components.NewTransactionFab
import dev.ridill.oar.transactions.presentation.components.TransactionListItem
import java.time.LocalDateTime
import kotlin.math.absoluteValue

@Composable
fun FolderDetailsScreen(
    snackbarController: SnackbarController,
    state: FolderDetailsState,
    transactionPagingItems: LazyPagingItems<TransactionListItemUIModel>,
    actions: FolderDetailsActions,
    navigateToEditFolder: () -> Unit,
    navigateToAddEditTransaction: (Long?) -> Unit,
    navigateUp: () -> Unit
) {
    val areTransactionsEmpty by remember {
        derivedStateOf { transactionPagingItems.isEmpty() }
    }

    val layoutDirection = LocalLayoutDirection.current
    OarScaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(FolderDetailsScreenSpec.labelRes)) },
                navigationIcon = { BackArrowButton(onClick = navigateUp) },
                actions = {
                    IconButton(onClick = navigateToEditFolder) {
                        Icon(
                            imageVector = Icons.Rounded.Edit,
                            contentDescription = stringResource(R.string.cd_edit_folder)
                        )
                    }

                    IconButton(onClick = actions::onDeleteClick) {
                        Icon(
                            imageVector = Icons.Rounded.DeleteForever,
                            contentDescription = stringResource(R.string.cd_delete_folder)
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            NewTransactionFab(onClick = { navigateToAddEditTransaction(null) })
        },
        snackbarController = snackbarController,
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    top = paddingValues.calculateTopPadding(),
                    start = paddingValues.calculateStartPadding(layoutDirection),
                    end = paddingValues.calculateEndPadding(layoutDirection),
                )
        ) {
            FolderDetails(
                folderName = state.folderName,
                isExcluded = state.isExcluded,
                aggregateAmount = state.aggregateAmount,
                aggregateType = state.aggregateType,
                createdTimestamp = state.createdTimestampFormatted,
                modifier = Modifier
                    .fillMaxWidth()
            )

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize(),
                contentPadding = PaddingValues(
                    top = MaterialTheme.spacing.medium,
                    start = paddingValues.calculateStartPadding(layoutDirection),
                    end = paddingValues.calculateEndPadding(layoutDirection),
                    bottom = paddingValues.calculateBottomPadding() + PaddingScrollEnd
                ),
                verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small)
            ) {
                stickyHeader(
                    key = "TransactionListHeader",
                    contentType = "TransactionListHeader"
                ) {
                    ListLabel(
                        text = stringResource(R.string.transactions),
                        modifier = Modifier
                            .padding(
                                horizontal = MaterialTheme.spacing.medium,
                                vertical = MaterialTheme.spacing.small
                            )
                            .animateItem()
                    )
                }

                listEmptyIndicator(
                    isListEmpty = areTransactionsEmpty,
                    messageRes = R.string.transactions_in_folder_list_empty_message
                )

                repeat(transactionPagingItems.itemCount) { index ->
                    transactionPagingItems[index]?.let { item ->
                        when (item) {
                            is TransactionListItemUIModel.CycleSeparator -> {
                                stickyHeader(
                                    key = "CycleId-${item.cycle.id}",
                                    contentType = CycleIndicator::class
                                ) {
                                    ListSeparator(
                                        label = item.cycle.description,
                                        modifier = Modifier
                                            .animateItem()
                                    )
                                }
                            }

                            is TransactionListItemUIModel.TransactionItem -> {
                                item(
                                    key = item.id,
                                    contentType = TransactionEntry::class
                                ) {
                                    TransactionInFolderItem(
                                        note = item.note,
                                        amount = TextFormat.currency(item.amount, item.currency),
                                        timestamp = item.timestamp,
                                        type = item.type,
                                        tag = item.tag,
                                        excluded = item.excluded,
                                        onClick = { navigateToAddEditTransaction(item.id) },
                                        onRevealed = actions::onTransactionSwipeActionRevealed,
                                        onRemoveFromFolderClick = {
                                            actions.onRemoveTransactionFromFolderClick(item.id)
                                        },
                                        showSwipePreview = state.shouldShowActionPreview && index == 1,
                                        modifier = Modifier
                                            .fillParentMaxWidth()
                                            .animateItem()
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        if (state.showDeleteConfirmation) {
            if (transactionPagingItems.itemCount == 0) {
                ConfirmationDialog(
                    title = pluralStringResource(
                        R.plurals.delete_folders_confirmation_title,
                        Int.One
                    ),
                    content = stringResource(R.string.action_irreversible_message),
                    onConfirm = actions::onDeleteFolderOnlyClick,
                    onDismiss = actions::onDeleteDismiss
                )
            } else {
                MultiActionConfirmationDialog(
                    title = pluralStringResource(
                        R.plurals.delete_folders_confirmation_title,
                        Int.One
                    ),
                    text = stringResource(R.string.action_irreversible_message),
                    primaryActionLabelRes = R.string.delete_folder,
                    additionalNote = stringResource(R.string.delete_folder_confirmation_note),
                    onPrimaryActionClick = actions::onDeleteFolderOnlyClick,
                    secondaryActionLabelRes = R.string.delete_folder_and_transactions,
                    onSecondaryActionClick = actions::onDeleteFolderAndTransactionsClick,
                    onDismiss = actions::onDeleteDismiss
                )
            }
        }
    }
}

@Composable
private fun FolderDetails(
    folderName: String,
    isExcluded: Boolean,
    aggregateAmount: Double,
    aggregateType: AggregateType,
    createdTimestamp: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .padding(horizontal = MaterialTheme.spacing.medium),
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small)
        ) {
            AnimatedVisibility(isExcluded) {
                ExcludedIcon()
            }
            TitleLargeText(folderName)
        }

        AggregateAmountAndCreatedDate(
            aggregateAmount = aggregateAmount,
            aggregateType = aggregateType,
            date = createdTimestamp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = MaterialTheme.spacing.medium)
        )

        HorizontalDivider()
    }
}

@Composable
private fun AggregateAmountAndCreatedDate(
    aggregateAmount: Double,
    aggregateType: AggregateType,
    date: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom
    ) {
        AggregateAmount(
            amount = aggregateAmount,
            type = aggregateType,
            modifier = Modifier
                .weight(weight = Float.One, fill = false)
        )
        SpacerSmall()
        FolderCreatedDate(
            date = date
        )
    }
}

@Composable
private fun FolderCreatedDate(
    date: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            horizontalAlignment = Alignment.End
        ) {
            Text(
                text = stringResource(R.string.created),
                style = MaterialTheme.typography.labelSmall
            )
            Text(
                text = date,
                style = MaterialTheme.typography.bodyMedium
            )
        }
        SpacerSmall()
        Icon(
            imageVector = Icons.Outlined.CalendarClock,
            contentDescription = stringResource(R.string.cd_folder_created_date)
        )
    }
}

@Composable
private fun AggregateAmount(
    amount: Double,
    type: AggregateType,
    modifier: Modifier = Modifier
) {
    val aggregateAmountContentDescription = when (type) {
        AggregateType.BALANCED -> stringResource(R.string.cd_folder_aggregate_amount_balanced)
        else -> stringResource(
            R.string.cd_folder_aggregate_amount_unbalanced,
            TextFormat.number(amount),
            stringResource(type.labelRes)
        )
    }
    Row(
        modifier = modifier
            .mergedContentDescription(aggregateAmountContentDescription)
    ) {
        VerticalNumberSpinnerContent(
            number = amount,
            modifier = Modifier
                .weight(weight = Float.One, fill = false)
                .alignBy(LastBaseline)
        ) {
            AmountWithTypeIndicator(
                value = TextFormat.number(it.absoluteValue),
                type = type
            )
        }

        SpacerExtraSmall()

        Crossfade(
            targetState = type.labelRes,
            label = "AggregateType",
            modifier = Modifier
                .alignBy(LastBaseline)
        ) { resId ->
            Text(
                text = stringResource(resId),
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}

@Composable
private fun TransactionInFolderItem(
    note: String,
    amount: String,
    timestamp: LocalDateTime,
    type: TransactionType,
    tag: TagIndicator?,
    excluded: Boolean,
    onClick: () -> Unit,
    onRemoveFromFolderClick: () -> Unit,
    onRevealed: () -> Unit,
    showSwipePreview: Boolean,
    modifier: Modifier = Modifier
) {
    var isRevealed by remember { mutableStateOf(false) }
    SwipeActionsContainer(
        isRevealed = isRevealed,
        onRevealedChange = { revealed ->
            isRevealed = revealed
            if (revealed) {
                onRevealed()
            }
        },
        actions = {
            OarPlainTooltip(
                tooltipText = stringResource(R.string.cd_remove_from_folder)
            ) {
                IconButton(
                    onClick = {
                        isRevealed = false
                        onRemoveFromFolderClick()
                    }
                ) {
                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.ic_outline_remove_folder),
                        contentDescription = stringResource(R.string.cd_remove_from_folder)
                    )
                }
            }
        },
        animatePreview = showSwipePreview
    ) {
        TransactionListItem(
            note = note,
            amount = amount,
            timeStamp = timestamp,
            leadingContentLine1 = timestamp.format(DateUtil.Formatters.ddth),
            leadingContentLine2 = timestamp.format(DateUtil.Formatters.EEE),
            type = type,
            tag = tag,
            excluded = excluded,
            modifier = modifier
                .clickable(
                    onClick = onClick,
                    onClickLabel = stringResource(R.string.cd_tap_to_edit_transaction)
                )
        )
    }
}