package dev.ridill.oar.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.ridill.oar.core.data.db.OarDatabase
import dev.ridill.oar.settings.data.local.CurrencyListDao
import dev.ridill.oar.settings.data.local.CurrencyPreferenceDao
import dev.ridill.oar.settings.data.repository.CurrencyRepositoryImpl
import dev.ridill.oar.settings.domain.repositoty.CurrencyRepository

@Module
@InstallIn(SingletonComponent::class)
object CurrencyModule {

    @Provides
    fun provideCurrencyDao(database: OarDatabase): CurrencyListDao =
        database.currencyListDao()

    @Provides
    fun provideCurrencyPreferenceDao(database: OarDatabase): CurrencyPreferenceDao =
        database.currencyPreferenceDao()

    @Provides
    fun provideCurrencyPreferenceRepository(
        dao: CurrencyPreferenceDao,
        currencyListDao: CurrencyListDao
    ): CurrencyRepository = CurrencyRepositoryImpl(
        dao = dao,
        currencyListDao = currencyListDao
    )
}