package com.example.triperience.features.profile.domain.model

import androidx.compose.ui.graphics.painter.Painter

data class CategoryItem(
    val painter: Painter,
    val category: PostCategory
)
