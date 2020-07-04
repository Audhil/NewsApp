package com.example.newsapp.ui.detail

import android.content.Intent
import android.os.Bundle
import android.webkit.*
import androidx.activity.viewModels
import com.example.newsapp.BR
import com.example.newsapp.NewsApplication
import com.example.newsapp.R
import com.example.newsapp.data.model.Article
import com.example.newsapp.databinding.ActivityDetailBinding
import com.example.newsapp.ui.base.BaseLifeCycleActivity
import com.example.newsapp.util.Constants
import com.example.newsapp.util.isNetworkConnected

class DetailActivity : BaseLifeCycleActivity<ActivityDetailBinding, DetailViewModel>() {

    override fun getBindingVariable(): Int = BR.detail_view_model

    override fun initErrorObserver() {
    }

    override fun getLayoutId(): Int = R.layout.activity_detail

    override val viewModel: DetailViewModel by viewModels()

    companion object {
        fun newIntent(article: Article? = null) =
            Intent(NewsApplication.INSTANCE, DetailActivity::class.java).apply {
                article?.let {
                    putExtra(Constants.PARCEL, it)
                }
            }
    }

    private val newsUrl by lazy {
        intent.extras?.getParcelable<Article>(Constants.PARCEL)?.url
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        newsUrl?.let { url ->
            viewDataBinding.detailWebView.run {
                settings.run {
                    cacheMode = if (context.isNetworkConnected())
                        WebSettings.LOAD_DEFAULT
                    else
                        WebSettings.LOAD_CACHE_ELSE_NETWORK
                }
                webViewClient = object : WebViewClient() {
                    override fun shouldOverrideUrlLoading(
                        view: WebView?,
                        request: WebResourceRequest?
                    ): Boolean = false
                }
                webChromeClient = object : WebChromeClient() {
                    override fun onProgressChanged(view: WebView?, newProgress: Int) {
                        if (progress == 100)
                            viewModel.pageLoadingProgress.set(false)
                    }
                }
                loadUrl(url)
            }
        }
    }
}