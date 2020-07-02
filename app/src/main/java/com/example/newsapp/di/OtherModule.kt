package com.example.newsapp.di

import com.example.newsapp.ui.main.FeedListAdapter
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent

@Module
@InstallIn(ActivityComponent::class)
class OtherModule {

    @Provides
    fun giveFeedListAdapter(): FeedListAdapter = FeedListAdapter()
}