package dev.ridill.oar.dashboard.presentation

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.LastBaseline
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.ui.unit.dp
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import dev.ridill.oar.R
import dev.ridill.oar.account.domain.model.UserAccount
import dev.ridill.oar.core.domain.util.DateUtil
import dev.ridill.oar.core.domain.util.LocaleUtil
import dev.ridill.oar.core.domain.util.One
import dev.ridill.oar.core.domain.util.PartOfDay
import dev.ridill.oar.core.domain.util.WhiteSpace
import dev.ridill.oar.core.ui.components.BodyMediumText
import dev.ridill.oar.core.ui.components.CollapsibleHeaderLayout
import dev.ridill.oar.core.ui.components.DisplaySmallText
import dev.ridill.oar.core.ui.components.ListLabel
import dev.ridill.oar.core.ui.components.OarImage
import dev.ridill.oar.core.ui.components.OarPlainTooltip
import dev.ridill.oar.core.ui.components.OarProgressBar
import dev.ridill.oar.core.ui.components.OarRichTooltip
import dev.ridill.oar.core.ui.components.OarScaffold
import dev.ridill.oar.core.ui.components.OnLifecycleStartEffect
import dev.ridill.oar.core.ui.components.SnackbarController
import dev.ridill.oar.core.ui.components.SpacerMedium
import dev.ridill.oar.core.ui.components.SpacerSmall
import dev.ridill.oar.core.ui.components.TitleMediumText
import dev.ridill.oar.core.ui.components.VerticalNumberSpinnerContent
import dev.ridill.oar.core.ui.components.listEmptyIndicator
import dev.ridill.oar.core.ui.components.rememberCollapsibleHeaderState
import dev.ridill.oar.core.ui.components.rememberSnackbarController
import dev.ridill.oar.core.ui.navigation.destinations.AllTransactionsScreenSpec
import dev.ridill.oar.core.ui.navigation.destinations.BottomNavDestination
import dev.ridill.oar.core.ui.theme.ContentAlpha
import dev.ridill.oar.core.ui.theme.OarTheme
import dev.ridill.oar.core.ui.theme.PaddingScrollEnd
import dev.ridill.oar.core.ui.theme.elevation
import dev.ridill.oar.core.ui.theme.spacing
import dev.ridill.oar.core.ui.util.TextFormat
import dev.ridill.oar.core.ui.util.isEmpty
import dev.ridill.oar.core.ui.util.mergedContentDescription
import dev.ridill.oar.schedules.domain.model.ActiveSchedule
import dev.ridill.oar.schedules.presentation.components.ActiveScheduleItem
import dev.ridill.oar.transactions.domain.model.TransactionEntry
import dev.ridill.oar.transactions.domain.model.TransactionType
import dev.ridill.oar.transactions.presentation.components.NewTransactionFab
import dev.ridill.oar.transactions.presentation.components.TransactionListItem
import kotlinx.coroutines.flow.flowOf

