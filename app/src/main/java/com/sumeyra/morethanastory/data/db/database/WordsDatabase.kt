package com.sumeyra.morethanastory.data.db.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.sumeyra.morethanastory.data.db.dao.WordsDao
import com.sumeyra.morethanastory.data.db.entitiy.WordEntity

@Database(entities = [WordEntity::class], version = 1)
abstract class WordsDatabase : RoomDatabase() {

    abstract fun wordsDao(): WordsDao

}