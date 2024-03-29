package com.tasdjilati.ui.main.exit

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.tasdjilati.data.StudentsDatabase
import com.tasdjilati.data.entities.StudentEnterAttendance
import com.tasdjilati.data.entities.StudentExitAttendance
import com.tasdjilati.repositories.StudentExitAttendanceRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class StudentExitAttendanceViewModel (application: Application) : AndroidViewModel(application) {
     val getAllStudents : LiveData<List<StudentExitAttendance>>
    private val repository : StudentExitAttendanceRepository

    init {
        val studentExitAttendanceDao = StudentsDatabase.getDatabase(application).getStudentExitAttendanceDao()
        repository = StudentExitAttendanceRepository(studentExitAttendanceDao)
        getAllStudents = repository.getAllStudents
    }

    fun addStudent(student : StudentExitAttendance){
        viewModelScope.launch(Dispatchers.IO) {
            repository.addStudent(student)
        }
    }

    fun deleteAttendanceTable(){
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteAttendanceTable()
        }
    }

    fun updateStudent(student : StudentExitAttendance){
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateStudent(student)
        }
    }

    fun getStudentById(id: Int) : StudentExitAttendance {
        return repository.getStudentById(id)
    }

    fun isRowExists(id : Int) : Boolean{
        return repository.isRowExists(id)
    }
}