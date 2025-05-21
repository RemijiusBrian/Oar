package dev.ridill.oar.tags.presentation.allTags

interface AllTagsActions {
    fun onTagLongPress(id: Long)
    fun onTagSelectionChange(id: Long)
    fun onMultiSelectionModeDismiss()
    fun onDeleteTagsClick()
    fun onDeleteDismiss()
    fun onDeleteConfirm()
}