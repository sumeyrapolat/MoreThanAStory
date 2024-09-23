package com.sumeyra.morethanastory.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.sumeyra.morethanastory.data.db.entitiy.LikedStoryEntity

@Dao
interface LikedStoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLikedStory(likedStory: LikedStoryEntity)

    @Query("DELETE FROM liked_stories WHERE storyId = :storyId")
    suspend fun deleteLikedStory(storyId: String)

    @Query("SELECT * FROM liked_stories WHERE storyId = :storyId LIMIT 1")
    suspend fun isStoryLiked(storyId: String): LikedStoryEntity?

    @Query("SELECT * FROM liked_stories")
    suspend fun getAllLikedStories(): List<LikedStoryEntity>
}
