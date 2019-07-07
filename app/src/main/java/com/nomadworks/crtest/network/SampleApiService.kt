package com.nomadworks.crtest.network

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.*
import okhttp3.OkHttpClient
import okhttp3.Request
import timber.log.Timber

class SampleApiService {
    private val client = OkHttpClient()

    companion object {
        private val API_DELAY_MSEC = 3000L
        private val POST_LIST_ENDPOINT = "https://jsonplaceholder.typicode.com/posts"
    }

    //DON'T DO THIS
    fun fetchPostEntriesAsync(): Deferred<List<PostEntry>> {
        return GlobalScope.async(Dispatchers.IO) {
            Timber.d("[ktcr] #1 thread = ${Thread.currentThread().name}")
            delay(API_DELAY_MSEC)
            Timber.d("[ktcr] #2 thread = ${Thread.currentThread().name}")
            fetchPostEntriesSync()
        }
    }

    suspend fun fetchPostEntriesSuspend(): List<PostEntry> {
        return withContext(Dispatchers.IO) {
            Timber.d("[ktcr] #1-1 thread = ${Thread.currentThread().name}")
            delay(API_DELAY_MSEC)
            Timber.d("[ktcr] #2-1 thread = ${Thread.currentThread().name}")
            fetchPostEntriesSync()
        }
    }

    fun fetchPostEntriesSync(): List<PostEntry> {
        val request = Request.Builder().url(POST_LIST_ENDPOINT).build()
        val response = client.newCall(request).execute()
        val postType = object : TypeToken<List<PostEntry>>() {}.type
        return Gson().fromJson<List<PostEntry>>(response.body()?.string(), postType)
    }
}