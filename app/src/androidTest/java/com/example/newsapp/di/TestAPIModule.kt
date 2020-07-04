package com.example.newsapp.di

import com.example.newsapp.data.remote.API
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import io.reactivex.schedulers.TestScheduler
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory

@Module
@InstallIn(ApplicationComponent::class)
class TestAPIModule {

    @Provides
    fun giveRetrofitAPIService(): API =
        Retrofit.Builder()
            .baseUrl("http://localhost:8080/")
//            .baseUrl("http://127.0.0.1:8080") //  this too works
            .addConverterFactory(MoshiConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
            .create(API::class.java)

    @Provides
    fun giveTestScheduler(): TestScheduler? = null
}