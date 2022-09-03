package com.tasdjilati.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.tasdjilati.data.entities.Student

@Dao
interface StudentDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addStudent(student: Student)

    @Query("SELECT * FROM student_table ORDER BY name ASC")
    fun getAllStudents() : LiveData<List<Student>>

}