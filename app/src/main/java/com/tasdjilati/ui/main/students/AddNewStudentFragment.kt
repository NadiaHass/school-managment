package com.tasdjilati.ui.main.students

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.tasdjilati.data.entities.Student
import com.tasdjilati.databinding.FragmentAddNewStudentBinding

class AddNewStudentFragment : Fragment() {
    private var _binding: FragmentAddNewStudentBinding? = null
    private val binding get() = _binding!!
    private lateinit var studentViewModel: StudentViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentAddNewStudentBinding.inflate(inflater, container, false)
        studentViewModel = ViewModelProvider(this)[StudentViewModel::class.java]

        binding.btnAddStudent.setOnClickListener {
            val student = Student(0 , binding.etName.text.toString() , binding.etSurname.text.toString() ,
                binding.etBirthDate.text.toString() , binding.etYear.text.toString() , binding.etClasse.text.toString() ,
                binding.etNumParent1.text.toString() , binding.etNumParent2.text.toString() , binding.etAddress.text.toString())
            insertStudentToDatabase(student)
        }
        return binding.root
    }

    private fun insertStudentToDatabase(student: Student) {
        studentViewModel.addStudent(student)
    }
}