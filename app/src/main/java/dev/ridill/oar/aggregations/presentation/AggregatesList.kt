package dev.ridill.oar.aggregations.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.BottomAppBarDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import dev.ridill.oar.R
import dev.ridill.oar.core.ui.components.BodyMediumText
import dev.ridill.oar.core.ui.components.FadedVisibility
import dev.ridill.oar.core.ui.components.SpacerSmall
import dev.ridill.oar.core.ui.components.TitleMediumText
import dev.ridill.oar.core.ui.components.VerticalNumberSpinnerContent
import dev.ridill.oar.core.ui.theme.PaddingScrollEnd
import dev.ridill.oar.core.ui.theme.spacing
import dev.ridill.oar.core.ui.util.TextFormat
import dev.ridill.oar.folders.domain.model.AggregateType
import dev.ridill.oar.transactions.domain.model.AggregateAmountItem
import java.util.Currency
import kotlin.math.absoluteValue

@Composable
fun AmountAggregatesList(
    aggregatesList: List<AggregateAmountItem>,
    modifier: Modifier = Modifier,
    insets: WindowInsets = BottomAppBarDefaults.windowInsets
) {
    val insetPadding = insets.asPaddingValues()
    Surface(
        tonalElevation = BottomAppBarDefaults.ContainerElevation,
        color = BottomAppBarDefaults.containerColor,
    ) {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .padding(insetPadding)
                .padding(BottomAppBarDefaults.ContentPadding)
                .padding(vertical = MaterialTheme.spacing.small)
        ) {
            BodyMediumText(
                text = stringResource(R.string.aggregate),
                modifier = Modifier
                    .padding(
                        horizontal = MaterialTheme.spacing.medium,
                        vertical = MaterialTheme.spacing.extraSmall
                    )
            )

            LazyRow(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small),
                contentPadding = PaddingValues(
                    start = MaterialTheme.spacing.medium,
                    end = PaddingScrollEnd
                )
            ) {
                itemsIndexed(
                    items = aggregatesList,
                    key = { _, item -> item.currency.currencyCode },
                    contentType = { _, _ -> AggregateAmountItem::class.java }
                ) { index, item ->
                    AggregateAmount(
                        amount = item.amount.absoluteValue,
                        currency = item.currency,
                        aggregateType = item.aggregateType ?: AggregateType.BALANCED,
                        showTrailingPlus = index != aggregatesList.lastIndex,
                        modifier = Modifier
                            .animateItem()
                    )
                }
            }
        }
    }
}

@Composable
private fun AggregateAmount(
    amount: Double,
    currency: Currency,
    aggregateType: AggregateType,
    showTrailingPlus: Boolean,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
    ) {
        VerticalNumberSpinnerContent(amount) {
            TitleMediumText(
                text = TextFormat.currencyAmount(it, currency),
                color = aggregateType.color
            )
        }

        SpacerSmall()

        FadedVisibility(showTrailingPlus) {
            TitleMediumText("+")
        }
    }
}