package com.example.triperience.features.searchedPost.domain.repository

import com.example.triperience.features.profile.domain.model.Post
import com.example.triperience.utils.Resource
import kotlinx.coroutines.flow.Flow

interface SearchedPostsRepository {

    suspend fun searchPostsByCategory(category: String) : Flow<Resource<List<Post>?>>

    suspend fun searchPostsByCity(city: String) : Flow<Resource<List<Post>?>>

    suspend fun searchPostsByCoordinate(latitude: Double, longitude: Double) : Flow<Resource<List<Post>?>>

}