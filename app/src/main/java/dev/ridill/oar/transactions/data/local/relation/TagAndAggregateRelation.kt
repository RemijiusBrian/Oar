package dev.ridill.oar.transactions.data.local.relation

import java.time.LocalDateTime

data class TagAndAggregateRelation(
    val id: Long,
    val name: String,
    val colorCode: Int,
    val createdTimestamp: LocalDateTime,
    val excluded: Boolean,
    val aggregate: Double
)