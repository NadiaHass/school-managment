package com.tasdjilati.ui.main.students_list

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.tasdjilati.data.entities.Student
import com.tasdjilati.databinding.FragmentAddNewStudentBinding
import java.util.*

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
            if (checkInput()){
                val student = Student(0 , binding.etName.text.toString() , binding.etSurname.text.toString() ,
                    binding.etBirthDate.text.toString() , binding.etYear.text.toString() , binding.etClasse.text.toString() ,
                    binding.etNumParent1.text.toString() , binding.etNumParent2.text.toString() , binding.etAddress.text.toString())
                insertStudentToDatabase(student)
                emptyInput()
                Toast.makeText(requireContext() , "L'eleve est bien ajoutee" , Toast.LENGTH_SHORT).show()
            }
        }


        return binding.root
    }

    private fun insertStudentToDatabase(student: Student) {
        studentViewModel.addStudent(student)
    }

    private fun checkInput() : Boolean{
        return  binding.etName.text.toString().isNotEmpty() && binding.etSurname.text.toString().isNotEmpty() &&
        binding.etBirthDate.text.toString().isNotEmpty() && binding.etYear.text.toString().isNotEmpty() && binding.etClasse.text.toString().isNotEmpty() &&
        binding.etNumParent1.text.toString().isNotEmpty() && binding.etNumParent2.text.toString().isNotEmpty() && binding.etAddress.text.toString().isNotEmpty()
    }

    private fun emptyInput(){
        binding.etName.setText("")
        binding.etSurname.setText("")
        binding.etBirthDate.setText("")
        binding.etYear.setText("")
        binding.etClasse.setText("")
        binding.etNumParent1.setText("")
        binding.etNumParent2.setText("")
        binding.etAddress.setText("")
    }
}