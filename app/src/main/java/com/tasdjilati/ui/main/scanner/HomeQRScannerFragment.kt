package com.tasdjilati.ui.main.scanner

import android.Manifest
import android.app.Dialog
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.budiyev.android.codescanner.*
import com.tasdjilati.R
import com.tasdjilati.data.entities.Student
import com.tasdjilati.databinding.FragmentHomeQRScannerBinding
import com.tasdjilati.ui.main.students_list.StudentViewModel

class HomeQRScannerFragment : Fragment() {
    private var _binding: FragmentHomeQRScannerBinding? = null
    private val binding get() = _binding!!
    private lateinit var codeScanner: CodeScanner
    private val CAMERA_PERMISSION_REQUEST_CODE: Int = 3
    private lateinit var studentViewModel: StudentViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View{
        _binding = FragmentHomeQRScannerBinding.inflate(inflater, container, false)

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
            activity?.runOnUiThread {
                Toast.makeText(requireActivity(), "Id d'eleve: ${it.text}", Toast.LENGTH_LONG).show()
            }
            showStudentInfo(it.text.toInt())
        }
        codeScanner.errorCallback = ErrorCallback { // or ErrorCallback.SUPPRESS
            activity?.runOnUiThread {
                Toast.makeText(requireActivity(), "Camera initialization error: ${it.message}",
                    Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun showStudentInfo(id: Int) {
        try{
            if(studentViewModel.isRowExists(id)){
                val student = studentViewModel.getStudentById(id)
                activity?.runOnUiThread {
                    showDialogInfo(student)
                }
            }else{
                Toast.makeText(requireContext() , "Id n'existe pas" , Toast.LENGTH_LONG).show()
            }

        }catch (e : Exception){

        }
    }

    private fun showDialogInfo(student: Student) {
        try{
            val dialog = Dialog(requireContext())
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setCancelable(false)
            dialog.setContentView(R.layout.student_info_dialog)
            val name = dialog.findViewById<TextView>(R.id.tv_name)
            val surname = dialog.findViewById<TextView>(R.id.tv_surname)
            val address = dialog.findViewById<TextView>(R.id.tv_address)
            val birthDate = dialog.findViewById<TextView>(R.id.tv_birthDate)
            val classe = dialog.findViewById<TextView>(R.id.tv_classe)
            val year = dialog.findViewById<TextView>(R.id.tv_year)
            val numParent1 = dialog.findViewById<TextView>(R.id.tv_numParent1)
            val numParent2 = dialog.findViewById<TextView>(R.id.tv_numParent2)
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