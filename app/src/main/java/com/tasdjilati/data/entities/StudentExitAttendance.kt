package com.tasdjilati.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "exit_attendance_table")
class StudentExitAttendance (
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