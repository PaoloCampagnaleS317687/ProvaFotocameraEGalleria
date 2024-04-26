package com.example.provafotocameraegalleria.ViewModel

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.lifecycle.ViewModel

class PhotoViewModel : ViewModel() {

    var fullName = mutableStateOf("Mario Rossi")
        private set

    var photoURI by mutableStateOf<Uri?>(null)
        private set

    var photoBitmap by mutableStateOf<Bitmap?>(null)
        private set

    fun pickProfilePictureFromGallery (uri: Uri?) {
        if ( uri != null ) {
            photoURI = uri
            photoBitmap = null
        }
    }

    fun pickProfilePictureFromCamera (bitmap: Bitmap?) {
        if ( bitmap != null ) {
            photoBitmap = bitmap
            photoURI = null
        }
    }

    fun deleteProfilePicture () {
        photoURI = null
        photoBitmap = null
    }

}