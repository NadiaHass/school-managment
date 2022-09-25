package com.tasdjilati.ui.main.students_list

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
import com.tasdjilati.data.entities.Student
import com.tasdjilati.databinding.FragmentStudentsListBinding
import org.apache.poi.ss.usermodel.Sheet
import org.apache.poi.ss.usermodel.Workbook
import org.apache.poi.ss.usermodel.WorkbookFactory

class StudentsListFragment : Fragment() {
    private var studentsList: List<Student>? = ArrayList()
    private var _binding: FragmentStudentsListBinding? = null
    private val binding get() = _binding!!
    private lateinit var studentViewModel: StudentViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentStudentsListBinding.inflate(inflater, container, false)
        studentViewModel = ViewModelProvider(this)[StudentViewModel::class.java]

        binding.rvStudents.layoutManager = LinearLayoutManager(requireContext())

        studentViewModel.getAllStudents.observe(viewLifecycleOwner , {
            binding.rvStudents.adapter = StudentAdapter(it , studentViewModel , activity!!)
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
            Navigation.findNavController(binding.root).navigate(R.id.action_studentsListFragment_to_addNewStudentFragment)
        }

        binding.fabExportQrPdf.setOnClickListener {

            Navigation.findNavController(binding.root).navigate(R.id.action_studentsListFragment_to_qrCodeListsFragment)

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
                        studentViewModel.deleteAllStudents()
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

    private fun getAllStudentsFromSheet(sheet: Sheet) {
        try {
            val totalRows = sheet.physicalNumberOfRows
            for (i in 0 until totalRows){
                val student = Student(0 , sheet.getRow(i).getCell(0).stringCellValue,
                    sheet.getRow(i).getCell(1).stringCellValue ,  sheet.getRow(i).getCell(2).stringCellValue ,
                    sheet.getRow(i).getCell(3).stringCellValue.toString() ,  sheet.getRow(i).getCell(4).stringCellValue.toString() ,
                    sheet.getRow(i).getCell(5).stringCellValue.toString() ,  sheet.getRow(i).getCell(6).stringCellValue.toString()  ,
                    sheet.getRow(i).getCell(7).stringCellValue)

                studentViewModel.addStudent(student)

            }

        } catch (e: Exception) {
//            withContext(Dispatchers.Main) {
//                Toast.makeText(context, e.message, Toast.LENGTH_LONG).show()
//            }
        }
    }

    private fun searchDatabase(query : String){
        val searchQuery = "%$query%"
        studentViewModel.searchDatabase(searchQuery).observe(viewLifecycleOwner , {
            binding.rvStudents.adapter = StudentAdapter(it, studentViewModel , activity!!)
        })
    }

    private fun hideBottomNav() {
        try{
            val bottomNav = activity?.findViewById<BottomNavigationView>(R.id.main_bottom_nav)
            bottomNav!!.visibility = View.GONE
        }catch (e : Exception){
            Toast.makeText(activity , e.message , Toast.LENGTH_LONG).show()
        }
    }

    override fun onResume() {
        super.onResume()
        hideBottomNav()
    }

}