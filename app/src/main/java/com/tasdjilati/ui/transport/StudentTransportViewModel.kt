package com.tasdjilati.ui.transport

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.tasdjilati.data.StudentsDatabase
import com.tasdjilati.data.entities.StudentTransport
import com.tasdjilati.repositories.StudentTransportRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class StudentTransportViewModel(application: Application) : AndroidViewModel(application) {
    val getAllStudents : LiveData<List<StudentTransport>>
    private val repository : StudentTransportRepository

    init {
        val studentDao = StudentsDatabase.getDatabase(application).getStudentTransportDao()
        repository = StudentTransportRepository((studentDao))
        getAllStudents = repository.getAllStudents
    }

    fun addStudent(student : StudentTransport){
        viewModelScope.launch(Dispatchers.IO) {
            repository.addStudent(student)
        }
    }

    fun deleteStudent(student : StudentTransport){
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteStudent(student)
        }
    }

    fun updateStudent(student : StudentTransport){
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateStudent(student)
        }
    }

    fun deleteAllStudents(){
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteAllStudents()
        }
    }

    fun getStudentById(id: Int) : StudentTransport {
        return repository.getStudentById(id)
    }

    fun searchDatabase(searchQuery : String) : LiveData<List<StudentTransport>> {
        return repository.searchDatabase(searchQuery)
    }

    fun isRowExists(id : Int) : Boolean{
        return repository.isRowExists(id)
    }


}