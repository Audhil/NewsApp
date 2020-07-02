package com.example.newsapp.data.remote

import com.example.newsapp.data.model.HeadLinePojo
import com.example.newsapp.util.APIEndPoints
import io.reactivex.Flowable
import retrofit2.http.GET
import retrofit2.http.Query

interface API {
    @GET(APIEndPoints.TOP_HEADLINES)
    fun getHeadLines(
        @Query(APIEndPoints.COUNTRY)
        country: String = APIEndPoints.INDIA,
        @Query(APIEndPoints.CATEGORY)
        category: String = APIEndPoints.BUSINESS,
        @Query(APIEndPoints.API_KEY)
        apiKey: String
    ): Flowable<HeadLinePojo>
}