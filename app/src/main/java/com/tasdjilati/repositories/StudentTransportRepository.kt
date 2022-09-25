package com.tasdjilati.repositories

import androidx.lifecycle.LiveData
import com.tasdjilati.data.dao.StudentDao
import com.tasdjilati.data.dao.StudentTransportDao
import com.tasdjilati.data.entities.Student
import com.tasdjilati.data.entities.StudentTransport

class StudentTransportRepository(private val studentTransportDao : StudentTransportDao) {

    val getAllStudents : LiveData<List<StudentTransport>> = studentTransportDao.getAllTransportStudents()

    suspend fun addStudent(student: StudentTransport){
        studentTransportDao.addStudent(student)
    }

    suspend fun deleteStudent(student: StudentTransport){
        studentTransportDao.deleteStudent(student)
    }

    suspend fun updateStudent(student: StudentTransport){
        studentTransportDao.updateStudent(student)
    }

    suspend fun deleteAllStudents(){
        studentTransportDao.deleteAllStudents()
    }

    fun getStudentById(id: Int) : StudentTransport {
        return studentTransportDao.getStudentById(id)
    }

    fun searchDatabase(searchQuery : String) : LiveData<List<StudentTransport>> {
        return studentTransportDao.searchDatabase(searchQuery)
    }

     fun isRowExists(id : Int) : Boolean{
        return studentTransportDao.isRowExists(id)
    }

}