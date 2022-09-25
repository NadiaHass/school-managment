package com.tasdjilati.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.tasdjilati.data.entities.StudentEnterAttendance
import com.tasdjilati.data.entities.StudentExitAttendance

@Dao
interface StudentExitAttendanceDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addStudent(studentExit: StudentExitAttendance)

    @Update
    suspend fun updateStudent(student : StudentExitAttendance)

    @Query("SELECT * FROM exit_attendance_table ORDER BY name ASC")
    fun getAllStudents() : LiveData<List<StudentExitAttendance>>

    @Query("DELETE FROM exit_attendance_table")
    suspend fun deleteAttendanceTable()

    @Query("SELECT * FROM exit_attendance_table WHERE id= :id")
    fun getStudentById(id: Int) : StudentExitAttendance

    @Query("SELECT EXISTS(SELECT * FROM exit_attendance_table WHERE id= :id)")
    fun isRowExists(id : Int) : Boolean
}