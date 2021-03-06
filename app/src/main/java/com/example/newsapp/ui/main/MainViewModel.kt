package com.example.newsapp.ui.main

import androidx.databinding.ObservableField
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import com.example.newsapp.data.model.Article
import com.example.newsapp.ui.base.BaseViewModel
import com.example.newsapp.util.ErrorLiveData

class MainViewModel
@ViewModelInject
constructor(
    private val mainRepo: MainRepo
) : BaseViewModel() {

    val pageLoadingProgress = ObservableField(true)

    val errorLiveData: ErrorLiveData = mainRepo.errorLiveData

    val articlesListLiveData: LiveData<List<Article>> = mainRepo.articleListLiveData

    fun loadHeadlines() =
        compositeDisposable.add(
            mainRepo.loadHeadlines()
        )
}