package com.example.newsapp

import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.newsapp.core.Notification.LocalNotificationService
import com.example.newsapp.core.Notification.NotificationScreen
import com.example.newsapp.presentation.animation.AnimationPage
import com.example.newsapp.presentation.kotlinBasics.Test
import com.example.newsapp.ui.theme.NewsAppTheme
import org.koin.android.ext.android.inject

class MainActivity : ComponentActivity() {
    private val notificationService: LocalNotificationService by inject()

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            notificationService.showNotification(id = 1, count = 0)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NewsAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
//                    AppNav()
//                    CounterScreen()
//                    val test = Test()
//                    AnimationPage()
                    NotificationScreen(
                        onShowNotification = {
                            notificationService.showNotification(id = 1, count = 0)
                        }
                    )
                }
            }
        }
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS)
                == PackageManager.PERMISSION_GRANTED
            ) {
                notificationService.showNotification(id = 1, count = 0)
            } else {
                permissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
            }
        } else {
            // Below Android 13, no runtime permission needed
            notificationService.showNotification(id = 1, count = 0)
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    NewsAppTheme {
        Greeting("Android")
    }
}