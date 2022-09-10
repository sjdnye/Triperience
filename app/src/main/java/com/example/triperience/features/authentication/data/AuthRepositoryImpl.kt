package com.example.triperience.features.authentication.data

import android.net.Uri
import com.example.triperience.features.authentication.domain.model.User
import com.example.triperience.features.authentication.domain.repository.AuthRepository
import com.example.triperience.utils.Constants
import com.example.triperience.utils.Resource
import com.example.triperience.utils.await
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val firebaseStorage: FirebaseStorage
) : AuthRepository {

    override fun isUserAuthenticatedInFirebase(): Boolean {
        return auth.currentUser != null
    }

    override suspend fun createUserWithEmailAndPassword(
        email: String,
        password: String,
        username: String
    ): Flow<Resource<Boolean>> = flow {
        emit(Resource.Loading())
        try {
            auth.createUserWithEmailAndPassword(email, password).await()
            val userid = auth.currentUser?.uid!!
            val obj = User(
                username = username,
                userid = userid,
                profileImage = "https://firebasestorage.googleapis.com/v0/b/triperience-274c4.appspot.com/o/Default%2Fdefault%20image%20profile.png?alt=media&token=63f3c773-28fc-4f79-8e6d-3ce0a93776e9",
                email = email
            )
            firestore.collection(Constants.COLLECTION_USERS).document(userid)
                .set(obj)
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


    override suspend fun signInWithEmailAndPassword(
        email: String,
        password: String
    ): Flow<Resource<Boolean>> = flow {
        emit(Resource.Loading())
        try {
            auth.signInWithEmailAndPassword(email, password).await()
            emit(Resource.Success(true))

        } catch (e: IOException) {
            emit(Resource.Error(message = e.localizedMessage ?: "Something went wrong!"))
        } catch (e: HttpException) {
            emit(Resource.Error(message = e.localizedMessage ?: "Something went wrong!"))
        } catch (e: Exception) {
            emit(Resource.Error(message = e.localizedMessage ?: "Something went wrong!"))
        }
    }

    override suspend fun checkUsernameAvailability(username: String): Flow<Resource<Boolean>> =
        flow {
            try {
                val querySnapshot = firestore.collection(Constants.COLLECTION_USERS)
                    .whereEqualTo("username", username)
                    .get()
                    .await()

                emit(Resource.Success(
                    data = querySnapshot.documents
                        .map { it.getString("username") }
                        .none { it.equals(username, true) }
                )
                )
            } catch (e: IOException) {
                emit(Resource.Error(message = e.localizedMessage ?: "Something went wrong!"))
            } catch (e: HttpException) {
                emit(Resource.Error(message = e.localizedMessage ?: "Something went wrong!"))
            } catch (e: Exception) {
                emit(Resource.Error(message = e.localizedMessage ?: "Something went wrong!"))
            }
        }

    override fun signOut(): Flow<Resource<Boolean>> = flow {
        emit(Resource.Loading())
        try {
            auth.signOut()
            emit(Resource.Success(true))

        } catch (e: IOException) {
            emit(Resource.Error(message = e.localizedMessage ?: "Something went wrong!"))
        } catch (e: HttpException) {
            emit(Resource.Error(message = e.localizedMessage ?: "Something went wrong!"))
        } catch (e: Exception) {
            emit(Resource.Error(message = e.localizedMessage ?: "Something went wrong!"))
        }
    }

    override suspend fun getUser(): Flow<Resource<User?>> = flow {
        emit(Resource.Loading())
        try {
            val firebaseUser = auth.currentUser
            firebaseUser?.let { user ->
                val snapshot = firestore.collection(Constants.COLLECTION_USERS)
                    .document(user.uid)
                    .get()
                    .await()

                emit(Resource.Success(data = snapshot.toObject<User>()))

            }
        } catch (e: IOException) {
            emit(Resource.Error(message = e.localizedMessage ?: "Something went wrong!"))
        } catch (e: HttpException) {
            emit(Resource.Error(message = e.localizedMessage ?: "Something went wrong!"))
        } catch (e: Exception) {
            emit(Resource.Error(message = e.localizedMessage ?: "Something went wrong!"))
        }

    }

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

    override fun setUserDetails(
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


    override suspend fun saveUserProfile(user: User): Flow<Resource<Boolean>> {
        TODO("Not yet implemented")
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