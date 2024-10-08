package com.example.newsapp.UI.data.local

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.newsapp.UI.data.model.Article

@Dao
interface ArticleDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(article: Article): Long

    @Query("SELECT * FROM articles")
    fun getAllArticles(): LiveData<List<Article>>

    @Delete
    suspend fun deleteArticles(article: Article)

}