package com.tasdjilati.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.tasdjilati.data.entities.SplashImage

@Dao
interface SplashImageDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertImage(image : SplashImage)

    @Query("SELECT * FROM image_table")
    fun getImage() : LiveData<List<SplashImage>>

    @Query("UPDATE image_table SET name = :name")
    suspend fun updateName(name : String)

}