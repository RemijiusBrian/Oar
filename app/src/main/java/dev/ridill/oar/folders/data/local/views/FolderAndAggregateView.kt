package dev.ridill.oar.folders.data.local.views

import androidx.room.DatabaseView
import dev.ridill.oar.settings.data.local.ConfigKey
import java.time.LocalDateTime

@DatabaseView(
    value = """SELECT fld.id AS id,
        fld.name AS name,
        fld.created_timestamp AS createdTimestamp,
        fld.is_excluded AS excluded,
        IFNULL(SUM(
                CASE
                    WHEN tx.type = 'DEBIT' THEN tx.amount
                    WHEN tx.type = 'CREDIT' THEN -tx.amount
                END
        ), 0) AS aggregate
        FROM folder_table fld
        LEFT OUTER JOIN transaction_table tx ON (
            tx.folder_id = fld.id
            AND tx.is_excluded = 0
            AND tx.cycle_id = (
                SELECT cnf.config_value
                FROM config_table cnf
                WHERE cnf.config_key = '${ConfigKey.ACTIVE_CYCLE_ID}'
            )
        )
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