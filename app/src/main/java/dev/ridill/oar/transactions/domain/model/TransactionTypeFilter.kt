package dev.ridill.oar.transactions.domain.model

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import dev.ridill.oar.R

enum class TransactionTypeFilter(
    @DrawableRes val iconRes: Int,
    @StringRes val labelRes: Int
) {
    DEBITS(R.drawable.ic_rounded_arrow_down_left, R.string.debits),
    ALL(R.drawable.ic_rounded_arrow_up_down, R.string.all),
    CREDITS(R.drawable.ic_rounded_arrow_up_right, R.string.credits);

    companion object {
        fun mapToTransactionType(
            filter: TransactionTypeFilter
        ): TransactionType? = when (filter) {
            DEBITS -> TransactionType.DEBIT
            CREDITS -> TransactionType.CREDIT
            ALL -> null
        }
    }
}