package com.example.triperience.features.searchedPost.data.repository

import com.example.triperience.features.profile.domain.model.Post
import com.example.triperience.features.searchedPost.domain.repository.SearchedPostsRepository
import com.example.triperience.utils.Constants
import com.example.triperience.utils.Resource
import com.example.triperience.utils.await
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class SearchedPostsRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : SearchedPostsRepository {

    override suspend fun searchPostsByCategory(category: String): Flow<Resource<List<Post>?>> = flow{
        try {
            emit(Resource.Loading())
            val query = firestore.collection(Constants.COLLECTION_POST)
                .whereEqualTo(Constants.POST_CATEGORY, category.trim())
                .get()
                .await()
            val posts = query.toObjects(Post::class.java)
            emit(Resource.Success(data = posts.sortedByDescending { it.dateTime }))

        }catch (e: IOException) {
            emit(Resource.Error(message = e.localizedMessage ?: "Something went wrong!"))
        } catch (e: HttpException) {
            emit(Resource.Error(message = e.localizedMessage ?: "Something went wrong!"))
        } catch (e: Exception) {
            emit(Resource.Error(message = e.localizedMessage ?: "Something went wrong!"))
        }
    }

    override suspend fun searchPostsByCity(city: String): Flow<Resource<List<Post>?>> = flow{
        try {
            emit(Resource.Loading())
            val query = firestore.collection(Constants.COLLECTION_POST)
                .whereEqualTo(Constants.POST_CITY, city)
                .get()
                .await()
            val posts = query.toObjects(Post::class.java)
            emit(Resource.Success(data = posts.sortedByDescending { it.dateTime }))

        }catch (e: IOException) {
            emit(Resource.Error(message = e.localizedMessage ?: "Something went wrong!"))
        } catch (e: HttpException) {
            emit(Resource.Error(message = e.localizedMessage ?: "Something went wrong!"))
        } catch (e: Exception) {
            emit(Resource.Error(message = e.localizedMessage ?: "Something went wrong!"))
        }
    }

    override suspend fun searchPostsByCoordinate(
        latitude: Double,
        longitude: Double
    ): Flow<Resource<List<Post>?>> = flow{
        try {
            emit(Resource.Loading())
            val query = firestore.collection(Constants.COLLECTION_POST)
                .whereGreaterThanOrEqualTo(Constants.POST_LATITUDE, latitude - 5)
                .whereLessThanOrEqualTo(Constants.POST_LATITUDE, latitude + 5)
//                .whereGreaterThanOrEqualTo(Constants.POST_LONGITUDE, longitude - 5)
//                .whereLessThanOrEqualTo(Constants.POST_LONGITUDE, longitude + 5)
                .get()
                .await()
            val posts = query.toObjects(Post::class.java)
            emit(Resource.Success(data = posts.sortedByDescending { it.dateTime }))

        }catch (e: IOException) {
            emit(Resource.Error(message = e.localizedMessage ?: "Something went wrong!"))
        } catch (e: HttpException) {
            emit(Resource.Error(message = e.localizedMessage ?: "Something went wrong!"))
        } catch (e: Exception) {
            emit(Resource.Error(message = e.localizedMessage ?: "Something went wrong!"))
        }
    }
}