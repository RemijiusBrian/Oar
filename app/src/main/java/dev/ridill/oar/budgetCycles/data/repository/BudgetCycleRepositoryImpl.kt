package dev.ridill.oar.budgetCycles.data.repository

import androidx.room.withTransaction
import dev.ridill.oar.R
import dev.ridill.oar.aggregations.data.local.AggregationsDao
import dev.ridill.oar.budgetCycles.data.local.BudgetCycleDao
import dev.ridill.oar.budgetCycles.data.local.entity.BudgetCycleEntity
import dev.ridill.oar.budgetCycles.data.toEntry
import dev.ridill.oar.budgetCycles.domain.cycleManager.CycleManager
import dev.ridill.oar.budgetCycles.domain.model.BudgetCycleConfig
import dev.ridill.oar.budgetCycles.domain.model.BudgetCycleEntry
import dev.ridill.oar.budgetCycles.domain.model.BudgetCycleError
import dev.ridill.oar.budgetCycles.domain.model.BudgetCycleSummary
import dev.ridill.oar.budgetCycles.domain.model.CycleStartDay
import dev.ridill.oar.budgetCycles.domain.model.CycleStartDayType
import dev.ridill.oar.budgetCycles.domain.model.CycleStatus
import dev.ridill.oar.budgetCycles.domain.repository.BudgetCycleRepository
import dev.ridill.oar.core.data.db.OarDatabase
import dev.ridill.oar.core.domain.model.Result
import dev.ridill.oar.core.domain.util.DateUtil
import dev.ridill.oar.core.domain.util.LocaleUtil
import dev.ridill.oar.core.domain.util.logD
import dev.ridill.oar.core.domain.util.logE
import dev.ridill.oar.core.domain.util.logI
import dev.ridill.oar.core.domain.util.orZero
import dev.ridill.oar.core.domain.util.rethrowIfCoroutineCancellation
import dev.ridill.oar.core.domain.util.tryOrNull
import dev.ridill.oar.core.ui.util.UiText
import dev.ridill.oar.folders.domain.model.AggregateType
import dev.ridill.oar.settings.data.local.ConfigDao
import dev.ridill.oar.settings.data.local.ConfigKeys
import dev.ridill.oar.settings.data.local.entity.ConfigEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters
import java.util.Currency
import kotlin.math.absoluteValue

private const val TAG = "BudgetCycleRepository"

