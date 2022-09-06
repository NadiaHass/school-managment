package com.tasdjilati.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.tasdjilati.data.entities.StudentEnterAttendance

@Dao
interface StudentEnterAttendanceDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addStudent(studentEnter: StudentEnterAttendance)

    @Query("SELECT * FROM enter_attendance_table ORDER BY name ASC")
    fun getAllStudents() : LiveData<List<StudentEnterAttendance>>

    @Query("DELETE FROM enter_attendance_table")
    suspend fun deleteAttendanceTable()

    @Query("UPDATE enter_attendance_table SET attendance = :attendance WHERE id= :id")
    suspend fun updateStudentAttendance(attendance : Int , id : Int)

    @Query("SELECT * FROM enter_attendance_table WHERE id= :id")
    fun getStudentById(id: Int) : StudentEnterAttendance

}