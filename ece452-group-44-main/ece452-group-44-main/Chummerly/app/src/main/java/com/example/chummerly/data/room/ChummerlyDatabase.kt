package com.example.chummerly.data.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.chummerly.data.entity.CardEntity
import com.example.chummerly.data.entity.Category
import com.example.chummerly.data.entity.DeckEntity

@Database(
    entities = [
        CardEntity::class,
        DeckEntity::class,
        Category::class
    ],
    version = 1
)
abstract class ChummerlyDatabase : RoomDatabase() {
    abstract fun cardDao(): CardDao
    abstract fun deckDao(): DeckDao
    abstract fun categoryDao(): CategoryDao
}