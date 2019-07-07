package com.nomadworks.crtest.screens

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.nomadworks.crtest.R
import com.nomadworks.crtest.databinding.ActivityVmBinding

class VmScopeActivity : AppCompatActivity() {

    lateinit var viewBinding: ActivityVmBinding
    lateinit var viewModel: VmScopeViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProviders.of(this).get(VmScopeViewModel::class.java)
        viewBinding = DataBindingUtil.setContentView(this, R.layout.activity_vm)
        viewBinding.lifecycleOwner = this
        viewBinding.viewModel = viewModel
    }
}
