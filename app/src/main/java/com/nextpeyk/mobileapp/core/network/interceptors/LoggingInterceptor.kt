package ir.nextpeyk.android.core.network.interceptors

import okhttp3.logging.HttpLoggingInterceptor

object LoggingInterceptor {
    fun create(): HttpLoggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }
}
