package com.example.newsapp.ui.base

import android.os.Bundle
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModel

abstract class BaseLifeCycleActivity<B : ViewDataBinding, T : ViewModel> : BaseActivity() {

    abstract fun getBindingVariable(): Int

    abstract fun initErrorObserver()

    @LayoutRes
    abstract fun getLayoutId(): Int

    abstract val viewModel: T

    lateinit var viewDataBinding: B

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        performDataBinding()
        initErrorObserver()
    }

    private fun performDataBinding() {
        viewDataBinding = DataBindingUtil.setContentView(this, getLayoutId())
        viewDataBinding.apply {
            setVariable(getBindingVariable(), viewModel)
            executePendingBindings()
        }
    }
}