package dev.ridill.oar.folders.domain.model

import dev.ridill.oar.core.domain.util.DateUtil
import java.time.LocalDateTime

data class FolderDetails(
    val id: Long,
    val name: String,
    val createdTimestamp: LocalDateTime,
    val excluded: Boolean,
) {
    val createdDateFormatted: String
        get() = createdTimestamp.format(DateUtil.Formatters.localizedDateMedium)
}