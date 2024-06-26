package com.example.provafotocameraegalleria

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.provafotocameraegalleria.View.MainScreen
import com.example.provafotocameraegalleria.ViewModel.PhotoViewModel
import com.example.provafotocameraegalleria.ui.theme.ProvaFotocameraEGalleriaTheme

class MainActivity : ComponentActivity() {

    private lateinit var photoViewModel: PhotoViewModel

    private val galleryLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        photoViewModel.managePickProfilePictureFromGallery(uri)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        photoViewModel = ViewModelProvider(this).get(PhotoViewModel::class.java)
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
