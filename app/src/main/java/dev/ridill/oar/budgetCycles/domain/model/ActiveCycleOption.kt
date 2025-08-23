package dev.ridill.oar.budgetCycles.domain.model

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import dev.ridill.oar.R

enum class ActiveCycleOption(
    @DrawableRes val iconRes: Int,
    @StringRes val labelRes: Int
) {
    UPDATE_BUDGET(
        iconRes = R.drawable.ic_outlined_money_security,
        labelRes = R.string.update_budget
    ),
    CHANGE_BASE_CURRENCY(
        iconRes = R.drawable.ic_outlined_exchange,
        labelRes = R.string.change_base_currency
    )
}