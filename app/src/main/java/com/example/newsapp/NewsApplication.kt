package com.example.newsapp

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class NewsApplication : Application() {

    companion object {
        lateinit var INSTANCE: NewsApplication
    }

    override fun onCreate() {
        super.onCreate()
        INSTANCE = this
    }
}