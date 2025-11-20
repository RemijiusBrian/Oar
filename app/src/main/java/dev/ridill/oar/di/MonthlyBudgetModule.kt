package dev.ridill.oar.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dev.ridill.oar.core.domain.util.EventBus
import dev.ridill.oar.budgetCycles.presentation.budgetUpdate.UpdateBudgetViewModel

@Module
@InstallIn(ViewModelComponent::class)
object MonthlyBudgetModule {

    @Provides
    fun provideUpdateBudgetEventBus(): EventBus<UpdateBudgetViewModel.UpdateBudgetEvent> =
        EventBus()
}