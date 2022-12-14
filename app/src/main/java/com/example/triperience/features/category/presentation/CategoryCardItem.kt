package com.example.triperience.features.category.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.triperience.ui.theme.customFont

@Composable
fun CategoryCardItem(
    modifier: Modifier = Modifier,
    alignment: Alignment = Alignment.CenterEnd,
    painter: Painter,
    gradiant: Brush,
    title: String,
) {
    Card(
        modifier = modifier
            .background(MaterialTheme.colors.background)
            .clip(RoundedCornerShape(20.dp))
            .wrapContentHeight(),
    ) {
        Box(
            modifier = Modifier
                .background(Color.Transparent)
                .clip(RoundedCornerShape(20.dp))
                .background(gradiant),

            ) {
            Text(
                text = title,
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(5.dp),
                color = MaterialTheme.colors.onPrimary,
                fontSize = 20.sp,
                fontFamily = customFont
            )
            Image(
                painter = painter,
                contentDescription = "",
                modifier = Modifier
                    .align(alignment)
                    .size(128.dp)
                    .clip(RoundedCornerShape(20.dp))
            )
        }

    }
}