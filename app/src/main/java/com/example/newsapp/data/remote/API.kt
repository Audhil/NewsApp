package com.example.newsapp.data.remote

import com.example.newsapp.util.APIEndPoints
import io.reactivex.Flowable
import okhttp3.ResponseBody
import retrofit2.http.GET

interface API {
    @GET(APIEndPoints.TOP_HEADLINES)
    fun getHeadLines(): Flowable<ResponseBody>
}