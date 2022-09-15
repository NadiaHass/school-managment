package com.tasdjilati.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.tasdjilati.data.entities.StudentEnterAttendance
import com.tasdjilati.data.entities.StudentExitAttendance

@Dao
interface StudentExitAttendanceDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addStudent(studentExit: StudentExitAttendance)

    @Query("SELECT * FROM exit_attendance_table ORDER BY name ASC")
    fun getAllStudents() : LiveData<List<StudentExitAttendance>>

    @Query("DELETE FROM exit_attendance_table")
    suspend fun deleteAttendanceTable()

    @Query("UPDATE exit_attendance_table SET attendance = :attendance WHERE id= :id")
    suspend fun updateStudentAttendance(attendance : Int , id : Int)

    @Query("SELECT * FROM exit_attendance_table WHERE id= :id")
    fun getStudentById(id: Int) : StudentExitAttendance

    @Query("SELECT EXISTS(SELECT * FROM student_table WHERE id= :id)")
    fun isRowExists(id : Int) : Boolean
}