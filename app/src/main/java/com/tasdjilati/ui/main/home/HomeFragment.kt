package com.tasdjilati.ui.main.home

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import com.tasdjilati.R
import com.tasdjilati.databinding.FragmentHomeBinding
import com.tasdjilati.ui.settings.SettingsActivity

class HomeFragment : Fragment() {
    private var _binding : FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View{
        _binding = FragmentHomeBinding.inflate(inflater , container , false)

        binding.btnStart.setOnClickListener {
            openStudentsListFragment()
        }

        binding.ivSettings.setOnClickListener {
            openSettingsActivity()
        }

        binding.cardStudentsEnter.setOnClickListener {
            Navigation.findNavController(binding.root).navigate(R.id.action_homeFragment_to_studentsEnterFragment)
        }

        return binding.root
    }

    private fun openStudentsListFragment() {
        Navigation.findNavController(binding.root).navigate(R.id.action_homeFragment_to_studentsListFragment)
    }

    private fun openSettingsActivity() {
        startActivity(Intent(activity , SettingsActivity::class.java))
    }
}