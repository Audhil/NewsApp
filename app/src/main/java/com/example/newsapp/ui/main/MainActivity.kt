package com.example.newsapp.ui.main

import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.newsapp.BR
import com.example.newsapp.R
import com.example.newsapp.databinding.ActivityMainBinding
import com.example.newsapp.ui.base.BaseLifeCycleActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : BaseLifeCycleActivity<ActivityMainBinding, MainViewModel>() {

    override fun getBindingVariable(): Int = BR.main_view_model

    override fun initErrorObserver() {
    }

    override fun getLayoutId(): Int = R.layout.activity_main

    override val viewModel: MainViewModel by viewModels()

    @Inject
    lateinit var feedListAdapter: FeedListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setUpSwipeRefreshLayout()
        setUpRecyclerView()
        setUpDataObserver()
        refreshListener.onRefresh()
    }

    private fun setUpDataObserver() =
        viewModel.articlesListLiveData.observe(this, Observer {
            it?.let {
                viewDataBinding.repoSwipeRefreshLayout.isRefreshing = false
                feedListAdapter.addItems(it)
            }
        })

    private val refreshListener = SwipeRefreshLayout.OnRefreshListener {
        viewModel.loadFeeds()
    }

    private fun setUpSwipeRefreshLayout() =
        viewDataBinding.repoSwipeRefreshLayout.setOnRefreshListener(refreshListener)

    private fun setUpRecyclerView() =
        viewDataBinding.repoRecyclerView.run {
            adapter = feedListAdapter.apply {
                clickListener = { clickedPos ->
                    println("yup: clicked: $clickedPos")
                }
            }
        }
}