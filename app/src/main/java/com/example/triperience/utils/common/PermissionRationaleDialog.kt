package com.example.triperience.utils.common

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties


@Composable
fun PermissionRationaleDialog(
    message: String,
    imageVector: ImageVector,
    onRequestPermission: () -> Unit,
    onDismissRequest: () -> Unit,
) {
    Dialog(
        properties = DialogProperties(
            dismissOnClickOutside = false,
        ),
        onDismissRequest = { onDismissRequest() }
    ) {
        RationalDialogUI(message, imageVector, onRequestPermission)
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun RationalDialogUI(
    message: String,
    imageVector: ImageVector,
    onRequestPermission: () -> Unit,
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    Card(
        shape = RoundedCornerShape(10.dp),
        elevation = 10.dp,
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Image(
                imageVector = imageVector,
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .height(100.dp)
                    .padding(top = 20.dp)
                    .fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = message,
                style = MaterialTheme.typography.body1,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 20.dp, end = 20.dp, bottom = 20.dp),
                onClick = {
                    keyboardController?.hide()
                    onRequestPermission()
                }
            ) {
                Text(
                    text = "Request permission",
                    style = MaterialTheme.typography.button,
                    color = MaterialTheme.colors.onBackground,
                    modifier = Modifier.padding(vertical = 7.dp)
                )
            }
        }
    }
}

data class ShowRationale(
    val showDialog: Boolean = false,
    val message: String = "",
    val imageVector: ImageVector? = null,
    val permission: String = "",
)