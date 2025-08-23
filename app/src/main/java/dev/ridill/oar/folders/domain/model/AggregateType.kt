package dev.ridill.oar.folders.domain.model

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.ui.graphics.Color
import dev.ridill.oar.R
import dev.ridill.oar.core.domain.util.Zero
import dev.ridill.oar.core.ui.theme.PositiveGreen
import dev.ridill.oar.core.ui.theme.NegativeRed

enum class AggregateType(
    @StringRes val labelRes: Int,
    @DrawableRes val iconRes: Int,
    val color: Color
) {
    BALANCED(
        labelRes = R.string.aggregate_type_balanced,
        iconRes = R.drawable.ic_rounded_arrow_up_down,
        color = Color.Unspecified
    ),
    AGG_DEBIT(
        labelRes = R.string.aggregate_type_debit,
        iconRes = R.drawable.ic_rounded_arrow_up_right,
        color = NegativeRed
    ),
    AGG_CREDIT(
        labelRes = R.string.aggregate_type_credit,
        iconRes = R.drawable.ic_rounded_arrow_down_left,
        PositiveGreen
    );

    companion object {
        fun fromAmount(amount: Double): AggregateType = when {
            amount == Double.Zero -> BALANCED
            amount < Double.Zero -> AGG_CREDIT
            else -> AGG_DEBIT
        }
    }
}