package com.moviles.jobmatch

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import com.moviles.jobmatch.navigation.AppNavHost
import com.moviles.jobmatch.ui.theme.JobMatchTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            JobMatchTheme {
                AppNavHost(modifier = Modifier.fillMaxSize())
            }
        }
    }
}