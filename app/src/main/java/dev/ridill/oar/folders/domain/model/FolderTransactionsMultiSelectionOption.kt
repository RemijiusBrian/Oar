package dev.ridill.oar.folders.domain.model

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import dev.ridill.oar.R

enum class FolderTransactionsMultiSelectionOption(
    @DrawableRes val iconRes: Int,
    @StringRes val labelRes: Int
) {
    DELETE(
        iconRes = R.drawable.ic_outlined_delete,
        labelRes = R.string.action_delete
    ),
    REMOVE_FROM_FOLDERS(
        iconRes = R.drawable.ic_outlined_folder_export,
        labelRes = R.string.folder_multi_select_option_remove_from_this_folder
    ),
}