package com.tasdjilati.ui.main.scanner

import android.Manifest
import android.app.Activity
import android.app.PendingIntent
import android.content.*
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.telephony.SmsManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.budiyev.android.codescanner.*
import com.tasdjilati.data.entities.Student
import com.tasdjilati.data.entities.StudentEnterAttendance
import com.tasdjilati.databinding.FragmentQRScannerEnterBinding
import com.tasdjilati.ui.main.enter.StudentEnterAttendanceViewModel
import com.tasdjilati.ui.main.students_list.StudentViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class QRScannerEnterFragment : Fragment() {
    private val REQUEST_CODE: Int = 1
    private val CAMERA_PERMISSION_REQUEST_CODE: Int = 3
    private lateinit var codeScanner: CodeScanner
    private lateinit var studentEnterViewModel: StudentEnterAttendanceViewModel
    private lateinit var studentViewModel: StudentViewModel
    private var _binding: FragmentQRScannerEnterBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentQRScannerEnterBinding.inflate(inflater, container, false)

        studentEnterViewModel = ViewModelProvider(this)[StudentEnterAttendanceViewModel::class.java]
        studentViewModel = ViewModelProvider(this)[StudentViewModel::class.java]

        checkForPermission()

        startScanning()

        binding.scannerView.setOnClickListener {
            codeScanner.startPreview()
        }

        return binding.root
    }
        private fun startScanning() {
            codeScanner = CodeScanner(activity!!, binding.scannerView)
            codeScanner.camera = CodeScanner.CAMERA_BACK // or CAMERA_FRONT or specific camera id
            codeScanner.formats = CodeScanner.ALL_FORMATS // list of type BarcodeFormat,
            codeScanner.autoFocusMode = AutoFocusMode.SAFE // or CONTINUOUS
            codeScanner.scanMode = ScanMode.SINGLE // or CONTINUOUS or PREVIEW
            codeScanner.isAutoFocusEnabled = true // Whether to enable auto focus or not
            codeScanner.isFlashEnabled = false // Whether to enable flash or not
            codeScanner.decodeCallback = DecodeCallback {
                if (studentEnterViewModel.isRowExists(it.text.toInt())){
                    val student = studentEnterViewModel.getStudentById(it.text.toInt())

                    if(student.attendance == 0){
                        updateStudentAttendance(student)
                    }else{
                        activity?.runOnUiThread {
                            Toast.makeText(requireActivity(), "Le message est deja envoye",Toast.LENGTH_LONG).show()
                        }
                    }
                }else{
                    activity?.runOnUiThread {
                        Toast.makeText(requireContext() , "Id n'existe pas" , Toast.LENGTH_LONG).show()
                    }
                }

            }
            codeScanner.errorCallback = ErrorCallback { // or ErrorCallback.SUPPRESS
                activity?.runOnUiThread {
                    Toast.makeText(requireActivity(), "Camera initialization error: ${it.message}",
                        Toast.LENGTH_LONG).show()
                }
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

        private fun updateStudentAttendance(student : StudentEnterAttendance) {
            try {
                    student.attendance = 1
                    studentEnterViewModel.updateStudent(student)
                    sendSMS(student.numParent1 , "Votre enfant ${student.name} ${student.surname} est present")
                    sendSMS(student.numParent2 , "Votre enfant ${student.name} ${student.surname} est present")

            }catch (e : Exception){

            }
        }

        override fun onResume() {
            super.onResume()
            codeScanner.startPreview()
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