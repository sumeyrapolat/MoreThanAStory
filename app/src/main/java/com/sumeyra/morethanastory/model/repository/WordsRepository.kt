package com.sumeyra.morethanastory.model.repository

import com.sumeyra.morethanastory.data.db.dao.WordsDao
import com.sumeyra.morethanastory.data.db.entitiy.WordEntity
import javax.inject.Inject

class WordsRepository @Inject constructor(
    private val wordsDao: WordsDao
) {

    suspend fun insertWords(words: List<WordEntity>) {
        wordsDao.insertWords(words)
    }

    suspend fun getAllWords(): List<WordEntity> {
        return wordsDao.getAllWords()
    }

    suspend fun deleteWords() {
        wordsDao.deleteWords()
    }
}