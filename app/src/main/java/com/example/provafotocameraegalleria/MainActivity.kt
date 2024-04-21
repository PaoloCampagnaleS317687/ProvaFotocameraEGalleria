package com.example.provafotocameraegalleria

import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log             // Useful only for debug
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.example.provafotocameraegalleria.View.MainScreen
import com.example.provafotocameraegalleria.ViewModel.PhotoViewModel
import com.example.provafotocameraegalleria.ui.theme.ProvaFotocameraEGalleriaTheme

class MainActivity : ComponentActivity() {

    private lateinit var photoViewModel: PhotoViewModel

    // Gallery

    private val galleryLauncher = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        photoViewModel.managePickProfilePictureFromGallery(uri)
    }

    // Camera

    private lateinit var permissionsList : Array<String>

    private val requestPermissionsLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions())
        { permissions ->

            // Handle Permission granted/rejected
            var permissionGranted = true
            permissions.entries.forEach {
                if (it.key in permissionsList && !it.value)
                    permissionGranted = false
            }
            if (!permissionGranted) {

                AlertDialog.Builder(this@MainActivity)
                    .setMessage("Please grant the required permissions")
                    .setCancelable(false)
                    .setPositiveButton("Open settings") { dialog, _ ->
                        dialog.dismiss()
                        val openSettingIntent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                        openSettingIntent.data = Uri.parse("package:${this.packageName}")
                        openSettingsLauncher.launch(openSettingIntent)
                    }
                    .show()

            }
        }

    private val openSettingsLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { _ ->
        if (!allPermissionsGranted()) {
            requestPermissions()
        }
    }


    private fun allPermissionsGranted() = permissionsList.all {
        ContextCompat.checkSelfPermission(
            baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermissions() {
        requestPermissionsLauncher.launch(permissionsList/*.toTypedArray()*/)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        permissionsList = baseContext.packageManager.getPackageInfo(
            baseContext.packageName,
            PackageManager.GET_PERMISSIONS
        ).requestedPermissions

        photoViewModel = ViewModelProvider(this).get(PhotoViewModel::class.java)

        // Request camera permissions
        if (!allPermissionsGranted()) {
            requestPermissions()
        }

        setContent {
            ProvaFotocameraEGalleriaTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainScreen(photoViewModel, galleryLauncher)
                }
            }
        }
    }
}
