package com.example.triperience.features.comment.data.repository

import com.example.triperience.features.authentication.domain.model.User
import com.example.triperience.features.comment.domain.model.Comment
import com.example.triperience.features.comment.domain.repository.CommentRepository
import com.example.triperience.utils.Constants
import com.example.triperience.utils.Resource
import com.example.triperience.utils.await
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class CommentRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : CommentRepository {
    override fun getAllComments(postId: String): Flow<Resource<List<Comment>?>> = callbackFlow {
        trySend(Resource.Loading())
        val snapShotListener = firestore
            .collection(Constants.COLLECTION_COMMENTS)
            .addSnapshotListener { snapshot, error ->
                val response = if (snapshot != null) {
                    val comments = snapshot.toObjects(Comment::class.java)
                    Resource.Success<List<Comment>?>(data = comments)
                } else {
                    Resource.Error(error?.message ?: error.toString())
                }
                trySend(response).isSuccess
            }
        awaitClose {
            snapShotListener.remove()
        }
    }

    override suspend fun getCommentPublisherInfo(publisherId: String): User? {
        val query = firestore.collection(Constants.COLLECTION_USERS)
            .document(publisherId)
            .get()
            .await()
        return query.toObject(User::class.java)
    }

}