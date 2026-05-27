package com.example.newsapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.newsapp.data.notification.NotificationPermissionHelper
import com.example.newsapp.presentation.navigation.AppNav
import com.example.newsapp.presentation.viewmodel.NotificationViewModel
import com.example.newsapp.ui.theme.NewsAppTheme
import org.koin.androidx.viewmodel.ext.android.viewModel
class MainActivity : ComponentActivity() {
    private lateinit var permissionHelper: NotificationPermissionHelper
    private val notificationViewModel: NotificationViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        permissionHelper = NotificationPermissionHelper(this) { granted ->
            notificationViewModel.onPermissionResult(granted)
        }
        permissionHelper.requestIfNeeded()

        notificationViewModel.handleNotificationIntent(intent)
        enableEdgeToEdge()
        setContent {
            NewsAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    AppNav()
                }
            }
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