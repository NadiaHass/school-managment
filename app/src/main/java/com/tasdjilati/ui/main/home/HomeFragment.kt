package com.tasdjilati.ui.main.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.tasdjilati.R
import com.tasdjilati.databinding.FragmentHomeBinding
import com.tasdjilati.ui.main.enter.EnterAttendanceActivity
import com.tasdjilati.ui.main.exit.ExitAttendanceActivity

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
            openSettingsFragment()
        }

        binding.cardStudentsEnter.setOnClickListener {
            startActivity(Intent(activity , EnterAttendanceActivity::class.java))
        }

        binding.cardStudentsExit.setOnClickListener {
            startActivity(Intent(activity , ExitAttendanceActivity::class.java))
        }

       binding.cardStudentsConvocations.setOnClickListener {
            Navigation.findNavController(binding.root).navigate(R.id.action_homeFragment_to_convocationFragment)
        }

        binding.cardStudentsRestaurant.setOnClickListener {
            Navigation.findNavController(binding.root).navigate(R.id.action_homeFragment_to_canteenMenuFragment)
        }

        binding.cardStudentsEvents.setOnClickListener {
            Navigation.findNavController(binding.root).navigate(R.id.action_homeFragment_to_eventsFragment)
        }

        binding.cardStudentsSubscriptions.setOnClickListener {
            Navigation.findNavController(binding.root).navigate(R.id.action_homeFragment_to_subscriptionFragment)
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        showBottomNav()
    }

    private fun showBottomNav() {
        try{
            val bottomNav = activity?.findViewById<BottomNavigationView>(R.id.main_bottom_nav)
            bottomNav!!.visibility = View.VISIBLE
        }catch (e : Exception){
            Toast.makeText(activity , e.message , Toast.LENGTH_LONG).show()
        }
    }

    private fun openStudentsListFragment() {
        Navigation.findNavController(binding.root).navigate(R.id.action_homeFragment_to_studentsListFragment)
    }

    private fun openSettingsFragment() {
        Navigation.findNavController(binding.root).navigate(R.id.action_homeFragment_to_settingFragment)

    }
}