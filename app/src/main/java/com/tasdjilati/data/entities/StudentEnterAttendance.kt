package com.tasdjilati.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "enter_attendance_table")
data class StudentEnterAttendance (
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "id")
    var id : Int ,
    @ColumnInfo(name = "name")
    var name : String ,
    @ColumnInfo(name = "surname")
    var surname : String ,
    @ColumnInfo(name = "date")
    var birthDate : String ,
    var year : String ,
    var classe : String ,
    @ColumnInfo(name = "numParent1")
    var numParent1 : String ,
    @ColumnInfo(name = "numParent2")
    var numParent2 : String ,
    var address : String ,
    var attendance : Int
    )