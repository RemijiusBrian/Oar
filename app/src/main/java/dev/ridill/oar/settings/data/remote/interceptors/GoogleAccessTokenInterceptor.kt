package dev.ridill.oar.settings.data.remote.interceptors

import dev.ridill.oar.account.domain.service.AccessTokenService
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response

class GoogleAccessTokenInterceptor(
    private val tokenService: AccessTokenService
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response = runBlocking {
        val token = tokenService.getAccessToken().orEmpty()
        val original = chain.request()
        val updatedRequest = original.newBuilder()
            .addHeader("Authorization", "Bearer $token")
            .build()

        chain.proceed(updatedRequest)
    }
}