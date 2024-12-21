package com.dicoding.storyapp.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Story::class], version = 1, exportSchema = false)
abstract class StoryRoomDatabase : RoomDatabase() {

    abstract fun storyDao(): StoryDao

    companion object {

        @Volatile
        private var INSTANCE: StoryRoomDatabase? = null

        fun getDatabase(context: Context): StoryRoomDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    StoryRoomDatabase::class.java,
                    "story_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
