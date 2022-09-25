package com.tasdjilati.ui.qrLists

import android.content.Context.MODE_PRIVATE
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.tasdjilati.data.entities.Student
import com.tasdjilati.databinding.FragmentQrCodeListsBinding
import com.tasdjilati.ui.main.students_list.StudentViewModel
import org.apache.commons.collections4.ListUtils
import java.io.File
import java.io.FileOutputStream
import java.io.FileWriter
import java.io.OutputStreamWriter
import java.nio.charset.Charset


class QrCodeListsFragment : Fragment() {
    private var studentsList: List<Student>? = ArrayList()
    private var _binding: FragmentQrCodeListsBinding ?= null
    private val binding get() = _binding!!
    private lateinit var studentViewModel: StudentViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentQrCodeListsBinding.inflate(inflater, container, false)
        studentViewModel = ViewModelProvider(this)[StudentViewModel::class.java]

        studentViewModel.getAllStudents.observe(viewLifecycleOwner , {
            studentsList = it

            val idsList = ArrayList<String>()
            for (student in studentsList!!){
                idsList.add(student.id.toString())
            }

            val lists = ListUtils.partition(idsList , 200)

            binding.rvLists.layoutManager = LinearLayoutManager(requireContext())
            binding.rvLists.adapter = QrListAdapter(lists , activity!!)
            binding.rvLists.visibility = View.VISIBLE

            binding.ivExportFile.setOnClickListener {
                val text : String = generaleStringText()
                generateIdsFile(text)
            }

        })

        return binding.root
    }

    private fun generateIdsFile(text: String) {
        try {
            val myFilePath =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).path
            val dir = File(myFilePath);
            dir.mkdirs();
            val file =  File("$myFilePath/idsFile.txt");

            try {
                val fos = FileOutputStream(file)
                val ow =  OutputStreamWriter(fos)
                ow.write(text)
                ow.append(text)
                ow.close()
                fos.close()
                Toast.makeText(requireContext(),
                    "Vous treverez le fichier dans vos telechargements",Toast.LENGTH_SHORT).show();
            } catch ( e : Exception) {
            Toast.makeText(requireContext(), e.message,Toast.LENGTH_SHORT).show();
        }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun generaleStringText(): String {
        val result = StringBuilder()
        for(student in studentsList!!){
            result.append(student.id.toString() + "\n")
        }
        return result.toString()
    }

}