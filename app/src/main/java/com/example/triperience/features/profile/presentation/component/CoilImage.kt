package com.example.triperience.features.profile.presentation.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter
import coil.transform.CircleCropTransformation
import com.example.triperience.R

@Composable
fun CoilImage(
    imageUrl: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .aspectRatio(1f, matchHeightConstraintsFirst = true)
            .clip(CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        val painter = rememberImagePainter(
            data = imageUrl,
            builder = {
                transformations(
                    CircleCropTransformation(),
                )
                placeholder(R.drawable.default_image_profile)
            },
        )
        val painterState = painter.state
        Image(
            modifier = Modifier
                .fillMaxSize()
                .clip(CircleShape)
                .aspectRatio(1f, matchHeightConstraintsFirst = true)
            ,
            painter = painter,
            contentDescription = "profile image",

            )
    }
}