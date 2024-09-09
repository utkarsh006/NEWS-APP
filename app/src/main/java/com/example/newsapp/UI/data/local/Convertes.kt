package com.example.newsapp.UI.data.local

import androidx.room.TypeConverter
import com.example.newsapp.UI.data.model.Source

class Convertes {

    @TypeConverter
    fun fromSource(source: Source): String {
        return source.name
    }

    @TypeConverter
    fun toSource(name: String): Source {
        return Source(name, name)
    }
}

