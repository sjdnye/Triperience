package com.example.triperience.core.pagination

interface Paginator<Key, Item> {
    suspend fun loadNextItems()
    fun reset()
}