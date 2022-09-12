package com.example.triperience.features.profile.domain.repository

import android.net.Uri
import com.example.triperience.features.authentication.domain.model.User
import com.example.triperience.utils.Resource
import kotlinx.coroutines.flow.Flow

interface ProfileRepository {

    fun getUserDetails(userid:String): Flow<Resource<User>>

    suspend fun setUserDetails(
        userid: String,
        userName: String,
        bio: String,
    ) : Flow<Resource<Boolean>>

    suspend fun uploadProfileImage(imageUri: Uri): Flow<Resource<Boolean>>

}