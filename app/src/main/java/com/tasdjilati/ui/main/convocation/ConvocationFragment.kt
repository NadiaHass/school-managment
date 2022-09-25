package com.tasdjilati.ui.main.convocation

import android.Manifest
import android.app.Activity
import android.app.PendingIntent
import android.content.*
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.telephony.SmsManager
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.budiyev.android.codescanner.AutoFocusMode
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.DecodeCallback
import com.budiyev.android.codescanner.ErrorCallback
import com.budiyev.android.codescanner.ScanMode
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.tasdjilati.R
import com.tasdjilati.data.entities.Student
import com.tasdjilati.databinding.FragmentConvocationBinding
import com.tasdjilati.ui.main.students_list.StudentViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.lang.Exception

class ConvocationFragment : Fragment() {
    private lateinit var student: Student
    private var _binding: FragmentConvocationBinding? = null
    private val binding get() = _binding!!
    private lateinit var codeScanner: CodeScanner
    private val CAMERA_PERMISSION_REQUEST_CODE: Int = 3
    private val REQUEST_CODE: Int = 1
    private lateinit var studentViewModel: StudentViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentConvocationBinding.inflate(inflater, container, false)

        studentViewModel = ViewModelProvider(this)[StudentViewModel::class.java]

        binding.etMessage.setText("Votre enfant est convoqué")

        checkForPermission()

        startScanning()

        binding.scannerView.setOnClickListener {
            codeScanner.startPreview()
        }

        return binding.root
    }

    private fun hideBottomNav() {
        try{
            val bottomNav = activity?.findViewById<BottomNavigationView>(R.id.main_bottom_nav)
            bottomNav!!.visibility = View.GONE
        }catch (e : Exception){
            Toast.makeText(activity , e.message , Toast.LENGTH_LONG).show()
        }
    }

    private fun sendSMS(phoneNumber: String, message: String) = CoroutineScope(Dispatchers.IO).launch {
        if(checkSmsPermission() && phoneNumber.isNotEmpty()){
            val SENT = "SMS_SENT"
            val DELIVERED = "SMS_DELIVERED"
            val sentPI = PendingIntent.getBroadcast(activity!!, 0, Intent(
                SENT), 0)
            val deliveredPI = PendingIntent.getBroadcast(activity!!, 0,
                Intent(DELIVERED), 0)
            val sms = SmsManager.getDefault()
            sms.sendTextMessage(phoneNumber, null, message, sentPI, deliveredPI)


        }
    }

    private fun checkSmsPermission() : Boolean{
        return if (ContextCompat.checkSelfPermission(activity!!, Manifest.permission.SEND_SMS)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity!!,
                Array<String>(1){"Manifest.permission.SEND_SMS"},
                REQUEST_CODE)
            Toast.makeText(activity , "Activez la permission sms dans les parametres" , Toast.LENGTH_LONG).show()
            false
        }else {
            true
        }
    }


    private fun startScanning() {
        codeScanner = CodeScanner(activity!!, binding.scannerView)
        codeScanner.camera = CodeScanner.CAMERA_BACK
        codeScanner.formats = CodeScanner.ALL_FORMATS
        codeScanner.autoFocusMode = AutoFocusMode.SAFE
        codeScanner.scanMode = ScanMode.SINGLE
        codeScanner.isAutoFocusEnabled = true
        codeScanner.isFlashEnabled = false
        codeScanner.decodeCallback = DecodeCallback {
            if (studentViewModel.isRowExists(it.text.toInt())&& binding.etMessage.text.toString().isNotEmpty()){
                val student = studentViewModel.getStudentById(it.text.toInt())
                sendSMS(student.numParent1 , binding.etMessage.text.toString())
                sendSMS(student.numParent2 , binding.etMessage.text.toString())
            }else{
                activity?.runOnUiThread {
                    Toast.makeText(requireContext() , "Id n'existe pas" , Toast.LENGTH_LONG).show()
                }
            }
        }
        codeScanner.errorCallback = ErrorCallback {
            activity?.runOnUiThread {
                Toast.makeText(requireActivity(), "Camera initialization error: ${it.message}",
                    Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        codeScanner.startPreview()
        hideBottomNav()
    }

    override fun onPause() {
        codeScanner.releaseResources()
        super.onPause()
    }

    private fun checkForPermission() {
        if (ActivityCompat.checkSelfPermission(activity!!, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermission()
        }
    }


    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            activity!!, arrayOf(Manifest.permission.CAMERA),
            CAMERA_PERMISSION_REQUEST_CODE
        )
    }

}