package dev.ridill.oar.di

import android.content.Context
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dev.ridill.oar.BuildConfig
import dev.ridill.oar.account.domain.repository.AuthRepository
import dev.ridill.oar.account.domain.service.AccessTokenService
import dev.ridill.oar.budgetCycles.domain.repository.BudgetCycleRepository
import dev.ridill.oar.core.data.db.OarDatabase
import dev.ridill.oar.core.data.preferences.PreferencesManager
import dev.ridill.oar.core.data.preferences.security.SecurityPreferencesManager
import dev.ridill.oar.core.domain.crypto.CryptoManager
import dev.ridill.oar.core.domain.notification.NotificationHelper
import dev.ridill.oar.schedules.domain.repository.SchedulesRepository
import dev.ridill.oar.settings.data.local.ConfigDao
import dev.ridill.oar.settings.data.local.CurrencyListDao
import dev.ridill.oar.settings.data.remote.GDriveApi
import dev.ridill.oar.settings.data.remote.interceptors.GoogleAccessTokenInterceptor
import dev.ridill.oar.settings.data.repository.AppInitRepositoryImpl
import dev.ridill.oar.settings.data.repository.BackupRepositoryImpl
import dev.ridill.oar.settings.domain.appLock.AppLockServiceManager
import dev.ridill.oar.settings.domain.backup.BackupService
import dev.ridill.oar.settings.domain.backup.BackupWorkManager
import dev.ridill.oar.settings.domain.notification.AppInitNotificationHelper
import dev.ridill.oar.settings.domain.notification.BackupNotificationHelper
import dev.ridill.oar.settings.domain.repositoty.AppInitRepository
import dev.ridill.oar.settings.domain.repositoty.BackupRepository
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SettingsSingletonModule {

    @Provides
    fun provideConfigDao(database: OarDatabase): ConfigDao = database.configDao()

    @GoogleApis
    @Provides
    fun provideGoogleAccessTokenInterceptor(
        tokenService: AccessTokenService
    ): GoogleAccessTokenInterceptor = GoogleAccessTokenInterceptor(
        tokenService = tokenService
    )

    @Provides
    fun provideLoggingInterceptor(): HttpLoggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    @GoogleApis
    @Provides
    fun provideGoogleApisHttpClient(
        @GoogleApis googleAccessTokenInterceptor: GoogleAccessTokenInterceptor,
        loggingInterceptor: HttpLoggingInterceptor
    ): OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(googleAccessTokenInterceptor)
        .addInterceptor(loggingInterceptor)
        .build()

    @GoogleApis
    @Singleton
    @Provides
    fun provideGoogleApisRetrofit(
        @GoogleApis client: OkHttpClient
    ): Retrofit = Retrofit.Builder()
        .addConverterFactory(
            GsonConverterFactory.create(
                GsonBuilder()
                    .excludeFieldsWithoutExposeAnnotation()
                    .create()
            )
        )
        .baseUrl(BuildConfig.GOOGLE_APIS_BASE_URL)
        .client(client)
        .build()

    @Provides
    fun provideGDriveApi(@GoogleApis retrofit: Retrofit): GDriveApi =
        retrofit.create(GDriveApi::class.java)

    @Provides
    fun provideBackupService(
        @ApplicationContext context: Context,
        database: OarDatabase,
        cryptoManager: CryptoManager
    ): BackupService = BackupService(
        context = context,
        database = database,
        cryptoManager = cryptoManager
    )

    @Provides
    fun provideBackupRepository(
        @ApplicationContext context: Context,
        backupService: BackupService,
        gDriveApi: GDriveApi,
        preferencesManager: PreferencesManager,
        securityPreferencesManager: SecurityPreferencesManager,
        configDao: ConfigDao,
        backupWorkManager: BackupWorkManager,
        schedulesRepository: SchedulesRepository,
        authRepository: AuthRepository,
        cryptoManager: CryptoManager,
        cycleRepository: BudgetCycleRepository
    ): BackupRepository = BackupRepositoryImpl(
        context = context,
        backupService = backupService,
        gDriveApi = gDriveApi,
        preferencesManager = preferencesManager,
        securityPreferencesManager = securityPreferencesManager,
        configDao = configDao,
        backupWorkManager = backupWorkManager,
        schedulesRepository = schedulesRepository,
        authRepo = authRepository,
        cryptoManager = cryptoManager,
        cycleRepo = cycleRepository
    )

    @Provides
    fun provideBackupWorkManager(
        @ApplicationContext context: Context
    ): BackupWorkManager = BackupWorkManager(context)

    @BackupFeature
    @Provides
    fun provideBackupNotificationHelper(
        @ApplicationContext context: Context
    ): NotificationHelper<String> = BackupNotificationHelper(context)

    @Provides
    fun provideAppLockServiceManager(
        @ApplicationContext context: Context
    ): AppLockServiceManager = AppLockServiceManager(context)

    @Provides
    fun provideAppInitRepository(
        currencyListDao: CurrencyListDao
    ): AppInitRepository = AppInitRepositoryImpl(currencyListDao)

    @AppInitFeature
    @Provides
    fun provideAppInitNotificationHelper(
        @ApplicationContext context: Context
    ): NotificationHelper<Unit> = AppInitNotificationHelper(context)
}

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class GoogleApis

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class BackupFeature

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class AppInitFeature