@Composable
fun DashboardScreen(
    snackbarController: SnackbarController,
    recentSpends: LazyPagingItems<TransactionEntry>,
    state: DashboardState,
    navigateToAllTransactions: () -> Unit,
    navigateToAddEditTransaction: (id: Long?, isSchedule: Boolean) -> Unit,
    navigateToBottomNavDestination: (BottomNavDestination) -> Unit
) {
    val areActiveSchedulesEmpty by remember(state.activeSchedules) {
        derivedStateOf { state.activeSchedules.isEmpty() }
    }
    val areRecentSpendsEmpty by remember {
        derivedStateOf { recentSpends.isEmpty() }
    }

    OarScaffold(
        modifier = Modifier
            .fillMaxSize(),
        bottomBar = {
            BottomAppBar(
                actions = {
                    BottomNavDestination.bottomNavDestinations.forEach { destination ->
                        OarPlainTooltip(
                            tooltipText = stringResource(destination.labelRes),
                            focusable = false
                        ) {
                            IconButton(
                                onClick = { navigateToBottomNavDestination(destination) },
                                colors = IconButtonDefaults.iconButtonColors(
                                    contentColor = MaterialTheme.colorScheme.primary
                                )
                            ) {
                                Icon(
                                    imageVector = ImageVector.vectorResource(destination.iconRes),
                                    contentDescription = stringResource(destination.labelRes)
                                )
                            }
                        }
                    }
                },
                floatingActionButton = {
                    NewTransactionFab(
                        onClick = { navigateToAddEditTransaction(null, false) },
                        elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation()
                    )
                }
            )
        },
        snackbarController = snackbarController,
    ) { paddingValues ->
        val collapsibleHeaderState = rememberCollapsibleHeaderState(
            parallaxFactor = 2f
        )
        val headerShape = MaterialTheme.shapes.extraLarge
        CollapsibleHeaderLayout(
            modifier = Modifier
                .fillMaxSize(),
            state = collapsibleHeaderState,
            header = {
                DashboardHeader(
                    signedInUser = state.signedInUser,
                    balance = state.balance,
                    budget = state.monthlyBudgetInclCredits,
                    usageFraction = { state.usagePercent },
                    areActiveSchedulesEmpty = areActiveSchedulesEmpty,
                    activeSchedules = state.activeSchedules,
                    onScheduleClick = { navigateToAddEditTransaction(it.id, true) },
                    contentPadding = PaddingValues(
                        top = paddingValues.calculateTopPadding()
                    ),
                    shape = headerShape,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = MaterialTheme.spacing.small)
                        .padding(bottom = MaterialTheme.spacing.small)
                        .graphicsLayer {
                            val expansionProgress =
                                collapsibleHeaderState.expansionProgress.floatValue
                            alpha = expansionProgress
                            shape = headerShape
                        }
                )
            },
            body = {
                Surface {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                    ) {
                        SpacerSmall()
                        RecentSpendsHeader(
                            amount = state.spentAmount,
                            onAllTransactionsClick = navigateToAllTransactions,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(
                                    horizontal = MaterialTheme.spacing.medium,
                                    vertical = MaterialTheme.spacing.medium
                                )
                        )

                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(Float.One),
                            contentPadding = PaddingValues(
                                bottom = paddingValues.calculateBottomPadding() + PaddingScrollEnd
                            )
                        ) {
                            listEmptyIndicator(
                                isListEmpty = areRecentSpendsEmpty,
                                messageRes = R.string.recent_spends_list_empty_message
                            )

                            items(
                                count = recentSpends.itemCount,
                                key = recentSpends.itemKey { it.id },
                                contentType = recentSpends.itemContentType { "RecentSpendCard" }
                            ) { index ->
                                recentSpends[index]?.let { transaction ->
                                    TransactionListItem(
                                        note = transaction.note,
                                        amount = transaction.amountFormatted,
                                        timeStamp = transaction.timestamp,
                                        leadingContentLine1 = transaction.timestamp.format(DateUtil.Formatters.ddth),
                                        leadingContentLine2 = transaction.timestamp.format(DateUtil.Formatters.EEE),
                                        type = transaction.type,
                                        tag = transaction.tag,
                                        folder = transaction.folder,
                                        modifier = Modifier
                                            .fillParentMaxWidth()
                                            .clickable(
                                                onClick = {
                                                    navigateToAddEditTransaction(
                                                        transaction.id,
                                                        false
                                                    )
                                                },
                                                onClickLabel = stringResource(R.string.cd_tap_to_edit_transaction)
                                            )
                                            .animateItem(),
                                    )
                                }
                            }
                        }
                    }
                }
            }
        )
    }
}

@Composable
private fun DashboardHeader(
    signedInUser: UserAccount?,
    balance: Double,
    budget: Double,
    usageFraction: () -> Float,
    areActiveSchedulesEmpty: Boolean,
    activeSchedules: List<ActiveSchedule>,
    onScheduleClick: (ActiveSchedule) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(),
    shape: CornerBasedShape = MaterialTheme.shapes.large
) {
    Surface(
        modifier = modifier,
        shape = shape,
        tonalElevation = MaterialTheme.elevation.level2
    ) {
        Column(
            modifier = Modifier
                .padding(vertical = MaterialTheme.spacing.large)
                .padding(contentPadding),
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.large)
        ) {
            GreetingBar(
                signedInUser = signedInUser,
                modifier = Modifier
                    .padding(horizontal = MaterialTheme.spacing.medium)
            )
            BalanceAndUsage(
                balance = balance,
                budget = budget,
                usageFraction = usageFraction,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = MaterialTheme.spacing.medium)
            )

            if (!areActiveSchedulesEmpty) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    ListLabel(
                        text = stringResource(R.string.schedules_this_month),
                        modifier = Modifier
                            .padding(horizontal = MaterialTheme.spacing.medium)
                    )

                    ActiveSchedulesRow(
                        activeSchedules = activeSchedules,
                        onScheduleClick = onScheduleClick
                    )
                }
            } else {
                SpacerSmall()
            }
        }
    }
}

@Composable
private fun GreetingBar(
    signedInUser: UserAccount?,
    modifier: Modifier = Modifier
) {
    var partOfDay by remember { mutableStateOf(PartOfDay.MORNING) }

    OnLifecycleStartEffect {
        partOfDay = DateUtil.getPartOfDay()
    }

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small)
    ) {
        signedInUser?.let {
            OarImage(
                url = it.photoUrl,
                contentDescription = it.displayName,
                size = ProfileIconSize,
                placeholderRes = R.drawable.ic_rounded_person,
            )
        }

        Crossfade(
            targetState = partOfDay,
            label = "Greeting",
        ) { part ->
            TitleMediumText(
                text = buildAnnotatedString {
                    withStyle(
                        SpanStyle(
                            fontWeight = FontWeight.Normal,
                            color = LocalContentColor.current.copy(
                                alpha = ContentAlpha.SUB_CONTENT
                            )
                        )
                    ) {
                        append(stringResource(R.string.app_greeting, stringResource(part.labelRes)))
                    }

                    signedInUser?.let {
                        append(String.WhiteSpace)

                        withStyle(SpanStyle(fontWeight = FontWeight.Medium)) {
                            append(it.displayName)
                        }
                    }
                },
            )
        }
    }
}

