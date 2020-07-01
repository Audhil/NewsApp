package com.example.newsapp.di

import com.example.newsapp.NewsApplication
import com.example.newsapp.data.remote.API
import com.example.newsapp.util.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import okhttp3.Cache
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

@Module
@InstallIn(ApplicationComponent::class)
class APIModule {

    private val cacheSize: Long = 10 * 1024 * 1024  //  10 MiB

    private val cache by lazy {
        Cache(NewsApplication.INSTANCE.cacheDir, cacheSize)
    }

    private val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .cache(cache)
            .addInterceptor(object : Interceptor {

                override fun intercept(chain: Interceptor.Chain): Response {
                    var request = chain.request()
                    if (!NewsApplication.INSTANCE.isNetworkConnected()) {
                        showVLog { "DATA FROM CACHE" }
                        val maxStale = 2 * 60 * 60 //  2 hrs in sec
                        request = request
                            .newBuilder()
                            .header("Cache-Control", "public, only-if-cached, max-stale=$maxStale")
                            .build()
                    }
                    return chain.proceed(request)
                }
            })
            .addInterceptor(logInterceptor.apply {
                level = if (NLog.DEBUG_BOOL)
                    HttpLoggingInterceptor.Level.BODY
                else
                    HttpLoggingInterceptor.Level.NONE
            })
            .connectTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    private val logInterceptor: HttpLoggingInterceptor by lazy {
        HttpLoggingInterceptor(
            object : HttpLoggingInterceptor.Logger {
                override fun log(message: String) {
                    showDLog { "logInterceptor: $message" }
                }
            }
        )
    }

    @Provides
    fun giveRetrofitAPIService(): API =
        Retrofit.Builder()
            .baseUrl(APIEndPoints.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
            .create(API::class.java)
}