package com.tasdjilati.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.tasdjilati.data.entities.Student

@Dao
interface StudentDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addStudent(student: Student)

    @Delete
    suspend fun deleteStudent(student : Student)

    @Update
    suspend fun updateStudent(student : Student)

    @Query("SELECT * FROM student_table")
    fun getAllStudents() : LiveData<List<Student>>

    @Query("SELECT * FROM student_table WHERE id= :id")
    fun getStudentById(id: Int) : Student

    @Query("SELECT * FROM student_table WHERE name LIKE :searchQuery OR surname LIKE :searchQuery ")
    fun searchDatabase(searchQuery : String) : LiveData<List<Student>>

    @Query("SELECT EXISTS(SELECT * FROM student_table WHERE id= :id)")
    fun isRowExists(id : Int) : Boolean

    @Query("DELETE FROM student_table")
    suspend fun deleteAllStudents()


}