private val ProfileIconSize = 32.dp

@Composable
private fun BalanceAndUsage(
    balance: Double,
    budget: Double,
    usageFraction: () -> Float,
    modifier: Modifier = Modifier
) {
    val balanceAndBudgetContentDescription = stringResource(
        R.string.cd_balance_and_budget_amounts,
        TextFormat.currencyAmount(balance),
        TextFormat.currencyAmount(budget)
    )

    Column(
        modifier = modifier
    ) {
        BodyMediumText(
            text = stringResource(R.string.your_balance),
            color = LocalContentColor.current.copy(
                alpha = ContentAlpha.SUB_CONTENT
            )
        )

        Row(
            modifier = Modifier
                .mergedContentDescription(balanceAndBudgetContentDescription),
            verticalAlignment = Alignment.Bottom
        ) {
            VerticalNumberSpinnerContent(
                balance,
                modifier = Modifier
                    .alignBy(LastBaseline)
            ) {
                DisplaySmallText(
                    text = TextFormat.currencyAmount(it),
                    modifier = Modifier,
                )
            }

            SpacerSmall()

            VerticalNumberSpinnerContent(
                number = budget,
                modifier = Modifier
                    .alignBy(LastBaseline)
            ) {
                TitleMediumText(
                    text = stringResource(
                        R.string.fwd_slash_amount_value,
                        TextFormat.currencyAmount(amount = it)
                    ),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    color = LocalContentColor.current.copy(
                        alpha = ContentAlpha.SUB_CONTENT
                    ),
                    fontWeight = FontWeight.Normal
                )
            }
        }

        SpacerMedium()

        OarProgressBar(
            progress = usageFraction,
            modifier = Modifier
                .fillMaxWidth(),
        )
    }
}

@Composable
private fun RecentSpendsHeader(
    amount: Double,
    onAllTransactionsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        OarRichTooltip(
            tooltipTitle = stringResource(R.string.spent_this_month),
            tooltipText = stringResource(
                R.string.you_have_spent_a_total_of_amount_so_far_this_month,
                TextFormat.currencyAmount(amount)
            ),
            state = rememberTooltipState(isPersistent = true),
            tooltipColors = TooltipDefaults.richTooltipColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            TitleMediumText(
                text = stringResource(R.string.recent_spends),
                modifier = Modifier
                    .alignBy(LastBaseline)
            )
        }

        TextButton(
            onClick = onAllTransactionsClick,
            modifier = Modifier
                .alignBy(LastBaseline)
        ) {
            Text("${stringResource(AllTransactionsScreenSpec.labelRes)} >")
        }
    }
}

@Composable
private fun ActiveSchedulesRow(
    activeSchedules: List<ActiveSchedule>,
    onScheduleClick: (ActiveSchedule) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small),
        verticalAlignment = Alignment.CenterVertically,
        contentPadding = PaddingValues(
            top = MaterialTheme.spacing.medium,
            start = MaterialTheme.spacing.medium,
            end = PaddingScrollEnd
        )
    ) {
        items(
            items = activeSchedules,
            key = { it.id },
            contentType = { "ActiveSchedule" }
        ) { schedule ->
            ActiveScheduleItem(
                note = schedule.note,
                amount = schedule.amountFormatted,
                type = schedule.type,
                paymentDay = schedule.dayFormatted,
                onClick = { onScheduleClick(schedule) },
                modifier = Modifier
                    .fillParentMaxWidth(ACTIVE_SCHEDULE_WIDTH_FRACTION)
                    .animateItem()
            )
        }
    }
}

private const val ACTIVE_SCHEDULE_WIDTH_FRACTION = 0.80f

@PreviewLightDark
@PreviewScreenSizes
@Composable
private fun PreviewDashboardScreen() {
    OarTheme {
        DashboardScreen(
            state = DashboardState(
                balance = 1_000.0,
                spentAmount = 500.0,
                monthlyBudgetInclCredits = 5_000.0,
                activeSchedules = List(3) {
                    ActiveSchedule(
                        id = it.toLong(),
                        note = null, // "Really long transaction note",
                        amount = 200.0,
                        currency = LocaleUtil.defaultCurrency,
                        type = TransactionType.DEBIT,
                        nextPaymentDateTime = DateUtil.now()
                    )
                }
            ),
            navigateToAllTransactions = {},
            navigateToAddEditTransaction = { _, _ -> },
            snackbarController = rememberSnackbarController(),
            navigateToBottomNavDestination = {},
            recentSpends = flowOf(PagingData.empty<TransactionEntry>()).collectAsLazyPagingItems(),
        )
    }
}