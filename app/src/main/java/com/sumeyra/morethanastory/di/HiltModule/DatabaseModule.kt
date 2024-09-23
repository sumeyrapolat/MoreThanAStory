package com.sumeyra.morethanastory.di.HiltModule

import android.content.Context
import androidx.room.Room
import com.sumeyra.morethanastory.data.db.dao.LikedStoryDao
import com.sumeyra.morethanastory.data.db.dao.WordsDao
import com.sumeyra.morethanastory.data.db.database.LikedStoryDatabase
import com.sumeyra.morethanastory.data.db.database.WordsDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Singleton
    @Provides
    fun provideLikedStoryDatabase(@ApplicationContext context: Context): LikedStoryDatabase {
        return Room.databaseBuilder(
            context,
            LikedStoryDatabase::class.java,
            "liked_story_database"
        ).build()
    }

    @Singleton
    @Provides
    fun provideLikedStoryDao(database: LikedStoryDatabase): LikedStoryDao {
        return database.likedStoryDao()
    }

    @Singleton
    @Provides
    fun provideWordsDatabase(@ApplicationContext context: Context): WordsDatabase {
        return Room.databaseBuilder(
            context,
            WordsDatabase::class.java,
            "words_database"
        ).build()
    }

    @Singleton
    @Provides
    fun provideWordsDao(database: WordsDatabase): WordsDao {
        return database.wordsDao()
    }
}
