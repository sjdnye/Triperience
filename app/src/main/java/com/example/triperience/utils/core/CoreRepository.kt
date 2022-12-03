package com.example.triperience.utils.core

import com.example.triperience.features.authentication.domain.model.User

interface CoreRepository {
    suspend fun getPostPublisherDetail(userId: String) : User?

}