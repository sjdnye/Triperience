package com.example.triperience.features.search.domain.repository

import com.example.triperience.features.authentication.domain.model.User
import com.example.triperience.features.profile.domain.model.Post
import com.example.triperience.utils.Resource
import kotlinx.coroutines.flow.Flow

interface SearchRepository {

    suspend fun searchUsers(query: String) :Flow<Resource<List<User>?>>

    suspend fun searchPostsByCategory(category: String) : Flow<Resource<List<Post>?>>
}