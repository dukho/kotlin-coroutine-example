package com.nomadworks.crtest.network

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import okhttp3.OkHttpClient
import okhttp3.Request
import timber.log.Timber

class SampleApiService {
    private val client = OkHttpClient()

    companion object {
        private val API_DELAY_MSEC = 3000L
        private val POST_LIST_ENDPOINT = "https://jsonplaceholder.typicode.com/posts"
    }

    fun fetchPostEntriesAsync(): Deferred<List<PostEntry>> {
        return GlobalScope.async {
            Timber.d("[ktcr] #1 thread = ${Thread.currentThread()}")
            delay(API_DELAY_MSEC)
            Timber.d("[ktcr] #2 thread = ${Thread.currentThread()}")
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