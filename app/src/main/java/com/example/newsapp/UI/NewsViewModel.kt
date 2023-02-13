package com.example.newsapp.UI

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.ConnectivityManager.*
import android.net.NetworkCapabilities.*
import android.os.Build
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.newsapp.UI.database.ArticleDatabase
import com.example.newsapp.UI.models.Article
import com.example.newsapp.UI.models.NewsResponse
import com.example.newsapp.UI.repository.NewsRepository
import com.example.newsapp.UI.util.Resource
import kotlinx.coroutines.launch
import okio.IOException
import retrofit2.Response

class NewsViewModel(
    application: Application
) : AndroidViewModel(application) {


    private val newsRepository = NewsRepository(ArticleDatabase(context = application))

    val breakingNews: MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
    var breakingNewsPage = 1
    var breakingNewsResponse: NewsResponse? = null

    val searchNews: MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
    var searchNewsPage = 1
    var searhNewsResponse: NewsResponse? = null

    init {
        getBreakingNews("IN")
    }

    fun getBreakingNews(countrycode: String) = viewModelScope.launch {
      safeBreakingNewsCall(countrycode)

    }

    fun searchNews(searchQuery: String) = viewModelScope.launch {
        safeSearchNewsCall(searchQuery)
    }

    private fun handleBreakingNewsResponce(responce: Response<NewsResponse>) : Resource<NewsResponse>{
        if(responce.isSuccessful){
            responce.body()?.let { resultResponce ->
                breakingNewsPage++
                if (breakingNewsResponse == null){
                    breakingNewsResponse = resultResponce
                }else{
                    val oldArticle = breakingNewsResponse?.articles
                    val newArticle = resultResponce.articles
                    oldArticle?.addAll(newArticle)
                }
                return Resource.Success(breakingNewsResponse ?:resultResponce)
            }
        }
        return Resource.Error(responce.message())
    }

    private fun handleSearchNewsResponce(responce: Response<NewsResponse>) : Resource<NewsResponse>{
        if(responce.isSuccessful){
            responce.body()?.let { resultResponce ->
                searchNewsPage++
                if (searhNewsResponse == null){
                    searhNewsResponse = resultResponce
                }else{
                    val oldArticle = searhNewsResponse?.articles
                    val newArticle = resultResponce.articles
                    oldArticle?.addAll(newArticle)
                }
                return Resource.Success(searhNewsResponse ?:resultResponce)
            }
        }
        return Resource.Error(responce.message())
    }

    fun savedArticle(article: Article) = viewModelScope.launch {
        newsRepository.upsert(article)
    }

    fun getSavedNews() = newsRepository.getSavedNews()

    fun deleteArticle(article: Article) = viewModelScope.launch {
        newsRepository.deleteArticle(article)
    }

    private suspend fun safeSearchNewsCall(searchQuery: String) {
        searchNews.postValue(Resource.Loading())
        try {
            if (hasInternetConnection()) {
                val responce = newsRepository.searchForNews(searchQuery,searchNewsPage)
                searchNews.postValue(handleSearchNewsResponce(responce))
            }else {
                searchNews.postValue(Resource.Error("No internet connection"))
            }
        }catch (t: Throwable){
            when(t) {
                is IOException -> searchNews.postValue(Resource.Error("Network failure"))
                else -> searchNews.postValue(Resource.Error("Conversion Error"))
            }
        }
    }

    private suspend fun safeBreakingNewsCall(countrycode: String) {
        breakingNews.postValue(Resource.Loading())
        try {
            if (hasInternetConnection()) {
                val responce = newsRepository.getBreakingNews(countrycode,breakingNewsPage)
                breakingNews.postValue(handleBreakingNewsResponce(responce))
            }else {
                breakingNews.postValue(Resource.Error("No internet connection"))
            }
        }catch (t: Throwable){
            when(t) {
                is IOException -> breakingNews.postValue(Resource.Error("Network failure"))
                else -> breakingNews.postValue(Resource.Error("Conversion Error"))
            }
        }
    }

    private fun hasInternetConnection() : Boolean{
        val connectivityManager = getApplication<NewsApplication>().getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            val activeNetwork = connectivityManager.activeNetwork ?: return false
            val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
            return when {
                capabilities.hasTransport(TRANSPORT_WIFI) -> true
                capabilities.hasTransport(TRANSPORT_CELLULAR) -> true
                capabilities.hasTransport(TRANSPORT_ETHERNET) -> true
                else -> false
            }
        }else {
            connectivityManager.activeNetworkInfo ?.run {
                return when(type) {
                    TYPE_WIFI -> true
                    TYPE_MOBILE -> true
                    TYPE_ETHERNET -> true
                    else -> false
                }
            }
        }
        return false
    }

}