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
import com.tasdjilati.R

class MainActivity : AppCompatActivity() {
    private val REQUEST_CODE: Int = 1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

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
    }
}