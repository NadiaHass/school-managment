package com.tasdjilati.repositories

import androidx.lifecycle.LiveData
import com.tasdjilati.data.dao.StudentEnterAttendanceDao
import com.tasdjilati.data.entities.StudentEnterAttendance

class StudentEnterAttendanceRepository (private val studentEnterAttendanceDao : StudentEnterAttendanceDao) {

    val getAllStudents : LiveData<List<StudentEnterAttendance>> = studentEnterAttendanceDao.getAllStudents()
    suspend fun addStudent(studentEnter : StudentEnterAttendance){
        studentEnterAttendanceDao.addStudent(studentEnter)
    }

    suspend fun deleteAttendanceTable(){
        studentEnterAttendanceDao.deleteAttendanceTable()
    }

    suspend fun updateStudentAttendance(attendance : Int , id : Int){
        studentEnterAttendanceDao.updateStudentAttendance(attendance , id)
    }

    fun getStudentById(id: Int) : StudentEnterAttendance{
        return studentEnterAttendanceDao.getStudentById(id)
    }

}