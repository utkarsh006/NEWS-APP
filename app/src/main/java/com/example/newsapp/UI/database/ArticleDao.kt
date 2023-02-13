package com.example.newsapp.UI.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.newsapp.UI.models.Article


@Dao
interface ArticleDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(article: Article): Long

    @Query("SELECT * FROM articles")
    fun getAllArticles(): LiveData<List<Article>>

    @Delete
    suspend fun deleteArticles(article: Article)

}