package com.example.chummerly.data.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.chummerly.data.entity.CardEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CardDao {
    @Insert
    fun insert(card: CardEntity)

    @Insert
    fun insertAll(cards: List<CardEntity>)

    @Query("SELECT * FROM card")
    fun getAll(): Flow<List<CardEntity>>

    @Query("SELECT * FROM card WHERE id = :id")
    fun findById(id: Long): CardEntity

    @Query("SELECT * FROM card WHERE deckId = :deckId")
    fun findByDeckId(deckId: Long): List<CardEntity>

    @Query("""
        SELECT * FROM card 
        WHERE front LIKE :keyword
        OR back LIKE :keyword
        OR tags LIKE :keyword
    """)
    fun filterByKeyword(keyword: String): List<CardEntity>

    @Query("SELECT * FROM card WHERE deckId = :deckId")
    fun trueFindByDeckId(deckId: Long): Flow<List<CardEntity>>

    @Query("SELECT * FROM card WHERE id IN (:cardIds)")
    fun loadAllByIds(cardIds: IntArray): Flow<List<CardEntity>>

    @Update
    fun updateCards(vararg cards: CardEntity)

    @Delete
    fun delete(card: CardEntity)

    @Query("DELETE FROM card WHERE id = :cardId")
    fun deleteById(cardId: Long)
}