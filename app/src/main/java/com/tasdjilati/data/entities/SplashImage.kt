package com.tasdjilati.data.entities

import android.graphics.Bitmap
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "image_table")
data class SplashImage (
    @PrimaryKey
    var id : Int ,
    var image : Bitmap ,
    @ColumnInfo(name = "name")
    var name : String
)