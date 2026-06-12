package com.nextpeyk.mobileapp

import android.app.AlertDialog
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.nextpeyk.mobileapp.navigation.AppNavGraph
import com.nextpeyk.mobileapp.ui.theme.NextpeykTheme
import dagger.hilt.android.AndroidEntryPoint
import java.io.File

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val crashFile = File(filesDir, "last_crash.txt")
        if (crashFile.exists()) {
            val trace = crashFile.readText()
            crashFile.delete()
            AlertDialog.Builder(this)
                .setTitle("Crash Log")
                .setMessage(trace.take(3000))
                .setPositiveButton("OK") { _, _ -> }
                .show()
            return
        }

        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(
                android.graphics.Color.TRANSPARENT,
                android.graphics.Color.TRANSPARENT,
            ),
            navigationBarStyle = SystemBarStyle.light(
                android.graphics.Color.TRANSPARENT,
                android.graphics.Color.TRANSPARENT,
            ),
        )
        setContent {
            NextpeykTheme {
                AppNavGraph()
            }
        }
    }
}
