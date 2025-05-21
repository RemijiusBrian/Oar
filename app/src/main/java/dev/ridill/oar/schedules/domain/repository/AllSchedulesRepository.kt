package dev.ridill.oar.schedules.domain.repository

import androidx.paging.PagingData
import dev.ridill.oar.core.domain.model.Error
import dev.ridill.oar.core.domain.model.Result
import dev.ridill.oar.schedules.domain.model.ScheduleListItemUiModel
import kotlinx.coroutines.flow.Flow

interface AllSchedulesRepository {
    fun refreshCurrentDate()
    fun getSchedulesPagingData(): Flow<PagingData<ScheduleListItemUiModel>>
    suspend fun markScheduleAsPaid(id: Long): Result<Unit, ScheduleError>
    suspend fun deleteSchedulesById(ids: Set<Long>)
    fun shouldShowActionPreview(): Flow<Boolean>
    suspend fun disableActionPreview()
}

enum class ScheduleError : Error {
    SCHEDULE_NOT_FOUND,
    UNKNOWN
}