package com.example.newsapp.UI.data.remote

import com.example.newsapp.UI.data.model.NewsResponse
import com.example.newsapp.UI.util.Constants.API_KEY
import com.example.newsapp.UI.util.Constants.GET_BREAKING_NEWS_ENDPOINT
import com.example.newsapp.UI.util.Constants.SEARCH_ENDPOINT
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsApi {

    @GET(GET_BREAKING_NEWS_ENDPOINT)
    suspend fun getBreakingNews(
        @Query("country") countrycode: String = "us",
        @Query("page") pagenumber: Int = 1,
        @Query("apiKey") apiKey: String = API_KEY
    ): Response<NewsResponse>

    @GET(SEARCH_ENDPOINT)
    suspend fun searchForNews(
        @Query("q") searchQuery: String,
        @Query("page") pagenumber: Int = 1,
        @Query("apiKey") apiKey: String = API_KEY
    ): Response<NewsResponse>
}
