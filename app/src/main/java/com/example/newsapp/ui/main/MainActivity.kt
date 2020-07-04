package com.example.newsapp.ui.main

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.newsapp.BR
import com.example.newsapp.R
import com.example.newsapp.databinding.ActivityMainBinding
import com.example.newsapp.ui.base.BaseLifeCycleActivity
import com.example.newsapp.ui.detail.DetailActivity
import com.example.newsapp.util.NetworkError
import com.example.newsapp.util.showToast
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : BaseLifeCycleActivity<ActivityMainBinding, MainViewModel>() {

    override fun getBindingVariable(): Int = BR.main_view_model

    override fun initErrorObserver() =
        viewModel.errorLiveData.observe(this, Observer {
            when (it) {
                NetworkError.DISCONNECTED,
                NetworkError.BAD_URL,
                NetworkError.UNKNOWN,
                NetworkError.SOCKET_TIMEOUT -> {
                    viewDataBinding.retryBtn.visibility = View.VISIBLE
                    getString(R.string.something_went_wrong).showToast()
                }

                else ->
                    Unit
            }
        })

    override fun getLayoutId(): Int = R.layout.activity_main

    override val viewModel: MainViewModel by viewModels()

    @Inject
    lateinit var feedListAdapter: FeedListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setUpClickListeners()
        setUpSwipeRefreshLayout()
        setUpRecyclerView()
        setUpDataObserver()
        if (savedInstanceState == null)
            refreshListener.onRefresh()
    }

    private fun setUpClickListeners() =
        viewDataBinding.retryBtn.run {
            setOnClickListener {
                visibility = View.GONE
                refreshListener.onRefresh()
            }
        }

    private fun setUpDataObserver() =
        viewModel.articlesListLiveData.observe(this, Observer {
            it?.let {
                viewModel.pageLoadingProgress.set(false)
                viewDataBinding.repoSwipeRefreshLayout.isRefreshing = false
                feedListAdapter.addItems(it)
            }
        })

    private val refreshListener = SwipeRefreshLayout.OnRefreshListener {
        viewModel.loadHeadlines()
    }

    private fun setUpSwipeRefreshLayout() =
        viewDataBinding.repoSwipeRefreshLayout.setOnRefreshListener(refreshListener)

    private fun setUpRecyclerView() =
        viewDataBinding.repoRecyclerView.run {
            adapter = feedListAdapter.apply {
                clickListener = { _, article ->
                    startActivity(DetailActivity.newIntent(article))
                }
            }
        }
}