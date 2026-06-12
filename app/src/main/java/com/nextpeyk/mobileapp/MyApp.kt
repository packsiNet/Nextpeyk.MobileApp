package com.nextpeyk.mobileapp

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import org.osmdroid.config.Configuration
import timber.log.Timber
import java.io.File

@HiltAndroidApp
class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
        Configuration.getInstance().userAgentValue = packageName

        val crashFile = File(filesDir, "last_crash.txt")
        Thread.setDefaultUncaughtExceptionHandler { _, throwable ->
            crashFile.writeText(throwable.stackTraceToString())
            android.os.Process.killProcess(android.os.Process.myPid())
        }
    }
}
