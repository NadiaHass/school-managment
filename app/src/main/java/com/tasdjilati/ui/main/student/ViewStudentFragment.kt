 package com.tasdjilati.ui.main.student

import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.content.Context.WINDOW_SERVICE
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Point
import android.graphics.pdf.PdfDocument
import android.graphics.pdf.PdfDocument.PageInfo
import android.net.Uri
import android.os.Build
import android.os.Build.VERSION.SDK_INT
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.view.*
import android.widget.Toast
import androidmads.library.qrgenearator.QRGContents
import androidmads.library.qrgenearator.QRGEncoder
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.tasdjilati.R
import com.tasdjilati.data.entities.Student
import com.tasdjilati.databinding.FragmentViewStudentBinding
import java.io.File
import java.io.FileOutputStream


 class ViewStudentFragment : Fragment() {
     private val REQUEST_CODE: Int = 1
     private val PERMISSION_REQUEST_CODE: Int = 1
     private var _binding: FragmentViewStudentBinding? = null
     private val binding get() = _binding!!
     private lateinit var bitmap: Bitmap
     private lateinit var qrEncoder: QRGEncoder

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentViewStudentBinding.inflate(inflater, container, false)

        val student = arguments?.getSerializable("student") as Student

        displayData(student)

        generateQrCode(student.id.toString())

        binding.btnCall.setOnClickListener {
            val intent1 = Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + student.numParent1))
            startActivity(intent1)
        }

        binding.btnMessage.setOnClickListener {
            val smsIntent = Intent(Intent.ACTION_SENDTO)
            smsIntent.addCategory(Intent.CATEGORY_DEFAULT)
            smsIntent.type = "vnd.android-dir/mms-sms"
            smsIntent.data = Uri.parse("sms:" + student.numParent1)
            startActivity(smsIntent)
        }

        binding.btnSave.setOnClickListener {
            val name = student.name.replace("\\s" , "") + "_" + student.surname.replace("\\s" , "")
            if (checkPermission())
            createMyPDFofQRCode(name)
            else
                requestPermission()
        }

        binding.ivUpdateStudent.setOnClickListener {
            val bundle = Bundle()
            bundle.putSerializable("student" , student)
            Navigation.findNavController(binding.root).navigate(R.id.action_viewStudentFragment_to_updateStudentFragment , bundle)
        }

        return binding.root
    }

     private fun createMyPDFofQRCode(name: String) {
                     val pdfDocument = PdfDocument()
                     val pi = PageInfo.Builder(bitmap.width, bitmap.height, 1).create()
                     val page = pdfDocument.startPage(pi)
                     val paint = Paint()
                     val x = 5F
                     val y = 5F
                     val canvas: Canvas = page.canvas
                     canvas.drawBitmap(bitmap, x, y, paint) // float left = x, float top = y
                     pdfDocument.finishPage(page)

                     // save pdf file in Mobile Phone Storage
                     val myFilePath =
                         Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS + "/$name.pdf").path
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

     private fun displayData(student: Student) {
         binding.tvId.text = "Id: " + student.id.toString()
         binding.tvName.text = "Nom: " + student.name
         binding.tvSurname.text = "Prénom: " + student.surname
         binding.tvBirthDate.text = "Date de naissance: " + student.birthDate
         binding.tvYear.text = "Année: " + student.year
         binding.tvClasse.text = "Classe: " + student.classe
         binding.tvNumParent1.text = "Numéro 1: " + student.numParent1
         binding.tvNumParent2.text = "Numéro 2: " + student.numParent2
         binding.tvAddress.text = "Addresse: " + student.address

     }

     private fun generateQrCode(id: String?) {
         val windowManager: WindowManager = activity?.getSystemService(WINDOW_SERVICE) as WindowManager
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
             binding.ivQrCode.setImageBitmap(bitmap)
         } catch (e: Exception) {
             e.printStackTrace()
         }
     }

     private fun requestPermission() {
         if (SDK_INT >= Build.VERSION_CODES.R) {
             try {
                 val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                 intent.addCategory("android.intent.category.DEFAULT")
                 intent.data = Uri.parse(String.format("package:%s",
                     activity?.applicationContext?.packageName))

                 startActivityForResult(intent, 2296)
             } catch (e: java.lang.Exception) {
                 val intent = Intent()
                 intent.action = Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION
                 startActivityForResult(intent, 2296)
             }
         } else {
             //below android 11
             ActivityCompat.requestPermissions(activity!!,
                 arrayOf(WRITE_EXTERNAL_STORAGE),
                 PERMISSION_REQUEST_CODE)
         }
     }

     private fun checkPermission(): Boolean {
         return if (SDK_INT >= Build.VERSION_CODES.R) {
             Environment.isExternalStorageManager()
         } else {
             val result1 =
                 ContextCompat.checkSelfPermission(activity!!, WRITE_EXTERNAL_STORAGE)
             return result1 == PackageManager.PERMISSION_GRANTED
         }
     }
 }