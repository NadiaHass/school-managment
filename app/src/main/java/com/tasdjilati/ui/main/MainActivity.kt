package com.tasdjilati.ui.main

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.tasdjilati.R
import com.tasdjilati.databinding.ActivityEnterAttendanceBinding
import com.tasdjilati.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMainBinding
    private val REQUEST_CODE: Int = 1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
            != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,"Manifest.permission.SEND_SMS")) {
                Toast.makeText(this, "Activez la permission sms" , Toast.LENGTH_LONG).show()

            } else {
                ActivityCompat.requestPermissions(this,
                    Array<String>(1){"Manifest.permission.SEND_SMS"},
                    REQUEST_CODE)
            }
        }

        else {
        }

        val navHost = findNavController(R.id.fragmentContainerView)
        binding.mainBottomNav.setupWithNavController(navHost)

    }
}