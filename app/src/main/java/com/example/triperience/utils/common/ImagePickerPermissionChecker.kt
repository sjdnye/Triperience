package com.example.triperience.utils.common

import android.Manifest
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.filled.Image
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.FileProvider
import com.example.triperience.R
import com.example.triperience.utils.Constants
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.io.File


@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterialApi::class)
@Composable
fun ImagePickerPermissionChecker(
    coroutineScope: CoroutineScope,
    bottomSheetState: ModalBottomSheetState,
    onGalleryLaunchResult: (Uri?) -> Unit,
    onCameraLaunchResult: (Uri?) -> Unit,
) {
    val storagePermission =
        rememberPermissionState(permission = Manifest.permission.READ_EXTERNAL_STORAGE)
    val cameraPermission = rememberPermissionState(permission = Manifest.permission.CAMERA)
    val showPermissionRationale = remember { mutableStateOf(ShowRationale()) }
    val context = LocalContext.current
    var imageUri: Uri? = null

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri ->
            run {
                onGalleryLaunchResult(uri)
            }
        }
    )

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview(),
        onResult = { bitmapImage ->
            run {
               val result =  getImageUri(inContext = context, inImage = bitmapImage!!)
                onCameraLaunchResult(result)
//                if (success) {
//                    onCameraLaunchResult(imageUri)
//                }

            }
        }
    )

    ImagePickerBottomSheetDialog(bottomSheetState = bottomSheetState) { option ->
        when (option) {
            Constants.GALLERY -> {
                coroutineScope.launch { bottomSheetState.hide() }

                if (storagePermission.status.isGranted) {
                    galleryLauncher.launch("image/*")
                } else if (storagePermission.status.shouldShowRationale) {
                    showPermissionRationale.value = showPermissionRationale.value.copy(
                        showDialog = true,
                        message = "Triperience Requires this Storage permission to access images in your phones Gallery.",
                        imageVector = Icons.Filled.Image,
                        permission = Constants.GALLERY
                    )
                } else {
                    storagePermission.launchPermissionRequest()
                }
            }
            Constants.CAMERA -> {
                coroutineScope.launch { bottomSheetState.hide() }

                if (cameraPermission.status.isGranted) {
//                    val uri = ComposeFileProvider.getImageUri(context)
//                    imageUri = uri
//                    cameraLauncher.launch(uri)
                    cameraLauncher.launch()
                } else if (cameraPermission.status.shouldShowRationale) {
                    showPermissionRationale.value = showPermissionRationale.value.copy(
                        showDialog = true,
                        message = "Triperiece Requires this Camera permission to access your phones Camera.",
                        imageVector = Icons.Filled.Camera,
                        permission = Constants.CAMERA
                    )
                } else {
                    cameraPermission.launchPermissionRequest()
                }
            }
        }
    }

    if (showPermissionRationale.value.showDialog) {
        PermissionRationaleDialog(
            message = showPermissionRationale.value.message,
            imageVector = showPermissionRationale.value.imageVector!!,
            onRequestPermission = {
                showPermissionRationale.value =
                    showPermissionRationale.value.copy(showDialog = false)
                when (showPermissionRationale.value.permission) {
                    Constants.GALLERY -> storagePermission.launchPermissionRequest()
                    Constants.CAMERA -> cameraPermission.launchPermissionRequest()
                }
            },
            onDismissRequest = {
                showPermissionRationale.value =
                    showPermissionRationale.value.copy(showDialog = false)
            }
        )
    }
}

fun getImageUri(inContext: Context, inImage: Bitmap) : Uri{
   val bytes : ByteArrayOutputStream = ByteArrayOutputStream()
    inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
    val path : String = MediaStore.Images.Media.insertImage(inContext.contentResolver, inImage, "Title", null)
    return Uri.parse(path)
}

class ComposeFileProvider : FileProvider(
    R.xml.file_paths
) {
    companion object {
        fun getImageUri(context: Context): Uri {
            val directory = File(context.cacheDir, "images")
            directory.mkdirs()
            val file = File.createTempFile(
                "selected_image_",
                ".jpg",
                directory,
            )
            val authority = context.packageName + ".fileProvider"
            return getUriForFile(
                context,
                authority,
                file,
            )
        }
    }
}