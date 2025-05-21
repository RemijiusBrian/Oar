package dev.ridill.oar.transactions.domain.model

import dev.ridill.oar.core.ui.util.TextFormat
import dev.ridill.oar.folders.domain.model.AggregateType
import java.util.Currency
import kotlin.math.absoluteValue

data class AggregateAmountItem(
    val amount: Double,
    val currency: Currency
) {
    val amountFormatted: String
        get() = TextFormat.currency(amount.absoluteValue, currency)

    val aggregateType: AggregateType?
        get() = AggregateType.fromAmount(amount)
}