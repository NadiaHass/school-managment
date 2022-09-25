package com.tasdjilati.ui.transport

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.tasdjilati.R
import com.tasdjilati.data.entities.Student
import com.tasdjilati.data.entities.StudentTransport
import com.tasdjilati.databinding.FragmentAddNewStudentBinding
import com.tasdjilati.databinding.FragmentAddTransportStudentBinding
import com.tasdjilati.ui.main.students_list.StudentViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AddTransportStudentFragment : Fragment() {
    private var _binding: FragmentAddTransportStudentBinding? = null
    private val binding get() = _binding!!
    private lateinit var studentTransportViewModel: StudentTransportViewModel
    private lateinit var studentViewModel: StudentViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentAddTransportStudentBinding.inflate(inflater, container, false)
        studentTransportViewModel = ViewModelProvider(this)[StudentTransportViewModel::class.java]
        studentViewModel = ViewModelProvider(this)[StudentViewModel::class.java]

        binding.btnAddStudent.setOnClickListener {
            if(binding.etNumParent2.text.toString().isEmpty()){
                binding.etNumParent2.setText(" ")
            }
            if (checkInput()){
                val student = StudentTransport(binding.etId.text.toString().toInt() , binding.etName.text.toString() , binding.etSurname.text.toString() ,
                    binding.etBirthDate.text.toString() , binding.etYear.text.toString() , binding.etClasse.text.toString() ,
                    binding.etNumParent1.text.toString() , binding.etNumParent2.text.toString() , binding.etAddress.text.toString() ,
                binding.etDestination.text.toString())
                insertStudentToDatabase(student)
            }
        }


        return binding.root
    }

    private fun insertStudentToDatabase(student: StudentTransport) =
        CoroutineScope(Dispatchers.IO).launch {
        try{
            if (!(studentTransportViewModel.isRowExists(student.id) || studentViewModel.isRowExists(student.id))){
                studentTransportViewModel.addStudent(student)
                withContext(Dispatchers.Main) {
                    emptyInput()
                    Toast.makeText(requireContext(), "L'eleve est bien ajoutee", Toast.LENGTH_SHORT)
                        .show()
                }
            }else{
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(),
                        "Changez id = ${student.id} , id existe deja",
                        Toast.LENGTH_SHORT).show()
                }
            }
        }catch (e : Exception){
        withContext(Dispatchers.Main) {
        Toast.makeText(requireContext() , e.message , Toast.LENGTH_SHORT).show()}
        }
}

    private fun checkInput() : Boolean{
        return  binding.etName.text.toString().isNotEmpty() && binding.etSurname.text.toString().isNotEmpty() &&
                binding.etBirthDate.text.toString().isNotEmpty() && binding.etYear.text.toString().isNotEmpty() && binding.etClasse.text.toString().isNotEmpty() &&
                binding.etNumParent1.text.toString().isNotEmpty() &&  binding.etAddress.text.toString().isNotEmpty() &&
                binding.etId.text.toString().isNotEmpty() && binding.etDestination.text.toString().isNotEmpty()

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
        binding.etId.setText("")
        binding.etDestination.setText("")

    }

    override fun onResume() {
        super.onResume()
        hideBottomNav()
    }
    private fun hideBottomNav() {
        try{
            val bottomNav = activity?.findViewById<BottomNavigationView>(R.id.transportBottomNavigationView)
            bottomNav!!.visibility = View.GONE
        }catch (e : Exception){
            Toast.makeText(activity , e.message , Toast.LENGTH_LONG).show()
        }
    }

}