package com.example.newsapp.ui.main

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.example.newsapp.R
import com.example.newsapp.data.model.Article
import com.example.newsapp.data.model.HeadLinePojo
import com.example.newsapp.data.remote.API
import com.example.newsapp.rx.makeFlowableRxConnection
import com.example.newsapp.ui.base.BaseRepo
import com.example.newsapp.util.Constants
import com.example.newsapp.util.ErrorLiveData
import dagger.hilt.android.qualifiers.ApplicationContext
import io.reactivex.disposables.Disposable
import javax.inject.Inject

class MainRepo
@Inject
constructor(
    private val errorLiveDataa: ErrorLiveData,
    private val api: API,
    @ApplicationContext private val context: Context
) : BaseRepo(errorLiveDataa) {

    val articleListLiveData by lazy {
        MutableLiveData<List<Article>>()
    }

    fun loadFeeds(): Disposable =
        api.getHeadLines(
            apiKey = context.getString(R.string.api_key)
        ).makeFlowableRxConnection(this, Constants.NEWS_FEEDS)

    override fun onSuccess(obj: Any?, tag: String) {
        when (tag) {
            Constants.NEWS_FEEDS ->
                (obj as? HeadLinePojo)?.let {
                    it.articles?.run {
                        articleListLiveData.value = this
                    }
                }

            else ->
                Unit
        }
    }
}