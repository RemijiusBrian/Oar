package dev.ridill.oar.folders.domain.model

sealed class FolderUIModel {
    data class FolderListItem(val folderDetails: FolderDetails) : FolderUIModel()
    data class AggregateTypeSeparator(val type: AggregateType) : FolderUIModel()
}