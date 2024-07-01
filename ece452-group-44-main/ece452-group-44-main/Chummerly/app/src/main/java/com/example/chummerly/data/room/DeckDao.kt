package com.example.chummerly.data.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.chummerly.data.entity.CardEntity
import com.example.chummerly.data.entity.DeckEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DeckDao {
    @Query("SELECT * FROM deck LEFT JOIN card ON deck.id = card.deckId WHERE deck.id = :deckId")
    fun loadDeckAndCards(deckId: Long): Flow<Map<DeckEntity, List<CardEntity>>>

    @Insert
    fun insert(deck: DeckEntity): Long

    @Query("SELECT * FROM deck")
    fun getAll(): Flow<List<DeckEntity>>

    @Query("SELECT * FROM deck WHERE id = :id")
    fun findById(id: Long): DeckEntity

    @Query("SELECT * FROM deck WHERE name = :name")
    fun findByName(name: String): DeckEntity

    @Query("SELECT * FROM deck WHERE name LIKE :keyword OR categoryId LIKE :keyword OR description LIKE :keyword")
    fun filterByKeyword(keyword: String): List<DeckEntity>

    @Update
    fun updateDecks(vararg decks: DeckEntity)

    @Delete
    fun delete(deck: DeckEntity)
}