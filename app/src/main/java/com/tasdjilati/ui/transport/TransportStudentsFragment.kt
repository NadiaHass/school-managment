package com.tasdjilati.ui.transport

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.tasdjilati.R
import com.tasdjilati.data.entities.StudentTransport
import com.tasdjilati.databinding.FragmentTransportStudentsBinding
import com.tasdjilati.ui.main.students_list.StudentViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.apache.poi.ss.usermodel.Sheet
import org.apache.poi.ss.usermodel.Workbook
import org.apache.poi.ss.usermodel.WorkbookFactory


class TransportStudentsFragment : Fragment() {
    private var studentsList: List<StudentTransport>? = ArrayList()
    private var _binding: FragmentTransportStudentsBinding? = null
    private val binding get() = _binding!!
    private lateinit var studentTransportViewModel: StudentTransportViewModel
    private lateinit var studentViewModel: StudentViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentTransportStudentsBinding.inflate(inflater, container, false)
        studentTransportViewModel = ViewModelProvider(this)[StudentTransportViewModel::class.java]
        studentViewModel = ViewModelProvider(this)[StudentViewModel::class.java]

        binding.rvStudentsTransport.layoutManager = LinearLayoutManager(requireContext())

        studentTransportViewModel.getAllStudents.observe(viewLifecycleOwner , {
            binding.rvStudentsTransport.adapter = StudentTransportAdapter(it , studentTransportViewModel , activity!!)
            studentsList = it

            if(it.isEmpty()){
                binding.ivEmpty.visibility = View.VISIBLE
            }else{
                binding.ivEmpty.visibility = View.GONE
            }
        })

        binding.fabImportExcel.setOnClickListener{
            openFileChooser()
        }

        binding.fabAddStudent.setOnClickListener {
            Navigation.findNavController(binding.root).navigate(R.id.action_transportStudentsFragment_to_addTransportStudentFragment)
        }

        binding.fabAddStudentByQr.setOnClickListener {
            Navigation.findNavController(binding.root).navigate(R.id.action_transportStudentsFragment_to_addScanTransportStudentFragment)
        }

        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

            override fun onQueryTextChange(query: String?): Boolean {
                if (query != null){
                    searchDatabase(query)
                }
                return true
            }

            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null){
                    searchDatabase(query)
                }
                return true
            }

        })

        binding.ivDeleteAll.setOnClickListener {
            try{
                val builder: AlertDialog.Builder = context.let {
                    AlertDialog.Builder(it)
                }

                builder.setMessage("Voulez vous vraiment supprimer tous les eleve ?")
                    .setTitle("Supprimer tous les eleve")

                builder.apply {
                    setPositiveButton("Oui") { dialog, id ->
                        studentTransportViewModel.deleteAllStudents()
                    }
                    setNegativeButton("Non") { dialog, id ->
                        dialog.dismiss()
                    }
                }
                val dialog: AlertDialog? = builder.create()

                dialog!!.show()
            }catch (e : Exception){

            }
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        showBottomNav()
    }

    private fun openFileChooser() {
        val intent = Intent()
        intent.type = "*/*"
        intent.action = Intent.ACTION_GET_CONTENT
        resultLauncher.launch(intent)
    }

    private var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result ->
        if (result.resultCode == Activity.RESULT_OK ){
            val data : Intent? = result.data
            if (data?.data != null) {
                try{
                    val uriFile = data.data

                    val sheet = selectSheet(uriFile)
                    getAllStudentsFromSheet(sheet!!)

                }catch (e : Exception){
                    Toast.makeText(requireContext() , "Il faut choisir un fichier excel", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun retrieveWorkbook(uriFile: Uri?): Workbook? {
        try {
            val workbookStream = activity?.contentResolver?.openInputStream(uriFile!!)
            return WorkbookFactory.create(workbookStream)
        } catch (e: Exception) {
            Toast.makeText(requireContext() , e.message , Toast.LENGTH_LONG).show()
        }
        return null
    }

    private fun selectSheet(uriFile: Uri?): Sheet? {
        try{
            retrieveWorkbook(uriFile)?.let { workbook ->
                if (workbook.numberOfSheets > 0) {
                    return workbook.getSheetAt(0)
                }
            }
        }catch (e : Exception){
            Toast.makeText(requireContext() , e.message , Toast.LENGTH_LONG).show()
        }


        return null
    }

    private fun getAllStudentsFromSheet(sheet: Sheet)=
        CoroutineScope(Dispatchers.IO).launch {
        try {
            val totalRows = sheet.physicalNumberOfRows
            for (i in 0 until totalRows){
                var userId = -1
                do {
                    userId = (100000..102000).random()

                }while ((studentTransportViewModel.isRowExists(id) || studentViewModel.isRowExists(id)))
                try{
                    val student = StudentTransport(userId, sheet.getRow(i).getCell(0).stringCellValue,
                        sheet.getRow(i).getCell(1).stringCellValue ,  sheet.getRow(i).getCell(2).stringCellValue ,
                        sheet.getRow(i).getCell(3).stringCellValue.toString() ,  sheet.getRow(i).getCell(4).stringCellValue.toString() ,
                        sheet.getRow(i).getCell(5).stringCellValue.toString() ,  sheet.getRow(i).getCell(6).stringCellValue.toString()  ,
                        sheet.getRow(i).getCell(7).stringCellValue , sheet.getRow(i).getCell(8).stringCellValue.toString())
                    studentTransportViewModel.addStudent(student)

                }catch(e:Exception){
                    withContext(Dispatchers.Main){
                        Toast.makeText(requireContext() , e.message , Toast.LENGTH_LONG).show()
                    }
                }
            }

        } catch (e: Exception) {
            withContext(Dispatchers.Main){
                Toast.makeText(context, e.message, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun searchDatabase(query : String){
        val searchQuery = "%$query%"
        studentTransportViewModel.searchDatabase(searchQuery).observe(viewLifecycleOwner , {
            binding.rvStudentsTransport.adapter = StudentTransportAdapter(it, studentTransportViewModel , activity!!)
        })
    }

    private fun showBottomNav() {
        try{
            val bottomNav = activity?.findViewById<BottomNavigationView>(R.id.transportBottomNavigationView)
            bottomNav!!.visibility = View.VISIBLE
        }catch (e : Exception){
            Toast.makeText(activity , e.message , Toast.LENGTH_LONG).show()
        }
    }

}