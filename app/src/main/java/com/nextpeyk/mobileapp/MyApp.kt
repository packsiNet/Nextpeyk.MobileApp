package ir.nextpeyk.android

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
import java.io.File

@HiltAndroidApp
class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())

        val crashFile = File(filesDir, "last_crash.txt")
        Thread.setDefaultUncaughtExceptionHandler { _, throwable ->
            crashFile.writeText(throwable.stackTraceToString())
            android.os.Process.killProcess(android.os.Process.myPid())
        }
    }
}
