package com.example.triperience.features.authentication.domain.repository

import android.net.Uri
import com.example.triperience.features.authentication.domain.model.User
import com.example.triperience.utils.Resource
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

interface AuthRepository {

    fun isUserAuthenticatedInFirebase(): Boolean

    suspend fun createUserWithEmailAndPassword(email:String, password:String, username: String): Flow<Resource<Boolean>>

    suspend fun signInWithEmailAndPassword(email: String, password: String): Flow<Resource<Boolean>>

    suspend fun checkUsernameAvailability(username: String): Flow<Resource<Boolean>>

    fun signOut() : Flow<Resource<Boolean>>

    suspend fun getUser() : Flow<Resource<User?>>


}