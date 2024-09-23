package com.sumeyra.morethanastory.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.sumeyra.morethanastory.data.db.entitiy.WordEntity

@Dao
interface WordsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWords(words: List<WordEntity>)


    @Query("SELECT * FROM words_table")
    suspend fun getAllWords(): List<WordEntity>


    @Query("DELETE FROM words_table")
    suspend fun deleteWords()
}