package com.tasdjilati.ui.main.enter

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.tasdjilati.R
import com.tasdjilati.databinding.ActivityEnterAttendanceBinding

class EnterAttendanceActivity : AppCompatActivity() {
    private lateinit var binding : ActivityEnterAttendanceBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEnterAttendanceBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHost = findNavController(R.id.enterFragmentContainerView)
        binding.bottomNavigationView.setupWithNavController(navHost)

    }
}