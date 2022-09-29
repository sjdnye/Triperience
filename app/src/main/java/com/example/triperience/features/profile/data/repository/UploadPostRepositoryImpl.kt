package com.example.triperience.features.profile.data.repository

import android.net.Uri
import com.example.triperience.features.profile.domain.model.Post
import com.example.triperience.features.profile.domain.repository.UploadPostRepository
import com.example.triperience.utils.Constants
import com.example.triperience.utils.Resource
import com.example.triperience.utils.await
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class UploadPostRepositoryImpl @Inject constructor(
    private val firebaseStorage: FirebaseStorage,
    private val firestore: FirebaseFirestore
): UploadPostRepository {

    override suspend fun uploadPost(post: Post, imageUri: Uri): Flow<Resource<Boolean>> = flow{
        try {
            emit(Resource.Loading())
            val uploadTask = firebaseStorage.reference
                .child("${Constants.POST_IMAGE_STORAGE_REF}/${post.publisher}/post_${System.currentTimeMillis()}")
                .putFile(Uri.parse(imageUri.toString())).await()
            val downloadUrl = uploadTask.storage.downloadUrl.await().toString()

            val postId = firestore.collection(Constants.COLLECTION_POST).document().id
            post.postImage = downloadUrl
            post.postId = postId
            post.dateTime = System.currentTimeMillis()

            firestore.collection(Constants.COLLECTION_POST)
                .document(post.postId)
                .set(post)
                .await()

            firestore.collection(Constants.COLLECTION_USERS)
                .document(post.publisher)
                .update("posts", FieldValue.arrayUnion(postId))
                .await()

            emit(Resource.Success(data = true))
        }catch (e: IOException) {
            emit(Resource.Error(message = e.localizedMessage ?: "Something went wrong!"))
        } catch (e: HttpException) {
            emit(Resource.Error(message = e.localizedMessage ?: "Something went wrong!"))
        } catch (e: Exception) {
            emit(Resource.Error(message = e.localizedMessage ?: "Something went wrong!"))
        }
    }
}