package com.example.triperience.features.home.data.repository

import com.example.triperience.features.authentication.domain.model.User
import com.example.triperience.features.home.domain.repository.HomeRepository
import com.example.triperience.features.profile.domain.model.Post
import com.example.triperience.utils.Constants
import com.example.triperience.utils.Resource
import com.example.triperience.utils.await
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class HomeRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : HomeRepository {
    override suspend fun getRelevantPost(followingList: List<String>): Flow<Resource<List<Post>?>> = flow{
        try {
            emit(Resource.Loading())
//            val query = firestore.collection(Constants.COLLECTION_POST)
//                .whereIn(Constants.PUBLISHER_ID, followingList)
//                .orderBy(Constants.DATE_TIME)
//                .get()
//                .await()
//            val posts = query.toObjects(Post::class.java)

            val query = firestore.collection(Constants.COLLECTION_POST)
                .get()
                .await()
            val posts = query.toObjects(Post::class.java)
            emit(Resource.Success(data = posts))

        }catch (e: IOException) {
            emit(Resource.Error(message = e.localizedMessage ?: "Something went wrong!"))
        } catch (e: HttpException) {
            emit(Resource.Error(message = e.localizedMessage ?: "Something went wrong!"))
        } catch (e: Exception) {
            emit(Resource.Error(message = e.localizedMessage ?: "Something went wrong!"))
        }
    }

    override suspend fun getPostPublisherDetail(userId: String): User? {
        val query = firestore.collection(Constants.COLLECTION_USERS)
            .document(userId)
            .get()
            .await()

        return query.toObject(User::class.java)
    }
}