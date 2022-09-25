package com.tasdjilati.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.tasdjilati.data.entities.StudentEnterAttendance

@Dao
interface StudentEnterAttendanceDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addStudent(studentEnter: StudentEnterAttendance)

    @Update
    suspend fun updateStudent(student : StudentEnterAttendance)

    @Query("SELECT * FROM enter_attendance_table")
    fun getAllStudents() : LiveData<List<StudentEnterAttendance>>

    @Query("DELETE FROM enter_attendance_table")
    suspend fun deleteAttendanceTable()

    @Query("SELECT * FROM enter_attendance_table WHERE id= :id")
    fun getStudentById(id: Int) : StudentEnterAttendance

    @Query("SELECT EXISTS(SELECT * FROM enter_attendance_table WHERE id= :id)")
    fun isRowExists(id : Int) : Boolean

}