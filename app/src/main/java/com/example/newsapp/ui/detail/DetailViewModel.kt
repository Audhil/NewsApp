package com.example.newsapp.ui.detail

import androidx.databinding.ObservableField
import androidx.hilt.lifecycle.ViewModelInject
import com.example.newsapp.ui.base.BaseViewModel

class DetailViewModel
@ViewModelInject
constructor() : BaseViewModel() {
    val pageLoadingProgress = ObservableField(true)
}