package com.tasdjilati.ui.transport

import android.Manifest
import android.app.Dialog
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import com.budiyev.android.codescanner.AutoFocusMode
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.DecodeCallback
import com.budiyev.android.codescanner.ErrorCallback
import com.budiyev.android.codescanner.ScanMode
import com.tasdjilati.R
import com.tasdjilati.data.entities.Student
import com.tasdjilati.data.entities.StudentTransport
import com.tasdjilati.databinding.FragmentHomeQRScannerBinding
import com.tasdjilati.ui.main.students_list.StudentViewModel

class ScannerTransportFragment : Fragment() {

    private var _binding: FragmentHomeQRScannerBinding? = null
    private val binding get() = _binding!!
    private lateinit var codeScanner: CodeScanner
    private val CAMERA_PERMISSION_REQUEST_CODE: Int = 3
    private lateinit var studentTransportViewModel: StudentTransportViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View{
        _binding = FragmentHomeQRScannerBinding.inflate(inflater, container, false)

        studentTransportViewModel = ViewModelProvider(this)[StudentTransportViewModel::class.java]

        checkForPermission()

        startScanning()

        binding.scannerView.setOnClickListener {
            codeScanner.startPreview()
        }

        return binding.root
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
            showStudentInfo(it.text.toInt())
        }
        codeScanner.errorCallback = ErrorCallback {
            activity?.runOnUiThread {
                Toast.makeText(requireActivity(), "Camera initialization error: ${it.message}",
                    Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun showStudentInfo(id: Int) {
        try{
            if(studentTransportViewModel.isRowExists(id)){
                val student = studentTransportViewModel.getStudentById(id)
                activity?.runOnUiThread {
                    showDialogInfo(student)
                }
            }else{
                activity?.runOnUiThread {
                    Toast.makeText(requireContext() , "Id n'existe pas" , Toast.LENGTH_LONG).show()
                }
            }

        }catch (e : Exception){

        }
    }

    private fun showDialogInfo(student: StudentTransport) {
        try{
            val dialog = Dialog(requireContext())
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setCancelable(false)
            dialog.setContentView(R.layout.student_transport_info_dialog)
            val name = dialog.findViewById<TextView>(R.id.tv_name)
            val surname = dialog.findViewById<TextView>(R.id.tv_surname)
            val address = dialog.findViewById<TextView>(R.id.tv_address)
            val birthDate = dialog.findViewById<TextView>(R.id.tv_birthDate)
            val classe = dialog.findViewById<TextView>(R.id.tv_classe)
            val year = dialog.findViewById<TextView>(R.id.tv_year)
            val numParent1 = dialog.findViewById<TextView>(R.id.tv_numParent1)
            val numParent2 = dialog.findViewById<TextView>(R.id.tv_numParent2)
            val destination = dialog.findViewById<TextView>(R.id.tv_destination)

            val id = dialog.findViewById<TextView>(R.id.tv_id)
            val okButton = dialog.findViewById<Button>(R.id.btn_ok)

            id.text = "Id: " + student.id
            name.text = "Nom: " + student.name
            surname.text = "Prenom: " + student.surname
            address.text = "Addresse: " + student.address
            birthDate.text = "Date de naissance: " + student.birthDate
            classe.text = "Classe: " + student.classe
            year.text = "Annee: " + student.year
            numParent1.text = "Numero 1: " + student.numParent1
            numParent2.text = "Numero 2: " + student.numParent2
            destination.text = "Destination : " + student.destination

            okButton.setOnClickListener {
                dialog.dismiss()
            }

            dialog.show()

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