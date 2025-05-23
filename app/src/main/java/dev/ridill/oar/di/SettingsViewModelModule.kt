package dev.ridill.oar.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.ridill.oar.core.data.preferences.PreferencesManager
import dev.ridill.oar.core.data.preferences.security.SecurityPreferencesManager
import dev.ridill.oar.core.domain.crypto.CryptoManager
import dev.ridill.oar.core.domain.util.EventBus
import dev.ridill.oar.settings.data.local.ConfigDao
import dev.ridill.oar.settings.data.repository.BackupSettingsRepositoryImpl
import dev.ridill.oar.settings.data.repository.SettingsRepositoryImpl
import dev.ridill.oar.settings.domain.appInit.AppInitWorkManager
import dev.ridill.oar.settings.domain.backup.BackupWorkManager
import dev.ridill.oar.settings.domain.repositoty.BackupSettingsRepository
import dev.ridill.oar.settings.domain.repositoty.BudgetPreferenceRepository
import dev.ridill.oar.settings.domain.repositoty.CurrencyRepository
import dev.ridill.oar.settings.domain.repositoty.SettingsRepository
import dev.ridill.oar.settings.presentation.backupEncryption.BackupEncryptionViewModel
import dev.ridill.oar.settings.presentation.backupSettings.BackupSettingsViewModel
import dev.ridill.oar.settings.presentation.securitySettings.SecuritySettingsViewModel
import dev.ridill.oar.settings.presentation.settings.SettingsViewModel

@Module
@InstallIn(ViewModelComponent::class)
object SettingsViewModelModule {

    @Provides
    fun provideSettingsRepository(
        preferencesManager: PreferencesManager,
        budgetRepo: BudgetPreferenceRepository,
        currencyRepo: CurrencyRepository
    ): SettingsRepository = SettingsRepositoryImpl(
        preferencesManager = preferencesManager,
        budgetRepo = budgetRepo,
        currencyRepo = currencyRepo
    )

    @Provides
    fun provideSettingsEventBus(): EventBus<SettingsViewModel.SettingsEvent> = EventBus()

    @Provides
    fun provideBackupSettingsEventBus(): EventBus<BackupSettingsViewModel.BackupSettingsEvent> =
        EventBus()

    @Provides
    fun provideBackupSettingsRepository(
        dao: ConfigDao,
        preferencesManager: PreferencesManager,
        securityPreferencesManager: SecurityPreferencesManager,
        backupWorkManager: BackupWorkManager,
        cryptoManager: CryptoManager
    ): BackupSettingsRepository = BackupSettingsRepositoryImpl(
        dao = dao,
        preferencesManager = preferencesManager,
        backupWorkManager = backupWorkManager,
        securityPreferencesManager = securityPreferencesManager,
        cryptoManager = cryptoManager
    )

    @Provides
    fun provideSecuritySettingsEventBus(): EventBus<SecuritySettingsViewModel.SecuritySettingsEvent> =
        EventBus()

    @Provides
    fun provideBackupEncryptionEventBus(): EventBus<BackupEncryptionViewModel.BackupEncryptionEvent> =
        EventBus()

    @Provides
    fun provideAppInitWorkManager(
        @ApplicationContext context: Context
    ): AppInitWorkManager = AppInitWorkManager(context)
}