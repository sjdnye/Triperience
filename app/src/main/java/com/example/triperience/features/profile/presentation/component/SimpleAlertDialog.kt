package com.example.triperience.features.profile.presentation.component

import androidx.compose.material.AlertDialog
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun SimpleAlertDialog(
    modifier: Modifier,
    onDismissRequest: () -> Unit,
    confirmButton: () -> Unit,
    dismissButton: () -> Unit
) {
    AlertDialog(
        modifier = modifier,
        onDismissRequest = {
            onDismissRequest()
        },
        confirmButton = {
            TextButton(
                onClick = {
                    confirmButton()
                }
            ) { Text(text = "Yes") }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    dismissButton()
                }
            ) { Text(text = "No") }
        },
        title = { Text(text = "Log Out") },
        text = { Text(text = "Are you sure ?") }
    )
}