package com.example.triperience.features.profile.data.repository

import android.net.Uri
import com.example.triperience.features.authentication.domain.model.User
import com.example.triperience.features.profile.domain.repository.ProfileRepository
import com.example.triperience.utils.Constants
import com.example.triperience.utils.Resource
import com.example.triperience.utils.await
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class ProfileRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val firebaseStorage: FirebaseStorage,
    private val auth: FirebaseAuth
) : ProfileRepository {

    override fun getUserDetails(userid: String): Flow<Resource<User>> = callbackFlow {
        trySend(Resource.Loading())
        val snapShotListener = firestore.collection(Constants.COLLECTION_USERS)
            .document(userid)
            .addSnapshotListener { snapshot, error ->
                val response = if (snapshot != null) {
                    val userInfo = snapshot.toObject(User::class.java)
                    Resource.Success<User>(userInfo!!)
                } else {
                    Resource.Error(error?.message ?: error.toString())
                }
                trySend(response).isSuccess
            }
        awaitClose {
            snapShotListener.remove()
        }
    }

    override suspend fun setUserDetails(
        userid: String,
        userName: String,
        bio: String
    ): Flow<Resource<Boolean>> = flow {
        emit(Resource.Loading())
        try {
            val userObj = mutableMapOf<String, String>()
            userObj["username"] = userName
            userObj["bio"] = bio
            firestore.collection(Constants.COLLECTION_USERS)
                .document(userid)
                .update(userObj as Map<String, Any>)
                .await()
            emit(Resource.Success(true))
        } catch (e: IOException) {
            emit(Resource.Error(message = e.localizedMessage ?: "Something went wrong!"))
        } catch (e: HttpException) {
            emit(Resource.Error(message = e.localizedMessage ?: "Something went wrong!"))
        } catch (e: Exception) {
            emit(Resource.Error(message = e.localizedMessage ?: "Something went wrong!"))
        }
    }

    override suspend fun uploadProfileImage(imageUri: Uri): Flow<Resource<Boolean>> = flow{
        emit(Resource.Loading())
        try {
            val uploadTask = firebaseStorage.reference
                .child("${Constants.PROFILE_IMAGE_STORAGE_REF}/image_${System.currentTimeMillis()}")
                .putFile(Uri.parse(imageUri.toString())).await()

            val downloadUrl = uploadTask.storage.downloadUrl.await().toString()
            val userObj = mutableMapOf<String,String>()
            userObj["profileImage"] = downloadUrl

            firestore.collection(Constants.COLLECTION_USERS)
                .document(auth.currentUser?.uid!!)
                .update(userObj as Map<String, Any>)
                .await()
            emit(Resource.Success(true))

        } catch (e: IOException) {
            emit(Resource.Error(message = e.localizedMessage ?: "Something went wrong!"))
        } catch (e: HttpException) {
            emit(Resource.Error(message = e.localizedMessage ?: "Something went wrong!"))
        } catch (e: Exception) {
            emit(Resource.Error(message = e.localizedMessage ?: "Something went wrong!"))
        }
    }
}