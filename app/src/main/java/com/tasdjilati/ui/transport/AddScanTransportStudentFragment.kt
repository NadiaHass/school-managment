package com.tasdjilati.ui.transport

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.budiyev.android.codescanner.*
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.tasdjilati.R
import com.tasdjilati.data.entities.StudentTransport
import com.tasdjilati.databinding.FragmentAddScanTransportStudentBinding
import com.tasdjilati.ui.main.students_list.StudentViewModel
import kotlinx.coroutines.*

class AddScanTransportStudentFragment : Fragment() {
    private var _binding: FragmentAddScanTransportStudentBinding? = null
    private val binding get() = _binding!!
    private lateinit var codeScanner: CodeScanner
    private val CAMERA_PERMISSION_REQUEST_CODE: Int = 3
    private lateinit var studentViewModel: StudentViewModel
    private lateinit var studentTransportViewModel: StudentTransportViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View{
        _binding = FragmentAddScanTransportStudentBinding.inflate(inflater, container, false)

        studentTransportViewModel = ViewModelProvider(this)[StudentTransportViewModel::class.java]
        studentViewModel = ViewModelProvider(this)[StudentViewModel::class.java]

        checkForPermission()

        startScanning()

        binding.scannerView.setOnClickListener {
            codeScanner.startPreview()
        }

            return binding.root
        }

    private fun insertStudentToDb(studentId: Int , destination : String) = CoroutineScope(Dispatchers.IO).launch {
        try{
            val student = studentViewModel.getStudentById(studentId)
            val studentTransport = StudentTransport(student.id,
                student.name,
                student.surname,
                student.birthDate,
                student.year,
                student.classe,
                student.numParent1,
                student.numParent2,
                student.address,
                destination)

            studentTransportViewModel.addStudent(studentTransport)

        }catch (e : Exception){
            withContext(Dispatchers.Main) {
                binding.etDestination.setText("")
                Toast.makeText(requireContext(),
                    e.message,
                    Toast.LENGTH_LONG).show()
            }

        }
        withContext(Dispatchers.Main) {
            binding.etDestination.setText("")
            Toast.makeText(requireContext(),
                "eleve a ete ajoute",
                Toast.LENGTH_LONG).show()
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
            if (binding.etDestination.text.isNotEmpty()) {
                if (!studentTransportViewModel.isRowExists(it.text.toInt())) {
                    if (studentViewModel.isRowExists(it.text.toInt())) {
                        insertStudentToDb(it.text.toInt() , binding.etDestination.text.toString())
                    } else {
                        activity?.runOnUiThread{
                            Toast.makeText(requireContext(),
                                "id n'existe pas",
                                Toast.LENGTH_LONG).show()
                        }
                    }
                } else {
                    activity?.runOnUiThread{
                        Toast.makeText(requireContext(),
                            "eleve existe deja dans transport",
                            Toast.LENGTH_LONG).show()
                    }

                }
            } else {
                activity?.runOnUiThread{
                    Toast.makeText(requireContext(), "Ecrivez destination", Toast.LENGTH_LONG)
                        .show()
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

    private fun hideBottomNav() {
        try{
            val bottomNav = activity?.findViewById<BottomNavigationView>(R.id.transportBottomNavigationView)
            bottomNav!!.visibility = View.GONE
        }catch (e : Exception){
            Toast.makeText(activity , e.message , Toast.LENGTH_LONG).show()
        }
    }

}