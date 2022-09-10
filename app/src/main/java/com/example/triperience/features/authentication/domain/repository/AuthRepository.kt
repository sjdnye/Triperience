package com.example.triperience.domain.repository

import android.net.Uri
import com.example.triperience.domain.model.User
import com.example.triperience.utils.Resource
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.Flow

interface AuthRepository {

    suspend fun createUserWithEmailAndPassword(email:String, password:String): Flow<Resource<Boolean>>

    suspend fun signInWithEmailAndPassword(email: String, password: String): Flow<Resource<Boolean>>

    suspend fun checkUsernameAvailability(username: String): Flow<Resource<Boolean>>

    fun signOut() : FirebaseUser?

    suspend fun getUser() : Flow<Resource<User?>>

    suspend fun saveUserProfile(user: User): Flow<Resource<Boolean>>

    suspend fun uploadProfileImage(imageUri: Uri): Flow<Resource<String>>


}