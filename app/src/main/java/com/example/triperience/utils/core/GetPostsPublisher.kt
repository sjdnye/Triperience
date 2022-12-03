package com.example.triperience.utils.core

import androidx.lifecycle.viewModelScope
import com.example.triperience.features.authentication.domain.model.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import javax.inject.Inject

class GetPostsPublisher @Inject constructor(
    private val coreRepository: CoreRepository
){

        suspend fun getPostPublisherDetail(userId: String) : User? {
            val job = CoroutineScope(context = Dispatchers.IO).async {
                return@async coreRepository.getPostPublisherDetail(userId)
            }
            return job.await()
        }

}