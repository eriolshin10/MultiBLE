package com.rb.caapplication.base

import android.os.Bundle
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding

abstract class BaseActivity<VB: ViewDataBinding, VM: BaseViewModel>(@LayoutRes private val layoutId: Int) :
    AppCompatActivity() {

        lateinit var binding: VB
        protected abstract val viewModel: VM

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, layoutId)
        binding.lifecycleOwner = this

        initVariable()
        initPermission()
        initObserver()
    }

    abstract fun initVariable()
    abstract fun initPermission()
    abstract fun initObserver()
}