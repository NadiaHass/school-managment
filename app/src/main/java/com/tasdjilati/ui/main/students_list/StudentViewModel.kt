package com.tasdjilati.ui.main.students_list

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.tasdjilati.data.StudentsDatabase
import com.tasdjilati.data.entities.Student
import com.tasdjilati.repositories.StudentRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class StudentViewModel(application: Application) : AndroidViewModel(application) {
     val getAllStudents : LiveData<List<Student>>
    private val repository : StudentRepository

    init {
        val studentDao = StudentsDatabase.getDatabase(application).getStudentDao()
        repository = StudentRepository((studentDao))
        getAllStudents = repository.getAllStudents
    }

    fun addStudent(student : Student){
        viewModelScope.launch(Dispatchers.IO) {
            repository.addStudent(student)
        }
    }

    fun getStudentById(id: Int) : Student {
        return repository.getStudentById(id)
    }
}