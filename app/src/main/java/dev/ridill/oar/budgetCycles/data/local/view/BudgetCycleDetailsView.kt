package dev.ridill.oar.budgetCycles.data.local.view

import androidx.room.DatabaseView
import dev.ridill.oar.settings.data.local.ConfigKey
import java.time.LocalDate

@DatabaseView(
    value = """
        SELECT bdgt.id AS id,
        bdgt.start_date AS startDate,
        bdgt.end_date as endDate,
        bdgt.budget AS budget,
        bdgt.currency_code AS currencyCode,
        CASE
            WHEN cnfg.config_value IS NOT NULL THEN 1
            ELSE 0
        END AS active
        FROM budget_cycle_table bdgt
        LEFT OUTER JOIN config_table cnfg ON (cnfg.config_key = '${ConfigKey.ACTIVE_CYCLE_ID}' AND cnfg.config_value = bdgt.id)
    """,
    viewName = "budget_cycle_details_view"
)
data class BudgetCycleDetailsView(
    val id: Long,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val budget: Long,
    val currencyCode: String,
    val active: Boolean
)