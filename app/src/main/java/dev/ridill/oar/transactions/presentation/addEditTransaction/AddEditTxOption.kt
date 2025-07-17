package dev.ridill.oar.transactions.presentation.addEditTransaction

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import dev.ridill.oar.R

enum class AddEditTxOption(
    @param:StringRes val labelRes: Int,
    @param:DrawableRes val iconRes: Int
) {
    DELETE(
        labelRes = R.string.delete,
        iconRes = R.drawable.ic_outlined_delete
    ),
    CONVERT_TO_SCHEDULE(
        labelRes = R.string.convert_to_schedule,
        iconRes = R.drawable.ic_time_schedule
    ),
    CONVERT_TO_NORMAL_TRANSACTION(
        labelRes = R.string.convert_to_normal_transaction,
        iconRes = R.drawable.ic_outlined_coins
    ),
    DUPLICATE(
        labelRes = R.string.duplicate,
        iconRes = R.drawable.ic_outlined_duplicate
    ),
}