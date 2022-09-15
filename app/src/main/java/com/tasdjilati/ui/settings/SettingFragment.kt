package com.tasdjilati.ui.settings

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.Navigation
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.tasdjilati.R
import com.tasdjilati.databinding.FragmentSettingBinding
import com.tasdjilati.databinding.FragmentSubscriptionBinding

class SettingFragment : Fragment() {
    private var _binding: FragmentSettingBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentSettingBinding.inflate(inflater, container, false)

        binding.updateInfo.setOnClickListener {
            Navigation.findNavController(binding.root).navigate(R.id.action_settingFragment_to_UpdateInfoFragment)
        }

        binding.updateLanguage.setOnClickListener {
            Navigation.findNavController(binding.root).navigate(R.id.action_settingFragment_to_changeLanguageFragment)
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        hideBottomNav()
    }
    private fun hideBottomNav() {
        try{
            val bottomNav = activity?.findViewById<BottomNavigationView>(R.id.main_bottom_nav)
            bottomNav!!.visibility = View.GONE
        }catch (e : Exception){
            Toast.makeText(activity , e.message , Toast.LENGTH_LONG).show()
        }
    }
}