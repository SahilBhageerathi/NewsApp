package com.example.newsapp.data.notification

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner

/**
 * Lifecycle-aware notification permission handler.
 *
 * USAGE:
 *   class MainActivity : ComponentActivity() {
 *       private lateinit var permissionHelper: NotificationPermissionHelper
 *
 *       override fun onCreate(savedInstanceState: Bundle?) {
 *           super.onCreate(savedInstanceState)
 *           permissionHelper = NotificationPermissionHelper(this) { granted ->
 *               if (granted) { /* show notifications */ }
 *               else { /* degrade gracefully */ }
 *           }
 *       }
 *
 *       fun onEnableNotificationsClicked() {
 *           permissionHelper.requestIfNeeded()
 *       }
 *   }
 */
class NotificationPermissionHelper(
    private val activity: ComponentActivity,
    private val onResult: (granted: Boolean) -> Unit
) : DefaultLifecycleObserver {

    private var launcher: ActivityResultLauncher<String>? = null

    init {
        launcher = activity.registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { granted ->
            onResult(granted)
        }
        activity.lifecycle.addObserver(this)
    }

    fun requestIfNeeded() {
        when {
            Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU -> {
                onResult(true)
            }
            hasPermission(activity) -> {
                onResult(true)
            }
            activity.shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS) -> {
                // Optionally show your own rationale UI first, then launch
                launcher?.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
            else -> {
                launcher?.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    override fun onDestroy(owner: LifecycleOwner) {
        launcher = null
        activity.lifecycle.removeObserver(this)
    }

    companion object {
        fun hasPermission(context: Context): Boolean {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            } else {
                true
            }
        }
    }
}
