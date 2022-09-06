package com.tasdjilati.ui.main.scanner

import android.Manifest
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
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
import com.tasdjilati.databinding.FragmentQRScannerEnterBinding
import com.tasdjilati.ui.main.enter.StudentEnterAttendanceViewModel
import java.lang.Exception

class QRScannerEnterFragment : Fragment() {
    private val REQUEST_CODE: Int = 1
    private val CAMERA_PERMISSION_REQUEST_CODE: Int = 3
    private lateinit var codeScanner: CodeScanner
    private lateinit var studentEnterViewModel: StudentEnterAttendanceViewModel
    private var _binding: FragmentQRScannerEnterBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentQRScannerEnterBinding.inflate(inflater, container, false)
        studentEnterViewModel = ViewModelProvider(this)[StudentEnterAttendanceViewModel::class.java]

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
        codeScanner.isFlashEnabled = true // Whether to enable flash or not
        codeScanner.decodeCallback = DecodeCallback {
            activity?.runOnUiThread {
                Toast.makeText(requireActivity(), "Id d'eleve: ${it.text}",Toast.LENGTH_LONG).show()
                updateStudentAttendance(it.text)
            }
            sendSMS(it.text.toInt())
        }
        codeScanner.errorCallback = ErrorCallback { // or ErrorCallback.SUPPRESS
            activity?.runOnUiThread {
                Toast.makeText(requireActivity(), "Camera initialization error: ${it.message}",
                    Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun sendSMS(id: Int) {
        val student = studentEnterViewModel.getStudentById(id)
        if (checkSmsPermission()){
            if (student.attendance == 0){
                try {
                    val sentPI: PendingIntent = PendingIntent.getBroadcast(requireContext(), 0, Intent("SMS_SENT"), 0)
                    SmsManager.getDefault().sendTextMessage(student.numParent1, null, "message Parent 1", sentPI, null)
                    SmsManager.getDefault().sendTextMessage(student.numParent2, null, "message Parent 2", sentPI, null)
                }catch (e : Exception){

                }
            }
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

    private fun updateStudentAttendance(id: String) {
        studentEnterViewModel.updateStudentAttendance(1 , id.toInt())
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