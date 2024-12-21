package com.dicoding.storyapp.data.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface StoryDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertAll(storyEntity: List<Story>)

    @Query("SELECT * FROM story ORDER BY id ASC")
    fun getAllStories(): LiveData<List<Story>>

    @Query("DELETE FROM story")
    fun deleteAllStories()
}