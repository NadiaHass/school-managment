package com.tasdjilati.ui.main.exit

import android.Manifest
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.telephony.SmsManager
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.tasdjilati.data.entities.StudentExitAttendance
import com.tasdjilati.databinding.FragmentStudentsExitBinding
import com.tasdjilati.ui.main.students_list.StudentViewModel
import java.lang.Exception

class StudentsExitFragment : Fragment() {
    private val REQUEST_CODE: Int = 1
    private var studentsList: List<StudentExitAttendance>? = ArrayList()
    private var _binding: FragmentStudentsExitBinding? = null
    private val binding get() = _binding!!
    private lateinit var studentViewModel: StudentViewModel
    private lateinit var studentExitViewModel: StudentExitAttendanceViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentStudentsExitBinding.inflate(inflater, container, false)
        studentViewModel = ViewModelProvider(this)[StudentViewModel::class.java]
        studentExitViewModel = ViewModelProvider(this)[StudentExitAttendanceViewModel::class.java]

        val sp = activity?.getSharedPreferences("sp" , Context.MODE_PRIVATE)
        val started = sp?.getString("attendance_exit" , "end")

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
                sendSMS(student)
        }
    }

    private fun sendSMS(student: StudentExitAttendance) {
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
        studentExitViewModel.deleteAttendanceTable()

        val sp = activity?.getSharedPreferences("sp" , Context.MODE_PRIVATE)
        val editor = sp?.edit()
        editor?.putString("attendance_exit" , "end")
        editor?.apply()

    }

    private fun insertStudents() {
        studentViewModel.getAllStudents.observe(viewLifecycleOwner , { studentsList ->
            for (student in studentsList){
                val studentAttendance = StudentExitAttendance(student.id , student.name , student.surname , student.birthDate ,
                    student.year , student.classe , student.numParent1 , student.numParent2 , student.address , 0)
                studentExitViewModel.addStudent(studentAttendance)
            }
        })

        val sp = activity?.getSharedPreferences("sp" , Context.MODE_PRIVATE)
        val editor = sp?.edit()
        editor?.putString("attendance" , "start")
        editor?.apply()
    }

    private fun getAllStudentsAttendanceList() {
        studentExitViewModel.getAllStudents.observe(viewLifecycleOwner , {
            studentsList = it
            binding.rvStudents.layoutManager = LinearLayoutManager(requireContext())
            binding.rvStudents.adapter = StudentExitAttendanceAdapter(it)
        })
    }
}