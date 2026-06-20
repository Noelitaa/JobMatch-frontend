package com.moviles.jobmatch

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.google.firebase.messaging.FirebaseMessaging
import com.moviles.jobmatch.data.AuthSession
import com.moviles.jobmatch.data.NotificationHandler
import com.moviles.jobmatch.data.repository.AppContainer
import com.moviles.jobmatch.navigation.AppNavHost
import com.moviles.jobmatch.ui.theme.JobMatchTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private val requestNotificationPermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        Log.d("MainActivity", "Notification permission granted: $granted")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        requestNotificationPermissionIfNeeded()
        handleNotificationIntent()

        setContent {
            JobMatchTheme {
                AppNavHost(modifier = Modifier.fillMaxSize())
            }
        }
    }

    override fun onNewIntent(intent: android.content.Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleNotificationIntent()
    }

    private fun requestNotificationPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val granted = ContextCompat.checkSelfPermission(
                this, Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
            if (!granted) {
                requestNotificationPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    private fun handleNotificationIntent() {
        val type = intent?.getStringExtra("type") ?: return
        val entityId = intent?.getStringExtra("entityId")
        NotificationHandler.pendingRoute = NotificationHandler.resolveRoute(type, entityId)
        Log.d("MainActivity", "Notification tap — type: $type → route: ${NotificationHandler.pendingRoute}")
    }

    fun registerFcmToken() {
        FirebaseMessaging.getInstance().token.addOnSuccessListener { token ->
            CoroutineScope(Dispatchers.IO).launch {
                AppContainer.fcmRepository.registerToken(
                    token = token,
                    deviceInfo = "Android ${Build.VERSION.RELEASE}"
                )
                Log.d("MainActivity", "FCM token registered after login")
            }
        }
    }
}
