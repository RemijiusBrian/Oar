package dev.ridill.oar.folders.domain.repository

import dev.ridill.oar.folders.domain.model.Folder
import java.time.LocalDateTime

interface AddEditFolderRepository {
    suspend fun getFolderDetails(id: Long): Folder?
    suspend fun saveFolder(
        id: Long,
        name: String,
        timestamp: LocalDateTime,
        excluded: Boolean
    ): Long
}