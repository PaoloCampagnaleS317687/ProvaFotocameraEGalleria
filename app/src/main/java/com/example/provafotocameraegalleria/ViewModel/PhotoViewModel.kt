package com.example.provafotocameraegalleria.ViewModel

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.lifecycle.ViewModel

class PhotoViewModel : ViewModel() {

    var screen by mutableStateOf(Screen.BASE)
        private set

    fun openCamera () {
        screen = Screen.PHOTO
    }

    fun returnToBase () {
        screen = Screen.BASE
    }

    var fullName = mutableStateOf("Mario Rossi")
        private set

    var photoBitmap by mutableStateOf<Bitmap?>(null)
        private set

    var photoURI by mutableStateOf<Uri?>(null)
        private set

    fun openGallery(galleryLauncher: ActivityResultLauncher<String>) {
        screen = Screen.GALLERY
        galleryLauncher.launch("image/*")
    }

    fun managePickProfilePictureFromGallery (uri: Uri?) {
        photoURI = uri
        screen = Screen.BASE
    }

    fun deletePhoto () {
        photoBitmap = null
        photoURI = null
    }


}