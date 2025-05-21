package dev.ridill.oar.transactions.domain.model

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.ui.graphics.Color
import dev.ridill.oar.R
import dev.ridill.oar.core.ui.theme.CreditGreen
import dev.ridill.oar.core.ui.theme.DebitRed

enum class TransactionType(
    @StringRes val labelRes: Int,
    @DrawableRes val iconRes: Int,
    val color: Color
) {
    CREDIT(
        labelRes = R.string.transaction_type_label_credit,
        iconRes = R.drawable.ic_arrow_bottom_right,
        color = CreditGreen
    ),
    DEBIT(
        labelRes = R.string.transaction_type_label_debit,
        iconRes = R.drawable.ic_arrow_top_right,
        color = DebitRed
    ),
}