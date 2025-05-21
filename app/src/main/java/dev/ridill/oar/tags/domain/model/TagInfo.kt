package dev.ridill.oar.tags.domain.model

import androidx.compose.ui.graphics.Color
import dev.ridill.oar.core.domain.util.DateUtil
import dev.ridill.oar.folders.domain.model.AggregateType
import java.time.LocalDateTime

data class TagInfo(
    val id: Long,
    val name: String,
    val color: Color,
    val createdTimestamp: LocalDateTime,
    val excluded: Boolean,
    val aggregate: Double
) {
    val createdTimestampFormatted: String
        get() = createdTimestamp.format(DateUtil.Formatters.localizedDateMedium)

    val aggregateType: AggregateType
        get() = AggregateType.fromAmount(aggregate)
}