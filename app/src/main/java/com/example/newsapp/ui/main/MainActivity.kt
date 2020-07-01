package com.example.newsapp.ui.main

import androidx.activity.viewModels
import com.example.newsapp.R
import com.example.newsapp.databinding.ActivityMainBinding
import com.example.newsapp.ui.base.BaseLifeCycleActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : BaseLifeCycleActivity<ActivityMainBinding, MainViewModel>() {

    override fun getBindingVariable(): Int = 0

    override fun initErrorObserver() {
    }

    override fun getLayoutId(): Int = R.layout.activity_main

    override val viewModel: MainViewModel by viewModels()
}