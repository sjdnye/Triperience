package com.example.triperience.features.profile.domain.repository

import android.net.Uri
import com.example.triperience.features.profile.domain.model.Post
import com.example.triperience.utils.Resource
import kotlinx.coroutines.flow.Flow

interface UploadPostRepository {

    suspend fun uploadPost(post: Post, uri: Uri) : Flow<Resource<Boolean>>
}