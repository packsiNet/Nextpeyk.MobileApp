package com.nextpeyk.mobileapp.core.network.interceptors

import com.nextpeyk.mobileapp.core.datastore.AppPreferences
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class AuthInterceptor @Inject constructor(
    private val appPreferences: AppPreferences,
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val token = appPreferences.token
        val request = if (token != null) {
            chain.request().newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .build()
        } else {
            chain.request()
        }
        return chain.proceed(request)
    }
}