class BudgetCycleRepositoryImpl(
    private val db: OarDatabase,
    private val cycleDao: BudgetCycleDao,
    private val aggDao: AggregationsDao,
    private val configDao: ConfigDao,
    private val manager: CycleManager
) : BudgetCycleRepository {

    override suspend fun getCycleConfig(): BudgetCycleConfig = withContext(Dispatchers.IO) {
        val budgetAmount = configDao.getBudgetAmount()?.toDoubleOrNull().orZero()
        val budgetCurrency = configDao.getBudgetCurrencyCode()
            ?.let { LocaleUtil.currencyForCode(it) }
            ?: LocaleUtil.defaultCurrency

        val type = configDao.getBudgetStartDayType()
            ?.let { tryOrNull { CycleStartDayType.valueOf(it) } }
            ?: CycleStartDayType.LAST_DAY_OF_MONTH

        val startDay = when (type) {
            CycleStartDayType.LAST_DAY_OF_MONTH -> CycleStartDay.LastDayOfMonth

            CycleStartDayType.SPECIFIC_DAY_OF_MONTH -> {
                val dayOfMonth =
                    configDao.getValueForKey(ConfigKeys.BUDGET_CYCLE_START_DAY_OF_MONTH)
                        ?.toIntOrNull() ?: 1
                CycleStartDay.SpecificDayOfMonth(dayOfMonth)
            }
        }

        return@withContext BudgetCycleConfig(
            budget = budgetAmount,
            currency = budgetCurrency,
            startDay = startDay
        )
    }

    override suspend fun updateScheduleCycleConfig(
        config: BudgetCycleConfig
    ): Unit = withContext(Dispatchers.IO) {
        val budgetUpdateJob = async {
            val entity = ConfigEntity(
                configKey = ConfigKeys.CYCLE_BUDGET_AMOUNT,
                configValue = config.budget.toString()
            )
            configDao.upsert(entity)
        }
        val currencyUpdateJob = async {
            val entity = ConfigEntity(
                configKey = ConfigKeys.CYCLE_BUDGET_CURRENCY_CODE,
                configValue = config.currency.currencyCode
            )
            configDao.upsert(entity)
        }

        val startDayTypeUpdateJob = async {
            val entity = ConfigEntity(
                configKey = ConfigKeys.BUDGET_CYCLE_START_DAY_TYPE,
                configValue = config.startDay.type.name
            )
            configDao.upsert(entity)
        }

        val startDayDataUpdateJob = async {
            when (config.startDay) {
                CycleStartDay.LastDayOfMonth -> null
                is CycleStartDay.SpecificDayOfMonth -> ConfigEntity(
                    configKey = ConfigKeys.BUDGET_CYCLE_START_DAY_OF_MONTH,
                    configValue = config.startDay.dayOfMonth.toString()
                )
            }?.let {
                configDao.upsert(it)
            }
        }


        awaitAll(budgetUpdateJob, currencyUpdateJob, startDayTypeUpdateJob, startDayDataUpdateJob)
    }

    override suspend fun getLastCycle(): BudgetCycleEntry? = withContext(Dispatchers.IO) {
        cycleDao.getLastCycle()?.toEntry()
    }

    override fun scheduleCycleCompletion(cycle: BudgetCycleEntry) {
        manager.scheduleCycleCompletion(
            cycleId = cycle.id,
            endDate = cycle.endDate
                .plusDays(1)
                .atStartOfDay()
        )
    }

    override suspend fun scheduleLastCycleOrNew(): Result<Unit, BudgetCycleError> =
        withContext(Dispatchers.IO) {
            try {
                val lastCycle = getLastCycle()
                logD(TAG) { "lastCycle = $lastCycle" }
                val isLastCycleActiveRightNow = lastCycle?.status == CycleStatus.ACTIVE
                        && lastCycle.endDate > DateUtil.dateNow()
                logD(TAG) { "isLastCycleActiveRightNow = $isLastCycleActiveRightNow" }

                return@withContext if (isLastCycleActiveRightNow) {
                    // Continue Ongoing cycle
                    // Schedule it's completion alarm
                    logI(TAG) { "Continuing ongoing cycle" }
                    scheduleCycleCompletion(lastCycle)
                    Result.Success(Unit)
                } else {
                    logI(TAG) { "Creating new cycle" }
                    val config = getCycleConfig()
                    logD(TAG) { "config = $config" }
                    val dateNow = DateUtil.dateNow()
                    val lastMonthDate = dateNow.withMonth(dateNow.monthValue - 1)
                    val cycleStartDay = config.startDay
                    // Create a new cycle
                    val configStartDate = when (cycleStartDay) {
                        CycleStartDay.LastDayOfMonth -> lastMonthDate
                            .with(TemporalAdjusters.lastDayOfMonth())

                        is CycleStartDay.SpecificDayOfMonth -> lastMonthDate
                            .withDayOfMonth(cycleStartDay.dayOfMonth)
                    }
                    val nextMonthDate = dateNow.withMonth(dateNow.monthValue + 1)
                    val newCycleEndDate = when (cycleStartDay) {
                        CycleStartDay.LastDayOfMonth -> nextMonthDate
                            .with(TemporalAdjusters.lastDayOfMonth())

                        is CycleStartDay.SpecificDayOfMonth -> nextMonthDate
                            .withDayOfMonth(cycleStartDay.dayOfMonth)
                    }
                    createNewCycleAndScheduleCompletion(
                        startDate = configStartDate,
                        endDate = newCycleEndDate,
                        budget = config.budget,
                        currency = config.currency
                    )
                }
            } catch (t: Throwable) {
                t.rethrowIfCoroutineCancellation()
                Result.Error(
                    error = BudgetCycleError.UNKNOWN,
                    message = UiText.StringResource(R.string.error_failed_to_start_cycle, true)
                )
            }
        }

    override suspend fun createNewCycleAndScheduleCompletion(
        startDate: LocalDate,
        endDate: LocalDate,
        budget: Double,
        currency: Currency
    ): Result<Unit, BudgetCycleError> = withContext(Dispatchers.IO) {
        try {
            logI(TAG) { "createNewCycleAndScheduleCompletion() called with: startDate = $startDate, endDate = $endDate, budget = $budget, currency = $currency" }
            val entity = BudgetCycleEntity(
                startDate = startDate,
                endDate = endDate,
                budget = budget,
                currencyCode = currency.currencyCode,
                status = CycleStatus.ACTIVE
            )
            val insertedId = cycleDao.upsert(entity).first()
            if (insertedId == -1L) throw CycleEntryCreationFailedThrowable(entity)
            logD(TAG) { "entity = $entity created with ID = $insertedId" }
            scheduleCycleCompletion(
                entity.copy(
                    id = insertedId
                ).toEntry()
            )
            Result.Success(Unit)
        } catch (t: CycleEntryCreationFailedThrowable) {
            logE(t) { "createNewCycleAndScheduleCompletion" }
            Result.Error(
                error = BudgetCycleError.CREATION_FAILED,
                message = UiText.StringResource(R.string.error_failed_to_start_cycle, true)
            )
        } catch (t: Throwable) {
            t.rethrowIfCoroutineCancellation()
            logE(t) { "createNewCycleAndScheduleCompletion" }
            Result.Error(
                error = BudgetCycleError.UNKNOWN,
                message = UiText.StringResource(R.string.error_unknown, true)
            )
        }
    }

    override suspend fun completeCurrentCycleAndStartNext(
        id: Long
    ): Result<BudgetCycleSummary, BudgetCycleError> = withContext(Dispatchers.IO) {
        logI(TAG) { "completeCurrentCycleAndStartNext() called with ID = $id" }
        try {
            db.withTransaction {
                val cycle = cycleDao.getCycleById(id)?.toEntry()
                    ?: throw CycleNotFoundThrowable(id)
                logD(TAG) { "cycle = $cycle" }

                if (cycle.status != CycleStatus.ACTIVE)
                    throw CycleNotActiveThrowable(id, cycle.status)

                // Change status of current cycle to COMPLETED
                cycleDao.markCycleCompleted(id)
                logI(TAG) { "cycle marked as ${CycleStatus.COMPLETED}" }

                // Create Next Cycle Entry
                val cycleConfig = getCycleConfig()
                val startDay = cycleConfig.startDay
                val nextMonthDate = DateUtil.dateNow().withMonth(DateUtil.dateNow().monthValue + 1)
                val nextEndDate = when (startDay) {
                    CycleStartDay.LastDayOfMonth -> nextMonthDate
                        .with(TemporalAdjusters.lastDayOfMonth())

                    is CycleStartDay.SpecificDayOfMonth -> nextMonthDate
                        .withDayOfMonth(startDay.dayOfMonth)
                }

                val newCycleResult = createNewCycleAndScheduleCompletion(
                    startDate = DateUtil.dateNow(),
                    endDate = nextEndDate,
                    budget = cycleConfig.budget,
                    currency = cycleConfig.currency
                )

                when (newCycleResult) {
                    is Result.Error -> return@withTransaction Result.Error(
                        error = newCycleResult.error,
                        message = newCycleResult.message
                    )

                    is Result.Success -> Unit
                }

                // Get Aggregate
                val aggregateForCycle = aggDao.getAggregateAmountForCycle(id)
                val aggregateType = AggregateType.fromAmount(aggregateForCycle)
                val summary = BudgetCycleSummary(
                    aggregateAmount = aggregateForCycle.absoluteValue,
                    aggregateType = aggregateType,
                    currency = cycleConfig.currency
                )
                logD(TAG) { "summary = $summary" }

                return@withTransaction Result.Success(summary)
            }
        } catch (t: CycleNotFoundThrowable) {
            logE(t) { "completeCurrentCycleAndStartNext" }
            Result.Error(
                error = BudgetCycleError.CYCLE_NOT_FOUND,
                message = UiText.StringResource(R.string.error_cycle_not_found, true)
            )
        } catch (t: CycleNotActiveThrowable) {
            logE(t) { "completeCurrentCycleAndStartNext" }
            Result.Error(
                error = BudgetCycleError.CYCLE_NOT_ACTIVE,
                message = UiText.StringResource(R.string.error_cycle_not_active, true)
            )
        } catch (t: CycleEntryCreationFailedThrowable) {
            logE(t) { "completeCurrentCycleAndStartNext" }
            Result.Error(
                error = BudgetCycleError.CREATION_FAILED,
                message = UiText.StringResource(R.string.error_failed_to_start_cycle, true)
            )
        } catch (t: Throwable) {
            logE(t) { "completeCurrentCycleAndStartNext" }
            t.rethrowIfCoroutineCancellation()
            Result.Error(
                error = BudgetCycleError.UNKNOWN,
                message = UiText.StringResource(R.string.error_unknown)
            )
        }
    }
}

class CycleEntryCreationFailedThrowable(entity: BudgetCycleEntity) :
    IllegalStateException("Failed to create cycle entity $entity")

class CycleNotFoundThrowable(val id: Long) : IllegalStateException("Cycle not found for ID = $id")
class CycleNotActiveThrowable(val id: Long, status: CycleStatus) :
    IllegalStateException("Cycle with ID = $id is not ACTIVE. Current status is '$status'")