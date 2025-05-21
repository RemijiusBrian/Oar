package dev.ridill.oar.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dev.ridill.oar.core.domain.util.EventBus
import dev.ridill.oar.onboarding.presentation.OnboardingViewModel

@Module
@InstallIn(ViewModelComponent::class)
object OnboardingModule {
    @Provides
    fun provideOnboardingEventBus(): EventBus<OnboardingViewModel.OnboardingEvent> = EventBus()
}