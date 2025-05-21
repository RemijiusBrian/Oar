package dev.ridill.oar.transactions.presentation.addEditTransaction

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import dev.ridill.oar.R

enum class AddEditTxOption(
    @StringRes val labelRes: Int,
    @DrawableRes val iconRes: Int
) {
    DELETE(
        labelRes = R.string.delete,
        iconRes = R.drawable.ic_rounded_delete
    ),
    CONVERT_TO_SCHEDULE(
        labelRes = R.string.convert_to_schedule,
        iconRes = R.drawable.ic_rounded_time_forward
    ),
    CONVERT_TO_NORMAL_TRANSACTION(
        labelRes = R.string.convert_to_normal_transaction,
        iconRes = R.drawable.ic_filled_coins
    ),
    DUPLICATE(
        labelRes = R.string.duplicate,
        iconRes = R.drawable.ic_rounded_duplicate
    ),
}