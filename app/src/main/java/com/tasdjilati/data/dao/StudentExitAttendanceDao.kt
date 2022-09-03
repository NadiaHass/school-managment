package com.tasdjilati.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.tasdjilati.data.entities.StudentExitAttendance

@Dao
interface StudentExitAttendanceDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addStudent(studentExit: StudentExitAttendance)

    @Query("SELECT * FROM exit_attendance_table ORDER BY name ASC")
    fun getAllStudents() : LiveData<List<StudentExitAttendance>>

    @Query("DELETE FROM exit_attendance_table")
    suspend fun deleteAttendanceTable()
}