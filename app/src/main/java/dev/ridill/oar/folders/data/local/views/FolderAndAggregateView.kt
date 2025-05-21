package dev.ridill.oar.folders.data.local.views

import androidx.room.DatabaseView
import java.time.LocalDateTime

@DatabaseView(
    value = """SELECT fld.id AS id,
        fld.name AS name,
        fld.created_timestamp AS createdTimestamp,
        fld.is_excluded as excluded,
        IFNULL(SUM(
                CASE
                    WHEN tx.type = 'DEBIT' THEN tx.amount
                    WHEN tx.type = 'CREDIT' THEN -tx.amount
                END
        ), 0) as aggregate
        FROM folder_table fld
        LEFT OUTER JOIN transaction_table tx ON (tx.folder_id = fld.id AND tx.is_excluded = 0)
        GROUP BY fld.id""",
    viewName = "folder_and_aggregate_view"
)
data class FolderAndAggregateView(
    val id: Long,
    val name: String,
    val createdTimestamp: LocalDateTime,
    val excluded: Boolean,
    val aggregate: Double
)