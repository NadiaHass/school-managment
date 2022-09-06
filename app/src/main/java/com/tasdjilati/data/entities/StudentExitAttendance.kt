package com.tasdjilati.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "exit_attendance_table")
class StudentExitAttendance (
    @PrimaryKey
    @ColumnInfo(name = "id")
    var id : Int ,
    var name : String ,
    var surname : String ,
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