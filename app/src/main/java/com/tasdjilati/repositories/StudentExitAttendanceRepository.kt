package com.tasdjilati.repositories

import androidx.lifecycle.LiveData
import com.tasdjilati.data.dao.StudentExitAttendanceDao
import com.tasdjilati.data.entities.StudentExitAttendance

class StudentExitAttendanceRepository (private val studentExitAttendanceDao : StudentExitAttendanceDao) {

    val getAllStudents : LiveData<List<StudentExitAttendance>> = studentExitAttendanceDao.getAllStudents()

    suspend fun addStudent(studentExit : StudentExitAttendance){
        studentExitAttendanceDao.addStudent(studentExit)
    }

    suspend fun deleteAttendanceTable(){
        studentExitAttendanceDao.deleteAttendanceTable()
    }
}