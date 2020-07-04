package com.example.newsapp.di

import com.example.newsapp.NewsApplication
import com.example.newsapp.data.remote.API
import com.example.newsapp.util.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import io.reactivex.schedulers.TestScheduler
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import okio.IOException
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
            .addNetworkInterceptor(object : Interceptor {
                @Throws(IOException::class)
                override fun intercept(chain: Interceptor.Chain): Response {
                    val originalResponse: Response = chain.proceed(chain.request())
                    val cacheControl = originalResponse.header("Cache-Control")
                    return if (cacheControl == null ||
                        cacheControl.contains("no-store") ||
                        cacheControl.contains("no-cache") ||
                        cacheControl.contains("must-revalidate") ||
                        cacheControl.contains("max-age=0")
                    )
                        originalResponse.newBuilder()
                            .removeHeader("Pragma")
                            .header("Cache-Control", "public, max-age=" + 5000)
                            .build()
                    else
                        originalResponse
                }
            })
            .addInterceptor(object : Interceptor {
                @Throws(IOException::class)
                override fun intercept(chain: Interceptor.Chain): Response {
                    var request: Request = chain.request()
                    if (!NewsApplication.INSTANCE.isNetworkConnected())
                        request = request.newBuilder()
                            .removeHeader("Pragma")
                            .header("Cache-Control", "public, only-if-cached")
                            .build()
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

    @Provides
    fun giveTestScheduler(): TestScheduler? = null
}