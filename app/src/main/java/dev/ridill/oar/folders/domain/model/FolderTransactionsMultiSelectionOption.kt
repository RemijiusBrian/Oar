package dev.ridill.oar.folders.domain.model

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import dev.ridill.oar.R

enum class FolderTransactionsMultiSelectionOption(
    @DrawableRes val iconRes: Int,
    @StringRes val labelRes: Int
) {
    DELETE(
        iconRes = R.drawable.ic_rounded_delete,
        labelRes = R.string.action_delete
    ),
    REMOVE_FROM_FOLDERS(
        iconRes = R.drawable.ic_outlined_remove_folder,
        labelRes = R.string.all_transactions_multi_selection_option_remove_from_folders
    ),
}