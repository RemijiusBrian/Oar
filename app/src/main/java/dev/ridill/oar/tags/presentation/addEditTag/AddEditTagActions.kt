package dev.ridill.oar.tags.presentation.addEditTag

import androidx.compose.ui.graphics.Color

interface AddEditTagActions {
    fun onColorSelect(color: Color)
    fun onExclusionChange(excluded: Boolean)
    fun onConfirm()
    fun onDeleteClick()
    fun onDeleteTagDismiss()
    fun onDeleteTagConfirm()
}