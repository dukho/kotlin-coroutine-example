package com.nomadworks.crtest

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.nomadworks.crtest.network.PostEntry
import com.nomadworks.crtest.network.SampleApiService
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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
    }
}
