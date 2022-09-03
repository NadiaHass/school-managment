package com.tasdjilati.ui.main.enter

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.tasdjilati.data.entities.StudentEnterAttendance
import com.tasdjilati.databinding.FragmentStudentsEnterBinding
import com.tasdjilati.ui.main.students.StudentViewModel

class StudentsEnterFragment : Fragment() {
    private var _binding: FragmentStudentsEnterBinding? = null
    private val binding get() = _binding!!
    private lateinit var studentViewModel: StudentViewModel
    private lateinit var studentEnterViewModel: StudentEnterAttendanceViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentStudentsEnterBinding.inflate(inflater, container, false)
        studentViewModel = ViewModelProvider(this)[StudentViewModel::class.java]
        studentEnterViewModel = ViewModelProvider(this)[StudentEnterAttendanceViewModel::class.java]

        val sp = activity?.getSharedPreferences("sp" , Context.MODE_PRIVATE)
        val started = sp?.getString("attendance" , "")

        if (started == "end")
           insertStudents()

        getAllStudentsAttendanceList()

        binding.btnCancel.setOnClickListener {
            deleteStudentAttendanceList()
        }

        binding.btnTerminate.setOnClickListener {
            deleteStudentAttendanceList()
        }

        return binding.root
    }

    private fun deleteStudentAttendanceList() {
        studentEnterViewModel.deleteAttendanceTable()

        val sp = activity?.getSharedPreferences("sp" , Context.MODE_PRIVATE)
        val editor = sp?.edit()
        editor?.putString("attendance" , "end")
        editor?.apply()

    }

    private fun insertStudents() {
        studentViewModel.getAllStudents.observe(viewLifecycleOwner , { studentsList ->
            for (student in studentsList){
                val studentAttendance = StudentEnterAttendance(student.id , student.name , student.surname , student.birthDate ,
                    student.year , student.classe , student.numParent1 , student.numParent2 , student.address , 0)
                studentEnterViewModel.addStudent(studentAttendance)
            }
        })

        val sp = activity?.getSharedPreferences("sp" , Context.MODE_PRIVATE)
        val editor = sp?.edit()
        editor?.putString("attendance" , "start")
        editor?.apply()
    }

    private fun getAllStudentsAttendanceList() {
        studentEnterViewModel.getAllStudents.observe(viewLifecycleOwner , {
            binding.rvStudents.layoutManager = LinearLayoutManager(requireContext())
            binding.rvStudents.adapter = StudentAttendanceAdapter(it)
        })
    }
}