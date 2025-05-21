package dev.ridill.oar.tags.data

import androidx.compose.ui.graphics.Color
import dev.ridill.oar.tags.data.local.entity.TagEntity
import dev.ridill.oar.tags.domain.model.Tag
import dev.ridill.oar.tags.domain.model.TagInfo
import dev.ridill.oar.transactions.data.local.relation.TagAndAggregateRelation

fun TagEntity.toTag(): Tag = Tag(
    id = id,
    name = name,
    colorCode = colorCode,
    createdTimestamp = createdTimestamp,
    excluded = isExcluded
)

fun TagAndAggregateRelation.toTagInfo(): TagInfo = TagInfo(
    id = id,
    name = name,
    color = Color(colorCode),
    createdTimestamp = createdTimestamp,
    excluded = excluded,
    aggregate = aggregate
)