package com.nextpeyk.mobileapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.nextpeyk.mobileapp.navigation.AppNavGraph
import com.nextpeyk.mobileapp.ui.theme.NextpeykTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NextpeykTheme {
                AppNavGraph()
            }
        }
    }
}
