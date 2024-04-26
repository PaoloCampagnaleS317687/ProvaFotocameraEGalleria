package com.example.provafotocameraegalleria.View

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.provafotocameraegalleria.R
import com.example.provafotocameraegalleria.ViewModel.PhotoViewModel
import java.io.File

@Preview
@Composable
fun MainScreen (vm : PhotoViewModel = viewModel()) {
    BaseComponent(
        vm.fullName.value,
        vm.photoURI,
        vm.photoBitmap,
        vm::pickProfilePictureFromGallery,
        vm::pickProfilePictureFromCamera,
        vm::deleteProfilePicture
    )
}

@Composable
fun BaseComponent (
    fullName: String,
    photoURI: Uri?,
    photoBitmap: Bitmap?,
    pickProfilePictureFromGallery: (Uri?) -> Unit,
    pickProfilePictureFromCamera: (Bitmap?) -> Unit,
    deleteProfilePicture: () -> Unit,
) {
    val circleSize = 200

    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri ->
            pickProfilePictureFromGallery(uri)
        }
    )

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            pickProfilePictureFromGallery(uri)
        }
    )

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview(),
        onResult = { bitmap ->
            pickProfilePictureFromCamera(bitmap)
        }
    )

    Column (
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        if (photoURI != null) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(photoURI)
                    .crossfade(true)
                    .build(),
                placeholder = painterResource(R.drawable.image_not_found),
                contentDescription = "Your profile picture",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .clip(CircleShape)
                    .size(circleSize.dp)
            )
        }
        else if (photoBitmap != null) {
            Image(
                bitmap = photoBitmap.asImageBitmap(),
                contentDescription = "Your profile picture",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .clip(CircleShape)
                    .size((0.8f * circleSize).dp)
            )
        }
        else {
            Monogram(fullName, circleSize)
        }
        Text(fullName, style = MaterialTheme.typography.headlineLarge)
        Button(onClick = {
            cameraLauncher.launch(null)
        }) {
            Text("Camera")
        }
        Button(
            onClick = { galleryLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) }
        ) {
            Text("Gallery BELLA")
        }
        Button(
            onClick = { imagePicker.launch("image/*") }
        ) {
            Text("Gallery")
        }
        Button(onClick = { deleteProfilePicture() }) {
            Text("Delete")
        }
    }
}

@Composable
fun Monogram (fullName: String, monogramSize: Int) {
    val parts = fullName.split(" ")
    val initials = StringBuilder()

    for (part in parts) {
        val initial = part.firstOrNull() ?: '?'
        initials.append(initial.uppercaseChar())
    }

    // Una persona può avere 2 nomi, un nome e due cognomi, un cognome di due parole...
    // Limitiamo il monogram a massimo 2 caratteri
    val monogram = initials.substring(0, 2).toString()

    val style = TextStyle(
        fontSize = (0.4f * monogramSize).sp,
        color = Color.Black,
    )

    val textMeasurer = rememberTextMeasurer()

    val textLayoutResult = textMeasurer.measure(monogram, style)

    Canvas(
        modifier = Modifier.size(monogramSize.dp),
        onDraw = {
            drawCircle(
                color = Color.Magenta,
                radius = 0.4f * size.minDimension,      // Se fosse esattamente 0.5f, Monogram e immagini avrebbero la stessa dimensione
                center = center
            )

            drawText(
                textMeasurer = textMeasurer,
                text = monogram,
                style = style,
                topLeft = Offset(
                    x = center.x - textLayoutResult.size.width / 2,
                    y = center.y - textLayoutResult.size.height / 2,
                )
            )
        }
    )
}
