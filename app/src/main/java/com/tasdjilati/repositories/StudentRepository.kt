package com.tasdjilati.repositories

import androidx.lifecycle.LiveData
import com.tasdjilati.data.dao.StudentDao
import com.tasdjilati.data.entities.Student

class StudentRepository(private val studentDao : StudentDao) {

    val getAllStudents : LiveData<List<Student>> = studentDao.getAllStudents()

    suspend fun addStudent(student : Student){
        studentDao.addStudent(student)
    }

    suspend fun deleteStudent(student : Student){
        studentDao.deleteStudent(student)
    }

    suspend fun updateStudent(student : Student){
        studentDao.updateStudent(student)
    }

    suspend fun deleteAllStudents(){
        studentDao.deleteAllStudents()
    }


    fun getStudentById(id: Int) : Student {
        return studentDao.getStudentById(id)
    }

    fun searchDatabase(searchQuery : String) : LiveData<List<Student>>{
        return studentDao.searchDatabase(searchQuery)
    }

    fun isRowExists(id : Int) : Boolean{
        return studentDao.isRowExists(id)
    }

}