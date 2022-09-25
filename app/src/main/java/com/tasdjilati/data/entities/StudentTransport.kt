package com.tasdjilati.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "student_transport_table")
data class StudentTransport (
    @PrimaryKey(autoGenerate = false)
    var id : Int ,
    @ColumnInfo(name = "name")
    var name : String ,
    @ColumnInfo(name = "surname")
    var surname : String ,
    var birthDate : String ,
    var year : String ,
    var classe : String ,
    var numParent1 : String ,
    var numParent2 : String ,
    var address : String ,
    var destination : String
) : Serializable