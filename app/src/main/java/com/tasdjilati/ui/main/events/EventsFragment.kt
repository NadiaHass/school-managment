package com.tasdjilati.ui.main.events

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.Navigation
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.tasdjilati.R
import com.tasdjilati.databinding.FragmentEventsBinding
import com.tasdjilati.databinding.FragmentSubscriptionBinding


class EventsFragment : Fragment() {
    private var _binding: FragmentEventsBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View{
        _binding = FragmentEventsBinding.inflate(inflater, container, false)

        val bundle = Bundle()

        binding.cardExams.setOnClickListener {
            bundle.putString("message" , "Nous vous informons que la période des examens débute le ")
            openEventFragment(bundle)
        }

        binding.cardTests.setOnClickListener {
            bundle.putString("message" , "Nous vous informons que la période des devoirs débute le")
            openEventFragment(bundle)
        }

        binding.cardHolidays.setOnClickListener {
            bundle.putString("message" , "Nous vous informons que la période des vacances débutent le ")
            openEventFragment(bundle)
        }

        binding.cardTrips.setOnClickListener {
            bundle.putString("message" , "Nous vous informons qu'il y a une excursion le")
            openEventFragment(bundle)
        }

        binding.cardRegistration.setOnClickListener {
            bundle.putString("message" , "Nous vous informons du début de la période des inscriptions")
            openEventFragment(bundle)
        }

        binding.cardOther.setOnClickListener {
            bundle.putString("message" , "Ecrivez votre message .....")
            openEventFragment(bundle)
        }

        return binding.root
    }

    private fun openEventFragment(bundle: Bundle) {
        Navigation.findNavController(binding.root).navigate(R.id.action_eventsFragment_to_eventFragment , bundle)
    }

    private fun hideBottomNav() {
        try{
            val bottomNav = activity?.findViewById<BottomNavigationView>(R.id.main_bottom_nav)
            bottomNav!!.visibility = View.GONE
        }catch (e : Exception){
            Toast.makeText(activity , e.message , Toast.LENGTH_LONG).show()
        }
    }

    override fun onResume() {
        super.onResume()
        hideBottomNav()
    }
}