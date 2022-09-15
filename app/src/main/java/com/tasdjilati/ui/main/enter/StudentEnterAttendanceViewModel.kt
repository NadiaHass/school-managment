package com.tasdjilati.ui.main.enter

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.tasdjilati.data.StudentsDatabase
import com.tasdjilati.data.entities.StudentEnterAttendance
import com.tasdjilati.repositories.StudentEnterAttendanceRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class StudentEnterAttendanceViewModel (application: Application) : AndroidViewModel(application) {
    val getAllStudents : LiveData<List<StudentEnterAttendance>>
    private val repository : StudentEnterAttendanceRepository

    init {
        val studentEnterAttendanceDao = StudentsDatabase.getDatabase(application).getStudentEnterAttendanceDao()
        repository = StudentEnterAttendanceRepository(studentEnterAttendanceDao)
        getAllStudents = repository.getAllStudents
    }

    fun addStudent(student : StudentEnterAttendance){
        viewModelScope.launch(Dispatchers.IO) {
            repository.addStudent(student)
        }
    }

    fun deleteAttendanceTable(){
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteAttendanceTable()
        }
    }

    fun updateStudentAttendance(attendance : Int, id : Int){
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateStudentAttendance(attendance , id)
        }
    }

    fun getStudentById(id: Int) : StudentEnterAttendance{
       return repository.getStudentById(id)
    }

    fun isRowExists(id : Int) : Boolean{
        return repository.isRowExists(id)
    }
}