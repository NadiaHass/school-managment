package com.tasdjilati.ui.main.student

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.tasdjilati.R
import com.tasdjilati.data.entities.Student
import com.tasdjilati.databinding.FragmentAddNewStudentBinding
import com.tasdjilati.databinding.FragmentUpdateStudentBinding
import com.tasdjilati.ui.main.students_list.StudentViewModel


class UpdateStudentFragment : Fragment() {
    private var _binding: FragmentUpdateStudentBinding? = null
    private val binding get() = _binding!!
    private lateinit var studentViewModel: StudentViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentUpdateStudentBinding.inflate(inflater, container, false)
        studentViewModel = ViewModelProvider(this)[StudentViewModel::class.java]

        val student = arguments?.getSerializable("student") as Student

        binding.etName.setText(student.name)
        binding.etSurname.setText(student.surname)
        binding.etBirthDate.setText(student.birthDate)
        binding.etYear.setText(student.year)
        binding.etClasse.setText(student.classe)
        binding.etNumParent1.setText(student.numParent1)
        binding.etNumParent2.setText(student.numParent2)
        binding.etAddress.setText(student.address)

        binding.btnAddStudent.setOnClickListener {
            if(binding.etNumParent2.text.toString().isEmpty()){
                binding.etNumParent2.setText(" ")
            }
            if (checkInput()){
                val newStudent = Student( student.id , binding.etName.text.toString() , binding.etSurname.text.toString() ,
                    binding.etBirthDate.text.toString() , binding.etYear.text.toString() , binding.etClasse.text.toString() ,
                    binding.etNumParent1.text.toString() , binding.etNumParent2.text.toString() , binding.etAddress.text.toString())
                updateStudentInDatabase(newStudent)

                emptyInput()
                Toast.makeText(requireContext() , "L'eleve est bien modifie" , Toast.LENGTH_SHORT).show()
            }
        }


        return binding.root
    }

    private fun updateStudentInDatabase(student: Student) {
        studentViewModel.updateStudent(student)
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