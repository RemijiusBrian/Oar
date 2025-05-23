package dev.ridill.oar.account.domain.service

interface AccessTokenService {
    suspend fun getAccessToken(): String?
    suspend fun updateAccessToken(token: String?)
}