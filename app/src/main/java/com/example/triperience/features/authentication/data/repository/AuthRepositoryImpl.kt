package com.example.triperience.features.authentication.data.repository

import com.example.triperience.features.authentication.domain.model.User
import com.example.triperience.features.authentication.domain.repository.AuthRepository
import com.example.triperience.utils.Constants
import com.example.triperience.utils.Resource
import com.example.triperience.utils.await
import com.example.triperience.utils.shared_preferences.SharedPrefUtil
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val firebaseStorage: FirebaseStorage,
    private val sharedPrefUtil: SharedPrefUtil
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

            sharedPrefUtil.saveCurrentUser(obj)

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
            getUser().collect{
                when(it){
                    is Resource.Success -> {
                        sharedPrefUtil.saveCurrentUser(it.data)
                        emit(Resource.Success(true))
                    }
                    is Resource.Error -> {
                        emit(Resource.Error(message = it.message.toString()))
                    }
                    is Resource.Loading -> {}
                }
            }

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
                auth.signInAnonymously().await()
                val querySnapshot = firestore.collection(Constants.COLLECTION_USERS)
                    .whereEqualTo("username", username)
                    .get()
                    .await()

                emit(
                    Resource.Success(
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
}