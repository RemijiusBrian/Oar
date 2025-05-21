package dev.ridill.oar.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dev.ridill.oar.core.data.db.OarDatabase
import dev.ridill.oar.core.domain.util.EventBus
import dev.ridill.oar.settings.data.local.BudgetPreferenceDao
import dev.ridill.oar.settings.data.repository.BudgetPreferenceRepositoryImpl
import dev.ridill.oar.settings.domain.repositoty.BudgetPreferenceRepository
import dev.ridill.oar.settings.presentation.budgetUpdate.UpdateBudgetViewModel

@Module
@InstallIn(ViewModelComponent::class)
object MonthlyBudgetModule {

    @Provides
    fun provideBudgetPreferenceDao(database: OarDatabase): BudgetPreferenceDao =
        database.budgetPreferenceDao()

    @Provides
    fun provideBudgetPreferenceRepository(
        dao: BudgetPreferenceDao
    ): BudgetPreferenceRepository = BudgetPreferenceRepositoryImpl(dao)

    @Provides
    fun provideUpdateBudgetEventBus(): EventBus<UpdateBudgetViewModel.UpdateBudgetEvent> =
        EventBus()
}