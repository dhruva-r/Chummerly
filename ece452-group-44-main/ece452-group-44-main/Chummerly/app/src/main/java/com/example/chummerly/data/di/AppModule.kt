package com.example.chummerly.data.di

import android.content.Context
import androidx.room.Room
import com.example.chummerly.data.room.CardDao
import com.example.chummerly.data.room.CategoryDao
import com.example.chummerly.data.room.ChummerlyDatabase
import com.example.chummerly.data.room.DeckDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext applicationContext: Context): ChummerlyDatabase {
        return Room.databaseBuilder(
            applicationContext,
            ChummerlyDatabase::class.java,
            "chummerly-db"
        ).build()
    }

    @Provides
    @Singleton
    fun provideDeckDao(db: ChummerlyDatabase): DeckDao {
        return db.deckDao()
    }

    @Provides
    @Singleton
    fun provideCardDao(db: ChummerlyDatabase): CardDao {
        return db.cardDao()
    }

    @Provides
    @Singleton
    fun provideCategoryDao(db: ChummerlyDatabase): CategoryDao {
        return db.categoryDao()
    }
}