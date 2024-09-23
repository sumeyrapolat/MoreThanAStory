package com.sumeyra.morethanastory.data.db.entitiy

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "liked_stories")
data class LikedStoryEntity(
    @PrimaryKey val storyId: String
)
