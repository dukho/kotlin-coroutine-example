package com.nomadworks.crtest.screens

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nomadworks.crtest.network.PostEntry
import com.nomadworks.crtest.network.SampleApiService
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*
import timber.log.Timber

class VmScopeViewModel : ViewModel() {
    private val service = SampleApiService()
    private var count = 0

    private val _tick = MutableLiveData<String>()
    val tick: LiveData<String> get() = _tick

    private val _result = MutableLiveData<String>()
    val result: LiveData<String> get() = _result

    init {
        _tick.postValue(count.toString())
    }

    fun onTick() {
       count++
        _tick.postValue(count.toString())
    }

    private fun clearResult() {
        _result.postValue("")
    }

    // DON'T DO THIS. NOT A Good way
    fun onFetchAsync() {
        clearResult()
        Timber.d("[ktcr] #0 thread = ${Thread.currentThread().name}")

        // this can be called out of launch{} block ~ it returns Deferred / not a suspend
        // it's fine to call it inside the launch block though
        val list = service.fetchPostEntriesAsync()

        lateinit var response: List<PostEntry>
        viewModelScope.launch {
            Timber.d("[ktcr] #3 thread = ${Thread.currentThread().name}")
            withContext(Dispatchers.Main) {
                Timber.d("[ktcr] #5 thread = ${Thread.currentThread().name}")
                response = list.await()
                Timber.d("[ktcr] #6 thread = ${Thread.currentThread().name}")
                _result.postValue("$response")
            }
        }

        // note: this would keep going while list.await() is suspended
        Timber.d("[ktcr] #7 thread = ${Thread.currentThread().name}")
    }

    fun onFetchSuspend() {
        clearResult()
        viewModelScope.launch {
            Timber.d("[ktcr] #0-2 thread = ${Thread.currentThread().name}")

            // suspend should be called inside launch {} block
            val list = service.fetchPostEntriesSuspend()
            Timber.d("[ktcr] #4-1 thread = ${Thread.currentThread().name}")
            _result.postValue("$list")
        }

        // note: this would keep going while list.await() is suspended
        Timber.d("[ktcr] #6-1 thread = ${Thread.currentThread().name}")
    }

    fun onFetchAsyncWrappedSuspend() {
        clearResult()
        viewModelScope.launch {
            Timber.d("[ktcr] #0-21 thread = ${Thread.currentThread().name}")

            // suspend should be called inside launch {} block
            val list = async {
                service.fetchPostEntriesSuspend()
            }
            Timber.d("[ktcr] #4-11 thread = ${Thread.currentThread().name}")
            _result.postValue("${list.await()}")
        }

        // note: this would keep going while list.await() is suspended
        Timber.d("[ktcr] #6-11 thread = ${Thread.currentThread().name}")
    }
}