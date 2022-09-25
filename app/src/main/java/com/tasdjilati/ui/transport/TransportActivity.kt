package com.tasdjilati.ui.transport

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.tasdjilati.R
import com.tasdjilati.databinding.ActivityEnterAttendanceBinding
import com.tasdjilati.databinding.ActivityTransportBinding

class TransportActivity : AppCompatActivity() {
    private lateinit var binding : ActivityTransportBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTransportBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHost = findNavController(R.id.transportFragmentContainerView)
        binding.transportBottomNavigationView.setupWithNavController(navHost)
    }
}