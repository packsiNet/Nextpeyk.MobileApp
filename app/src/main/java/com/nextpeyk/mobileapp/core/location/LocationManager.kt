package com.nextpeyk.mobileapp.core.location

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.os.Looper
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.Priority
import com.nextpeyk.mobileapp.core.utils.hasPermission
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

@Singleton
class LocationManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val fusedLocationClient: FusedLocationProviderClient,
) {

    @SuppressLint("MissingPermission")
    fun locationUpdates(intervalMs: Long = 5_000L): Flow<Location> = callbackFlow {
        if (!context.hasPermission(Manifest.permission.ACCESS_FINE_LOCATION)) {
            close()
            return@callbackFlow
        }

        val request = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, intervalMs).build()

        val callback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                result.lastLocation?.let { trySend(it) }
            }
        }

        fusedLocationClient.requestLocationUpdates(request, callback, Looper.getMainLooper())

        awaitClose { fusedLocationClient.removeLocationUpdates(callback) }
    }

    @SuppressLint("MissingPermission")
    suspend fun getLastKnownLocation(): Location? {
        if (!context.hasPermission(Manifest.permission.ACCESS_FINE_LOCATION)) return null
        return suspendCancellableCoroutine { cont ->
            fusedLocationClient.lastLocation
                .addOnSuccessListener { cont.resume(it) }
                .addOnFailureListener { cont.resumeWithException(it) }
        }
    }
}
