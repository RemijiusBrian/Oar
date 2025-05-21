package dev.ridill.oar.transactions.domain.model

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import dev.ridill.oar.R

enum class AllTransactionsMultiSelectionOption(
    @DrawableRes val iconRes: Int,
    @StringRes val labelRes: Int
) {
    DELETE(
        iconRes = R.drawable.ic_rounded_delete,
        labelRes = R.string.action_delete
    ),
    ASSIGN_TAG(
        iconRes = R.drawable.ic_rounded_tags,
        labelRes = R.string.all_transactions_multi_selection_option_assign_tag
    ),
    REMOVE_TAG(
        iconRes = R.drawable.ic_rounded_untag,
        labelRes = R.string.all_transactions_multi_selection_option_remove_tag
    ),
    EXCLUDE_FROM_EXPENDITURE(
        iconRes = R.drawable.ic_rounded_exclude,
        labelRes = R.string.all_transactions_multi_selection_option_mark_excluded
    ),
    INCLUDE_IN_EXPENDITURE(
        iconRes = R.drawable.ic_rounded_include,
        labelRes = R.string.all_transactions_multi_selection_option_un_mark_excluded
    ),
    ADD_TO_FOLDER(
        iconRes = R.drawable.ic_outline_add_folder,
        labelRes = R.string.all_transactions_multi_selection_option_add_to_folder
    ),
    REMOVE_FROM_FOLDERS(
        iconRes = R.drawable.ic_outline_remove_folder,
        labelRes = R.string.all_transactions_multi_selection_option_remove_from_folders
    ),
    AGGREGATE_TOGETHER(
        iconRes = R.drawable.ic_total,
        labelRes = R.string.all_transactions_multi_selection_option_aggregate_together
    )
}