package com.tasdjilati.ui.main.enter

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.PendingIntent
import android.content.*
import android.content.pm.PackageManager
import android.net.Uri
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
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.tasdjilati.data.entities.Student
import com.tasdjilati.data.entities.StudentEnterAttendance
import com.tasdjilati.databinding.FragmentStudentsEnterBinding
import com.tasdjilati.ui.main.exit.StudentExitAttendanceAdapter
import com.tasdjilati.ui.main.students_list.StudentViewModel
import kotlinx.coroutines.*

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
            val builder: AlertDialog.Builder = context.let {
                AlertDialog.Builder(it)
            }

            builder.setMessage("Voulez vous vraiment annuler le processus ?")
                .setTitle("Annuler")

            builder.apply {
                setPositiveButton("Oui") { dialog, id ->
                    try{
                        deleteStudentAttendanceList()
                        insertStudents()

                    }catch(e : Exception){

                    }
                }
                setNegativeButton("Non") { dialog, id ->
                    dialog.dismiss()
                }
            }
            val dialog: AlertDialog? = builder.create()

            dialog!!.show()
        }

        binding.btnTerminate.setOnClickListener {
            try{
                val builder: AlertDialog.Builder = context.let {
                    AlertDialog.Builder(it)
                }

                builder.setMessage("Voulez vous vraiment terminer le processus ?")
                    .setTitle("Terminer")

                builder.apply {
                    setPositiveButton("Oui") { dialog, id ->
                        try{
                            sendSmsToAbsents()
                            deleteStudentAttendanceList()
                            insertStudents()

                        }catch(e : Exception){

                        }
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
        getAllStudentsAttendanceList()

    }

    private fun sendSmsToAbsents()= lifecycleScope.launch{
        for (student in studentsList!!)  {
            if (student.attendance == 0){
                sendSMS(student.numParent1, "Votre enfant ${student.name} ${student.surname} est absent, veuillez justifier la cause")
                delay(200L)
                sendSMS(student.numParent2 , "Votre enfant ${student.name} ${student.surname} est absent, veuillez justifier la cause")
                delay(200L)
            }

        }
    }

    private fun sendSMS(phoneNumber: String, message: String) {
        if(checkSmsPermission() && phoneNumber.isNotEmpty()){
            val SENT = "SMS_SENT"
            val DELIVERED = "SMS_DELIVERED"
            val sentPI = PendingIntent.getBroadcast(activity!!, 0, Intent(
                SENT), 0)
            val deliveredPI = PendingIntent.getBroadcast(activity!!, 0,
                Intent(DELIVERED), 0)
            val sms = SmsManager.getDefault()
            sms.sendTextMessage(phoneNumber, null, message, sentPI, deliveredPI)


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
        var studentAttendanceList : ArrayList<StudentEnterAttendance> = java.util.ArrayList()
        studentViewModel.getAllStudents.observe(viewLifecycleOwner , {
            for (student in it!!){
                val studentAttendance = StudentEnterAttendance( student.id , student.name , student.surname , student.birthDate ,
                    student.year , student.classe , student.numParent1 , student.numParent2 , student.address , 0)
                studentAttendanceList.add(studentAttendance)
            }

            for (studentAttendance in studentAttendanceList){
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