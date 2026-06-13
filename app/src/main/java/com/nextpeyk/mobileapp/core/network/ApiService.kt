package ir.nextpeyk.android.core.network

import retrofit2.http.GET

interface ApiService {
    @GET("ping")
    suspend fun ping(): Map<String, String>
}
