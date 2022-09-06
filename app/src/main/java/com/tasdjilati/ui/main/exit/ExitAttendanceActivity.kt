package com.tasdjilati.ui.main.exit

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.tasdjilati.R
import com.tasdjilati.databinding.ActivityExitAttendanceBinding

class ExitAttendanceActivity : AppCompatActivity() {
    private lateinit var binding : ActivityExitAttendanceBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityExitAttendanceBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHost = findNavController(R.id.exitFragmentContainerView)
        binding.bottomNavigationView.setupWithNavController(navHost)

    }
}