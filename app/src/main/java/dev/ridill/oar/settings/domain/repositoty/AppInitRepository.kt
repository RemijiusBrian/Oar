package dev.ridill.oar.settings.domain.repositoty

interface AppInitRepository {
    suspend fun needsInit(): Boolean
    suspend fun initCurrenciesList()
}