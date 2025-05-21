package dev.ridill.oar.schedules.presentation.allSchedules

interface AllSchedulesActions {
    fun onNotificationWarningClick()
    fun onNotificationRationaleDismiss()
    fun onNotificationRationaleAgree()
    fun onScheduleActionRevealed()
    fun onMarkSchedulePaidClick(id: Long)
    fun onScheduleLongPress(id: Long)
    fun onScheduleSelectionToggle(id: Long)
    fun onMultiSelectionModeDismiss()
    fun onDeleteSelectedSchedulesClick()
    fun onDeleteSelectedSchedulesDismiss()
    fun onDeleteSelectedSchedulesConfirm()
}