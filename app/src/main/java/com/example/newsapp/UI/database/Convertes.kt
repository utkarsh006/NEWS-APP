package com.example.newsapp.UI.database

import androidx.room.TypeConverter
import com.example.newsapp.UI.models.Source

class Convertes {

    @TypeConverter
    fun fromSource(source: Source):String{
        return source.name
    }

    @TypeConverter
    fun toSource(name: String): Source {
        return Source(name ,name)
    }
}

