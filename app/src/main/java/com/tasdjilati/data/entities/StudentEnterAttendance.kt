package com.tasdjilati.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "enter_attendance_table")
data class StudentEnterAttendance (
    @PrimaryKey
    var id : Int ,
    var name : String ,
    var surname : String ,
    var birthDate : String ,
    var year : String ,
    var classe : String ,
    var numParent1 : String ,
    var numParent2 : String ,
    var address : String ,
    var attendance : Int
    )