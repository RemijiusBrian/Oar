package dev.ridill.oar.tags.presentation.allTags

data class AllTagsState(
    val multiSelectionModeActive: Boolean = false,
    val selectedIds: Set<Long> = emptySet(),
    val showDeleteConfirmation: Boolean = false
)