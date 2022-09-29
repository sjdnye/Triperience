package com.example.triperience.features.profile.domain.model

sealed interface PostCategory{
    object Sea : PostCategory
    object Jungle : PostCategory
    object Desert : PostCategory
    object Mountainous : PostCategory
    object City : PostCategory
    object Idle : PostCategory
}