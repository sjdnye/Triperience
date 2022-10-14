package com.example.triperience.features.home.domain.repository

import com.example.triperience.features.authentication.domain.model.User
import com.example.triperience.features.profile.domain.model.Post
import com.example.triperience.utils.Resource
import kotlinx.coroutines.flow.Flow

interface HomeRepository {

    suspend fun getRelevantPost(followingList : List<String>) : Flow<Resource<List<Post>?>>

    suspend fun getPostPublisherDetail(userId: String) : User?
}