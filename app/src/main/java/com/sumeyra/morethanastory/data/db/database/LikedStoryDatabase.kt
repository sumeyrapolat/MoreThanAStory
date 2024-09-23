package com.sumeyra.morethanastory.data.db.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.sumeyra.morethanastory.data.db.dao.LikedStoryDao
import com.sumeyra.morethanastory.data.db.entitiy.LikedStoryEntity

@Database(entities = [LikedStoryEntity::class], version = 1)
abstract class LikedStoryDatabase : RoomDatabase() {
    abstract fun likedStoryDao(): LikedStoryDao
}
