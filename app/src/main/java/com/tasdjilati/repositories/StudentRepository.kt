package com.tasdjilati.repositories

import androidx.lifecycle.LiveData
import com.tasdjilati.data.dao.StudentDao
import com.tasdjilati.data.entities.Student

class StudentRepository(private val studentDao : StudentDao) {

    val getAllStudents : LiveData<List<Student>> = studentDao.getAllStudents()

    suspend fun addStudent(student : Student){
        studentDao.addStudent(student)
    }
}