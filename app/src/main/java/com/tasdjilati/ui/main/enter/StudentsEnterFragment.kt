package com.tasdjilati.ui.main.enter

import android.Manifest
import android.app.PendingIntent
import android.content.Context
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
import androidx.recyclerview.widget.LinearLayoutManager
import com.tasdjilati.data.entities.StudentEnterAttendance
import com.tasdjilati.databinding.FragmentStudentsEnterBinding
import com.tasdjilati.ui.main.students_list.StudentViewModel
import java.lang.Exception

class StudentsEnterFragment : Fragment() {
    private val REQUEST_CODE: Int = 1
    private var studentsList: List<StudentEnterAttendance>? = ArrayList()
    private var _binding: FragmentStudentsEnterBinding? = null
    private val binding get() = _binding!!
    private lateinit var studentViewModel: StudentViewModel
    private lateinit var studentEnterViewModel: StudentEnterAttendanceViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentStudentsEnterBinding.inflate(inflater, container, false)
        studentViewModel = ViewModelProvider(this)[StudentViewModel::class.java]
        studentEnterViewModel = ViewModelProvider(this)[StudentEnterAttendanceViewModel::class.java]

        val sp = activity?.getSharedPreferences("sp" , Context.MODE_PRIVATE)
        val started = sp?.getString("attendance" , "end")

        if (started == "end")
           insertStudents()


        binding.btnCancel.setOnClickListener {
            deleteStudentAttendanceList()
        }

        binding.btnTerminate.setOnClickListener {
            sendSmsToAbsents()
            deleteStudentAttendanceList()
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        getAllStudentsAttendanceList()

    }

    private fun sendSmsToAbsents() {
        for (student in studentsList!!){
            if (student.attendance == 1)
                sendSMS(student.id)
        }
    }

    private fun sendSMS(id: Int) {
        if (checkSmsPermission()){
                try {
                    val sentPI: PendingIntent = PendingIntent.getBroadcast(requireContext(), 0, Intent("SMS_SENT"), 0)
                    SmsManager.getDefault().sendTextMessage("number1", null, "message Parent 1", sentPI, null)
                    SmsManager.getDefault().sendTextMessage("number2", null, "message Parent 2", sentPI, null)
                }catch (e : Exception){

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

    private fun deleteStudentAttendanceList() {
        studentEnterViewModel.deleteAttendanceTable()

        val sp = activity?.getSharedPreferences("sp" , Context.MODE_PRIVATE)
        val editor = sp?.edit()
        editor?.putString("attendance" , "end")
        editor?.apply()

    }

    private fun insertStudents() {
        studentViewModel.getAllStudents.observe(viewLifecycleOwner , { studentsList ->
            for (student in studentsList){
                val studentAttendance = StudentEnterAttendance(student.id , student.name , student.surname , student.birthDate ,
                    student.year , student.classe , student.numParent1 , student.numParent2 , student.address , 0)
                studentEnterViewModel.addStudent(studentAttendance)
            }
        })

        val sp = activity?.getSharedPreferences("sp" , Context.MODE_PRIVATE)
        val editor = sp?.edit()
        editor?.putString("attendance" , "start")
        editor?.apply()
    }

    private fun getAllStudentsAttendanceList() {
        studentEnterViewModel.getAllStudents.observe(viewLifecycleOwner , {
            studentsList = it
            binding.rvStudents.layoutManager = LinearLayoutManager(requireContext())
            binding.rvStudents.adapter = StudentEnterAttendanceAdapter(it)
        })
    }
}