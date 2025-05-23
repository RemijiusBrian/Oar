package dev.ridill.oar.schedules.presentation.allSchedules

data class AllSchedulesState(
    val showActionPreview: Boolean = false,
    val showNotificationRationale: Boolean = false,
    val multiSelectionModeActive: Boolean = false,
    val selectedScheduleIds: Set<Long> = emptySet(),
    val showDeleteSelectedSchedulesConfirmation: Boolean = false
)