package com.tasdjilati.ui.main.students_list

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Point
import android.graphics.pdf.PdfDocument
import android.os.Bundle
import android.os.Environment
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Toast
import androidmads.library.qrgenearator.QRGContents
import androidmads.library.qrgenearator.QRGEncoder
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.tasdjilati.R
import com.tasdjilati.data.entities.Student
import com.tasdjilati.databinding.FragmentStudentsListBinding
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
            binding.rvStudents.adapter = StudentAdapter(it)
            studentsList = it

            })

        binding.fabAddStudent.setOnClickListener {
            generateQrCodesPdf(studentsList)
            Navigation.findNavController(binding.root).navigate(R.id.action_studentsListFragment_to_addNewStudentFragment)
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

    override fun onResume() {
        super.onResume()
        hideBottomNav()
    }
    private fun generateQrCodesPdf(studentsList: List<Student>?) {
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
        // save pdf file in Mobile Phone Storage
        val myFilePath = Environment.getExternalStorageDirectory().path + "/listeQrCode.pdf"
        val myFile = File(myFilePath)
        try {
            pdfDocument.writeTo(FileOutputStream(myFile))
            Toast.makeText(requireContext(),
                "PDF File saved in mobile Location",
                Toast.LENGTH_SHORT).show()
        } catch (e: java.lang.Exception) {
            Toast.makeText(requireContext() , e.message , Toast.LENGTH_LONG).show()
        }

        pdfDocument.close()
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