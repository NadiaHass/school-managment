package com.tasdjilati.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.tasdjilati.data.entities.Student
import com.tasdjilati.data.entities.StudentTransport

@Dao
interface StudentTransportDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addStudent(student: StudentTransport)

    @Delete
    suspend fun deleteStudent(student: StudentTransport)

    @Update
    suspend fun updateStudent(student: StudentTransport)

    @Query("SELECT * FROM student_transport_table")
    fun getAllTransportStudents() : LiveData<List<StudentTransport>>

    @Query("SELECT * FROM student_transport_table WHERE id= :id")
    fun getStudentById(id: Int) : StudentTransport

    @Query("SELECT * FROM student_transport_table WHERE name LIKE :searchQuery OR surname LIKE :searchQuery ")
    fun searchDatabase(searchQuery : String) : LiveData<List<StudentTransport>>

    @Query("SELECT EXISTS(SELECT * FROM student_transport_table WHERE id= :id)")
    fun isRowExists(id : Int) : Boolean

    @Query("DELETE FROM student_transport_table")
    suspend fun deleteAllStudents()


}