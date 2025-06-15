package dev.ridill.oar.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.ridill.oar.aggregations.data.local.AggregationsDao
import dev.ridill.oar.aggregations.data.repository.AggregationsRepositoryImpl
import dev.ridill.oar.aggregations.domain.repository.AggregationsRepository
import dev.ridill.oar.core.data.db.OarDatabase

@Module
@InstallIn(SingletonComponent::class)
object AggregationsModule {

    @Provides
    fun provideAggregationsDao(database: OarDatabase): AggregationsDao = database.aggregationDao()

    @Provides
    fun provideAggregationsRepository(
        aggregationsDao: AggregationsDao
    ): AggregationsRepository = AggregationsRepositoryImpl(
        dao = aggregationsDao
    )
}