package com.tasdjilati.ui.main.students_list

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.*
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.*
import android.widget.SearchView
import androidx.fragment.app.Fragment
import android.widget.Toast
import androidmads.library.qrgenearator.QRGContents
import androidmads.library.qrgenearator.QRGEncoder
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.tasdjilati.R
import com.tasdjilati.data.entities.Student
import com.tasdjilati.databinding.FragmentStudentsListBinding
import kotlinx.coroutines.*
import org.apache.poi.ss.usermodel.Sheet
import org.apache.poi.ss.usermodel.Workbook
import org.apache.poi.ss.usermodel.WorkbookFactory
import java.io.File
import java.io.FileOutputStream

class StudentsListFragment : Fragment() {
    private var studentsList: List<Student>? = ArrayList()
    private var _binding: FragmentStudentsListBinding? = null
    private val binding get() = _binding!!
    private lateinit var bitmap: Bitmap
    private lateinit var qrEncoder: QRGEncoder
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
            generateQrCodesPdf(studentsList)
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

    private fun getAllStudentsFromSheet(sheet: Sheet)= CoroutineScope(Dispatchers.IO).launch {
        try {
            val totalRows = sheet.physicalNumberOfRows
            val studentsArray = ArrayList<Student>()
            for (i in 0 until totalRows){
                studentsArray.add( Student(0 , sheet.getRow(i).getCell(0).stringCellValue,
                    sheet.getRow(i).getCell(1).stringCellValue ,  sheet.getRow(i).getCell(2).stringCellValue ,
                    sheet.getRow(i).getCell(3).stringCellValue.toString() ,  sheet.getRow(i).getCell(4).stringCellValue.toString() ,
                    sheet.getRow(i).getCell(5).stringCellValue.toString() ,  sheet.getRow(i).getCell(6).stringCellValue.toString()  ,
                    sheet.getRow(i).getCell(7).stringCellValue))
            }
            insertStudentsToDatabase(studentsArray)

        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Toast.makeText(context, e.message, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun insertStudentsToDatabase(studentsArray: ArrayList<Student>) {
        for (student in studentsArray){
            studentViewModel.addStudent(student)
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
    private fun generateQrCodesPdf(studentsList: List<Student>?) {
        runBlocking {
            try{
                val pdfDocument = PdfDocument()
                var i = 0
                for (student in studentsList!!){
                    i++
                    generateQrCode(student.id.toString())
                    val pi = PdfDocument.PageInfo.Builder(bitmap.width, bitmap.height, i).create()
                    val page = pdfDocument.startPage(pi)
                    val paint = Paint()
                    val x = 5F
                    val y = 5F
                    val canvas: Canvas = page.canvas
                    canvas.drawBitmap(bitmap, x, y, paint) // float left = x, float top = y
                    pdfDocument.finishPage(page)
                }

                val myFilePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS + "/listeQrCode.pdf").path
                val myFile = File(myFilePath)
                try {
                    pdfDocument.writeTo(FileOutputStream(myFile))
                    Toast.makeText(requireContext(),
                        "Fichier pdf est sauvegardee dans vos telechargements",
                        Toast.LENGTH_SHORT).show()
                } catch (e: java.lang.Exception) {
                    Toast.makeText(requireContext() , e.message , Toast.LENGTH_LONG).show()
                }

                pdfDocument.close()

            }catch (e : Exception){
                Toast.makeText(requireContext() , e.message , Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun generateQrCode(id: String?) {
        val windowManager: WindowManager = activity?.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display: Display = windowManager.defaultDisplay
        val point = Point()
        display.getSize(point)
        val width = point.x
        val height = point.y
        var dimen = if (width < height) width else height
        dimen = dimen * 3 / 4
        qrEncoder = QRGEncoder(id , null, QRGContents.Type.TEXT, dimen)
        try {
            bitmap = qrEncoder.encodeAsBitmap()

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}