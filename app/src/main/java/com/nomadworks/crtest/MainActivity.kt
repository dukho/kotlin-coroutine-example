package com.nomadworks.crtest

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.nomadworks.crtest.network.PostEntry
import com.nomadworks.crtest.network.SampleApiService
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*
import timber.log.Timber

class MainActivity : AppCompatActivity() {

    private var ticks = 0
    private val service = SampleApiService()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initUI()
    }

    private fun initUI() {
        btnTick.setOnClickListener {
            ticks++
            txtTick.text = ticks.toString()
        }

        btnFetch.setOnClickListener {
            txtResult.text = ""
            Timber.d("[ktcr] #0 thread = ${Thread.currentThread().name}")

            // this can be called out of launch{} block ~ it returns Deferred / not a suspend
            // it's fine to call it inside the launch block though
            val list = service.fetchPostEntriesAsync()

            lateinit var response: List<PostEntry>
            GlobalScope.launch {
                Timber.d("[ktcr] #3 thread = ${Thread.currentThread().name}")
                withContext(Dispatchers.Main) {
                    Timber.d("[ktcr] #5 thread = ${Thread.currentThread().name}")
                    response = list.await()
                    Timber.d("[ktcr] #6 thread = ${Thread.currentThread().name}")
                    txtResult.text = "$response"
                }
            }

            // note: this would keep going while list.await() is suspended
            Timber.d("[ktcr] #7 thread = ${Thread.currentThread().name}")
        }

        btnFetchSuspend.setOnClickListener {
            txtResult.text = ""
            Timber.d("[ktcr] #0-1 thread = ${Thread.currentThread().name}")

            GlobalScope.launch {
                Timber.d("[ktcr] #0-2 thread = ${Thread.currentThread().name}")

                // suspend should be called inside launch {} block
                val list = service.fetchPostEntriesSuspend()
                Timber.d("[ktcr] #4-1 thread = ${Thread.currentThread().name}")
                withContext(Dispatchers.Main) {
                    Timber.d("[ktcr] #5-1 thread = ${Thread.currentThread().name}")
                    txtResult.text = "$list"
                }
            }

            // note: this would keep going while list.await() is suspended
            Timber.d("[ktcr] #6-1 thread = ${Thread.currentThread().name}")
        }

        btnFetchSuspendAsync.setOnClickListener {
            txtResult.text = ""
            Timber.d("[ktcr] #0-11 thread = ${Thread.currentThread().name}")

            GlobalScope.launch {
                Timber.d("[ktcr] #0-21 thread = ${Thread.currentThread().name}")

                // suspend should be called inside launch {} block
                val list = async {
                    service.fetchPostEntriesSuspend()
                }
                Timber.d("[ktcr] #4-11 thread = ${Thread.currentThread().name}")
                withContext(Dispatchers.Main) {
                    Timber.d("[ktcr] #5-11 thread = ${Thread.currentThread().name}")
                    txtResult.text = "${list.await()}"
                }
            }

            // note: this would keep going while list.await() is suspended
            Timber.d("[ktcr] #6-11 thread = ${Thread.currentThread().name}")
        }
    }
}
