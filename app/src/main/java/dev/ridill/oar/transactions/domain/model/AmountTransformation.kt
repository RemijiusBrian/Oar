package dev.ridill.oar.transactions.domain.model

import androidx.annotation.StringRes
import dev.ridill.oar.R

enum class AmountTransformation(
    @StringRes val labelRes: Int,
    val symbol: String
) {
    DIVIDE_BY(R.string.amount_transformation_label_divide_by, "/"),
    MULTIPLIER(R.string.amount_transformation_label_multiplier, "x"),
    PERCENT(R.string.amount_transformation_label_percent, "%")
}