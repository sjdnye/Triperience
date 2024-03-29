package com.example.triperience.features.profile.data.repository

import android.net.Uri
import android.telephony.ims.ImsReasonInfo
import android.util.Log
import com.example.triperience.features.authentication.domain.model.User
import com.example.triperience.features.profile.domain.model.Post
import com.example.triperience.features.profile.domain.repository.ProfileRepository
import com.example.triperience.utils.Constants
import com.example.triperience.utils.Resource
import com.example.triperience.utils.await
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import timber.log.Timber
import java.io.IOException
import javax.inject.Inject

class ProfileRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val firebaseStorage: FirebaseStorage,
    private val auth: FirebaseAuth
) : ProfileRepository {

    override fun getUserInformation(userid: String): Flow<Resource<User>> = callbackFlow {
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

    override suspend fun getUserPosts(userId: String): Flow<Resource<List<Post>?>> = flow {
        try {
            emit(Resource.Loading())
            val query = firestore.collection(Constants.COLLECTION_POST)
                .whereEqualTo(Constants.PUBLISHER_ID, userId)
                .get()
                .await()
            val posts = query.toObjects(Post::class.java)
            emit(Resource.Success(data = posts.sortedByDescending { it.dateTime }))
        } catch (e: IOException) {
            emit(Resource.Error(message = e.localizedMessage ?: "Something went wrong!"))
        } catch (e: HttpException) {
            emit(Resource.Error(message = e.localizedMessage ?: "Something went wrong!"))
        } catch (e: Exception) {
            emit(Resource.Error(message = e.localizedMessage ?: "Something went wrong!"))
        }
    }

    override suspend fun setUserInformation(
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

    override suspend fun uploadProfileImage(
        imageUri: Uri,
        userid: String
    ): Flow<Resource<Boolean>> = flow {
        emit(Resource.Loading())
        try {
            val uploadTask = firebaseStorage.reference
                .child("${Constants.PROFILE_IMAGE_STORAGE_REF}/image_${System.currentTimeMillis()}")
                .putFile(Uri.parse(imageUri.toString())).await()

            val downloadUrl = uploadTask.storage.downloadUrl.await().toString()
            val userObj = mutableMapOf<String, String>()
            userObj["profileImage"] = downloadUrl

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

    override suspend fun followUser(
        myId: String,
        userid: String
    ): Flow<Resource<Boolean>> = flow {
        try {
            emit(Resource.Loading())
//            firestore.collection(Constants.COLLECTION_USERS)
//                .document(myId)
//                .update("following", FieldValue.arrayUnion(userid))
//                .await()
//
//            firestore.collection(Constants.COLLECTION_USERS)
//                .document(userid)
//                .update("followers", FieldValue.arrayUnion(myId))
//                .await()
            firestore.runBatch { batch ->
                batch.update(
                    firestore.collection(Constants.COLLECTION_USERS)
                        .document(myId),
                    "following",
                    FieldValue.arrayUnion(userid)
                )

                batch.update(
                    firestore.collection(Constants.COLLECTION_USERS)
                        .document(userid),
                    "followers",
                    FieldValue.arrayUnion(myId)
                )
            }.await()

            emit(Resource.Success(data = true))

        } catch (e: IOException) {
            emit(Resource.Error(message = e.localizedMessage ?: "Something went wrong!"))
        } catch (e: HttpException) {
            emit(Resource.Error(message = e.localizedMessage ?: "Something went wrong!"))
        } catch (e: Exception) {
            emit(Resource.Error(message = e.localizedMessage ?: "Something went wrong!"))
        }
    }

    override suspend fun unFollowUser(myId: String, userid: String): Flow<Resource<Boolean>> =
        flow {
            try {
                emit(Resource.Loading())
//                firestore.collection(Constants.COLLECTION_USERS)
//                    .document(myId)
//                    .update("following", FieldValue.arrayRemove(userid))
//                    .await()
//
//                firestore.collection(Constants.COLLECTION_USERS)
//                    .document(userid)
//                    .update("followers", FieldValue.arrayRemove(myId))
//                    .await()

                firestore.runBatch { batch ->

                    batch.update(
                        firestore.collection(Constants.COLLECTION_USERS)
                            .document(myId),
                        "following",
                        FieldValue.arrayRemove(userid)
                    )

                    batch.update(
                        firestore.collection(Constants.COLLECTION_USERS)
                            .document(userid),
                        "followers",
                        FieldValue.arrayRemove(myId)
                    )
                }.await()

                emit(Resource.Success(data = true))

            } catch (e: IOException) {
                emit(Resource.Error(message = e.localizedMessage ?: "Something went wrong!"))
            } catch (e: HttpException) {
                emit(Resource.Error(message = e.localizedMessage ?: "Something went wrong!"))
            } catch (e: Exception) {
                emit(Resource.Error(message = e.localizedMessage ?: "Something went wrong!"))
            }
        }

    override suspend fun deletePost(postId: String, userId: String): Flow<Resource<Boolean>> =
        flow {
            try {
                emit(Resource.Loading())

//                firestore.collection(Constants.COLLECTION_POST)
//                    .document(postId)
//                    .delete()
//                    .await()
//
//                firestore.collection(Constants.COLLECTION_USERS)
//                    .document(userId)
//                    .update("posts", FieldValue.arrayRemove(postId))
//                    .await()

                firestore.runBatch { batch ->
                    batch.delete(
                        firestore.collection(Constants.COLLECTION_POST)
                            .document(postId)
                    )

                    batch.update(
                        firestore.collection(Constants.COLLECTION_USERS)
                            .document(userId),
                        "posts",
                        FieldValue.arrayRemove(postId)
                    )
                }.await()

                emit(Resource.Success(data = true))

            } catch (e: IOException) {
                emit(Resource.Error(message = e.localizedMessage ?: "Something went wrong!"))
            } catch (e: HttpException) {
                emit(Resource.Error(message = e.localizedMessage ?: "Something went wrong!"))
            } catch (e: Exception) {
                emit(Resource.Error(message = e.localizedMessage ?: "Something went wrong!"))
            }

        }

    override suspend fun getFollowListUsers(followList: List<String>): Flow<Resource<List<User>?>> =
        flow {
            try {
                emit(Resource.Loading())
                val query = firestore.collection(Constants.COLLECTION_USERS)
                    .whereIn(Constants.USER_ID, followList)
                    .get()
                    .await()

                val result = query.toObjects(User::class.java)
                emit(Resource.Success(result.sortedBy { it.username }))

            } catch (e: IOException) {
                emit(Resource.Error(message = e.localizedMessage ?: "Something went wrong!"))
            } catch (e: HttpException) {
                emit(Resource.Error(message = e.localizedMessage ?: "Something went wrong!"))
            } catch (e: Exception) {
                emit(Resource.Error(message = e.localizedMessage ?: "Something went wrong!"))
            }
        }

    override suspend fun changePassword(oldPass: String, newPass: String): Flow<Resource<Boolean>> =
        flow {
            try {
                emit(Resource.Loading())

                val user = auth.currentUser
                val userEmail = user?.email
                val credential: AuthCredential =
                    EmailAuthProvider.getCredential(userEmail!!, oldPass)

                user.reauthenticate(credential).await()
                user.updatePassword(newPass).await()


                emit(Resource.Success(data = true))


            } catch (e: IOException) {
                emit(Resource.Error(message = e.localizedMessage ?: "Something went wrong!"))
            } catch (e: HttpException) {
                emit(Resource.Error(message = e.localizedMessage ?: "Something went wrong!"))
            } catch (e: Exception) {
                emit(Resource.Error(message = e.localizedMessage ?: "Something went wrong!"))
            }
        }

    override suspend fun changeEmail(
        oldPass: String,
        newEmail: String,
        userid: String
    ): Flow<Resource<Boolean>> =
        flow {
            try {
                emit(Resource.Loading())
                val userObj = mutableMapOf<String, String>()
                val user = auth.currentUser
                val userEmail = user?.email
                val credential: AuthCredential =
                    EmailAuthProvider.getCredential(userEmail!!, oldPass)

                user.reauthenticate(credential).await()
                user.updateEmail(newEmail).await()

                userObj["email"] = newEmail
                firestore.collection(Constants.COLLECTION_USERS)
                    .document(userid)
                    .update(userObj as Map<String, Any>)
                    .await()

                emit(Resource.Success(data = true))
            } catch (e: IOException) {
                emit(Resource.Error(message = e.localizedMessage ?: "Something went wrong!"))
            } catch (e: HttpException) {
                emit(Resource.Error(message = e.localizedMessage ?: "Something went wrong!"))
            } catch (e: Exception) {
                emit(Resource.Error(message = e.localizedMessage ?: "Something went wrong!"))
            }

        }
}