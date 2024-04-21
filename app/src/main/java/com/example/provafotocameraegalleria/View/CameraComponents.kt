package com.example.provafotocameraegalleria.View

import android.graphics.Bitmap
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.Button
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.example.provafotocameraegalleria.R
import java.util.concurrent.Executor
import java.util.concurrent.ExecutorService

@Composable
fun CameraComponent (
    isCameraFacingBack : Boolean,
    switchCamera : () -> Unit,
    isFlashAvailable : Boolean,
    defineFlashAvailable : () -> Unit,
    defineFlashUnavailable : () -> Unit,
    isFlashOn : Boolean,
    switchFlash : () -> Unit,
    imageCapture : ImageCapture,
    //cameraExecutor : ExecutorService,
    temporaryPicture : Bitmap?,
    takePicture : (Executor) -> Unit,
    deleteTemporaryPicture : () -> Unit,
    saveTemporaryPicture : () -> Unit
) {

    val context = LocalContext.current

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        if (temporaryPicture == null) {
            AndroidView(
                factory = { ctx ->
                    PreviewView(ctx).apply {
                        implementationMode = PreviewView.ImplementationMode.COMPATIBLE
                    }
                },
                update = { previewView ->
                    val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
                    cameraProviderFuture.addListener({
                        val cameraProvider = cameraProviderFuture.get()

                        val cameraSelector = if (isCameraFacingBack) {
                            CameraSelector.DEFAULT_BACK_CAMERA
                        } else {
                            CameraSelector.DEFAULT_FRONT_CAMERA
                        }

                        val preview = Preview.Builder().build()
                        preview.setSurfaceProvider(previewView.surfaceProvider)

                        lateinit var camera : Camera
                        try {
                            cameraProvider.unbindAll()

                            camera = cameraProvider.bindToLifecycle(
                                (context as androidx.lifecycle.LifecycleOwner),
                                cameraSelector,
                                preview,
                                imageCapture
                            )
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }

                        if (camera.cameraInfo.hasFlashUnit()) {
                            defineFlashAvailable()
                        }
                        else {
                            defineFlashUnavailable()
                        }

                        camera.cameraControl.enableTorch(isFlashOn)
                    }, ContextCompat.getMainExecutor(context)) //NON VA BENE: BISOGNEREBBE USARE UN THREAD A PARTE
                }
            )
            // Pulsante al centro in basso
            Button(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 16.dp),
                onClick = { takePicture(ContextCompat.getMainExecutor(context)) }
            ) {
                Image(
                    painter = painterResource(id = R.drawable.camera),
                    contentDescription = "Take photo",
                    modifier = Modifier.size(50.dp)
                )

            }

            // Due pulsanti in alto a destra, uno sopra l'altro
            Column(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(end = 16.dp),
                verticalArrangement = Arrangement.Top
            ) {
                if (isFlashAvailable) {
                    Button(
                        onClick = { switchFlash() },
                        modifier = Modifier.padding(bottom = 8.dp)
                    ) {
                        if (isFlashOn) {
                            Image(
                                painter = painterResource(id = R.drawable.flash_on),
                                contentDescription = "Turn flash off",
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        else {
                            Image(
                                painter = painterResource(id = R.drawable.flash_off),
                                contentDescription = "Turn flash on",
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
                Button(
                    onClick = { switchCamera() }
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.flip_camera),
                        contentDescription = "Take photo",
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        } else {
            Column (
                modifier = Modifier.fillMaxSize(), 
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Image(
                    painter = BitmapPainter(temporaryPicture.asImageBitmap()),
                    contentDescription = "The picture you just take"
                )
                Text("Vuoi usare questa immagine?")
                Row (
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Column (
                        modifier = Modifier.fillMaxWidth().weight(1f)
                    ) {
                        IconButton(
                            onClick = { deleteTemporaryPicture() },
                            Modifier.background(Color.Red)
                        ) {
                            Icons.Default.Close
                        }
                    }
                    Column (
                        modifier = Modifier.fillMaxWidth().weight(1f)
                    ) {
                        IconButton(
                            onClick = { saveTemporaryPicture() },
                            Modifier.background(Color.Green)
                        ) {
                            Icons.Default.Done
                        }
                    }
                }
            }
        }
    }

}
