package com.example.provafotocameraegalleria.ViewModel

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.view.PreviewView
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import java.util.concurrent.Executor
import java.util.concurrent.Executors

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

    var pictureBitmap by mutableStateOf<Bitmap?>(null)
        private set

    var pictureURI by mutableStateOf<Uri?>(null)
        private set

    // Gallery

    fun openGallery(galleryLauncher: ActivityResultLauncher<PickVisualMediaRequest>) {
        screen = Screen.GALLERY
        galleryLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    fun managePickProfilePictureFromGallery (uri: Uri?) {
        pictureURI = uri
        pictureBitmap = null
        screen = Screen.BASE
    }

    // Camera

    var temporaryPicture by mutableStateOf<Bitmap?>(null)
        private set

    fun defineTemporaryPicture (picture: Bitmap?) {
        temporaryPicture = picture
    }

    /*var previewView by mutableStateOf<PreviewView?>(null)
        private set

    fun definePreviewView (context: Context) {
        previewView = PreviewView(context)
    }*/

    var isCameraFacingBack by mutableStateOf(true)
        private set

    fun switchCamera () {
        isCameraFacingBack = !isCameraFacingBack
    }

    var isFlashAvailable by mutableStateOf(false)
        private set

    fun defineFlashAvailable () {
        isFlashAvailable = true
    }

    fun defineFlashUnavailable () {
        isFlashAvailable = false
    }

    var isFlashOn by mutableStateOf(false)
        private set

    fun switchFlash () {
        isFlashOn = !isFlashOn
    }

    /*var isPreviewing by mutableStateOf(true)
        private set

    fun openPreview () {

    }

    fun closePreview () {

    }*/

    var imageCapture by mutableStateOf<ImageCapture>(
        ImageCapture.Builder()
            .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
            .build()
    )
        private set

    fun buildImageCapture () {

    }

    /*var cameraExecutor by mutableStateOf(Executors.newSingleThreadExecutor())
        private set

    fun defineExecutor () {

    }*/

    fun takePicture (executor: Executor) {      // Se usiamo un executor diverso da quello standard, lo possiamo togliere
        imageCapture.takePicture(
            executor,
            object : ImageCapture.OnImageCapturedCallback() {
                override fun onCaptureSuccess(image: ImageProxy) {
                    val buffer = image.planes[0].buffer
                    val bytes = ByteArray(buffer.remaining())
                    buffer.get(bytes)
                    val bitmap =  BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                    defineTemporaryPicture(bitmap)
                    
                    image.close()
                }

                override fun onError(exception: ImageCaptureException) {
                    // Handle error capturing image
                }
            }
        )
    }

    fun deleteTemporaryPicture () {
        temporaryPicture = null
    }

    fun saveTemporaryPicture () {
        pictureURI = null
        pictureBitmap = temporaryPicture
        deleteTemporaryPicture()
        screen = Screen.BASE
    }

    fun deletePicture () {
        pictureBitmap = null
        pictureURI = null
    }


}