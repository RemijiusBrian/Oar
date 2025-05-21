package dev.ridill.oar.settings.domain.modal

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class BackupDetails(
    val name: String,
    val id: String,
    val timestamp: String,
    val hashSalt: String?
) : Parcelable