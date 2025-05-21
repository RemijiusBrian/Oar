package dev.ridill.oar.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dev.ridill.oar.account.domain.repository.AuthRepository
import dev.ridill.oar.core.domain.util.EventBus
import dev.ridill.oar.dashboard.data.repository.DashboardRepositoryImpl
import dev.ridill.oar.dashboard.domain.repository.DashboardRepository
import dev.ridill.oar.dashboard.presentation.DashboardViewModel
import dev.ridill.oar.schedules.data.local.SchedulesDao
import dev.ridill.oar.settings.domain.repositoty.BudgetPreferenceRepository
import dev.ridill.oar.settings.domain.repositoty.CurrencyRepository
import dev.ridill.oar.transactions.data.local.TransactionDao
import dev.ridill.oar.transactions.domain.repository.TransactionRepository

@Module
@InstallIn(ViewModelComponent::class)
object DashboardModule {
    @Provides
    fun provideDashboardRepository(
        currencyRepo: CurrencyRepository,
        authRepo: AuthRepository,
        budgetRepo: BudgetPreferenceRepository,
        transactionDao: TransactionDao,
        transactionRepo: TransactionRepository,
        schedulesDao: SchedulesDao
    ): DashboardRepository = DashboardRepositoryImpl(
        currencyRepo = currencyRepo,
        authRepo = authRepo,
        budgetRepo = budgetRepo,
        transactionDao = transactionDao,
        transactionRepo = transactionRepo,
        schedulesDao = schedulesDao
    )

    @Provides
    fun provideDashboardEventBus(): EventBus<DashboardViewModel.DashboardEvent> = EventBus()
}