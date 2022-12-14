package com.example.triperience.features.search.data.repository

import com.example.triperience.features.authentication.domain.model.User
import com.example.triperience.features.profile.domain.model.Post
import com.example.triperience.features.search.domain.repository.SearchRepository
import com.example.triperience.utils.Constants
import com.example.triperience.utils.Resource
import com.example.triperience.utils.await
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class SearchRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : SearchRepository {
    override suspend fun searchUsers(query: String): Flow<Resource<List<User>?>> = flow {
        try {
            emit(Resource.Loading())
            val query = firestore.collection(Constants.COLLECTION_USERS)
                .orderBy(Constants.USER_USERNAME)
                .startAt(query)
                .endAt(query + "\uf8ff")
                .get()
                .await()

            val result : List<User> = query.toObjects(User::class.java)
            emit(Resource.Success(data = result))

        }catch (e: IOException) {
            emit(Resource.Error(message = e.localizedMessage ?: "Something went wrong!"))
        } catch (e: HttpException) {
            emit(Resource.Error(message = e.localizedMessage ?: "Something went wrong!"))
        } catch (e: Exception) {
            emit(Resource.Error(message = e.localizedMessage ?: "Something went wrong!"))
        }
    }


}