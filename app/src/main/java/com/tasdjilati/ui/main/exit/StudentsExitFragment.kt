package com.tasdjilati.ui.main.exit

import android.Manifest
import android.app.AlertDialog
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
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.tasdjilati.data.entities.StudentEnterAttendance
import com.tasdjilati.data.entities.StudentExitAttendance
import com.tasdjilati.databinding.FragmentStudentsExitBinding
import com.tasdjilati.ui.main.students_list.StudentViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
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

            dialog!!.show()        }

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
                        sendSmsToAbsents()
                        deleteStudentAttendanceList()
                        insertStudents()
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