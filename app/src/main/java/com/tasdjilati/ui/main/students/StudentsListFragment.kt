package com.tasdjilati.ui.main.students

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.tasdjilati.R
import com.tasdjilati.data.entities.Student
import com.tasdjilati.databinding.FragmentStudentsListBinding

class StudentsListFragment : Fragment() {
    private var _binding: FragmentStudentsListBinding? = null
    private val binding get() = _binding!!
    private lateinit var studentViewModel: StudentViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentStudentsListBinding.inflate(inflater, container, false)
        studentViewModel = ViewModelProvider(this)[StudentViewModel::class.java]

        binding.rvStudents.layoutManager = LinearLayoutManager(requireContext())

        studentViewModel.getAllStudents.observe(viewLifecycleOwner , {
                binding.rvStudents.adapter = StudentAdapter(it)
            })

        binding.fabAddStudent.setOnClickListener {
            Navigation.findNavController(binding.root).navigate(R.id.action_studentsListFragment_to_addNewStudentFragment)
        }

        return binding.root
    }
}