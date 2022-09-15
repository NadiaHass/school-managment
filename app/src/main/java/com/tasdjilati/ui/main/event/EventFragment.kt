package com.tasdjilati.ui.main.event

import android.Manifest
import android.app.PendingIntent
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
import com.tasdjilati.R
import com.tasdjilati.data.entities.Student
import com.tasdjilati.databinding.FragmentSubscriptionBinding
import com.tasdjilati.ui.main.students_list.StudentViewModel


class EventFragment : Fragment() {
    private var studentsList: List<Student> = ArrayList()
    private var _binding: FragmentSubscriptionBinding? = null
    private val binding get() = _binding!!
    private val REQUEST_CODE: Int = 1
    private lateinit var studentViewModel: StudentViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentSubscriptionBinding.inflate(inflater, container, false)

        studentViewModel = ViewModelProvider(this)[StudentViewModel::class.java]

        studentViewModel.getAllStudents.observe(viewLifecycleOwner, {
            studentsList = it
        })

        val message = arguments?.getString("message" , "")

        binding.etMessage.setText(message)
        binding.btnSendMessage.setOnClickListener {
            sendMessages()
            Toast.makeText(requireActivity(), "Les sms on ete envoyee", Toast.LENGTH_LONG).show()
        }

        return binding.root
    }

    private fun sendMessages() {
        for (student in studentsList){
            sendSMS(student)
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

    private fun sendSMS(student: Student) {
        if (checkSmsPermission()){
            try {
                val sentPI: PendingIntent = PendingIntent.getBroadcast(requireContext(), 0, Intent("SMS_SENT"), 0)
                SmsManager.getDefault().sendTextMessage(student.numParent1, null, binding.etMessage.text.toString(), sentPI, null)
                SmsManager.getDefault().sendTextMessage(student.numParent2, null, binding.etMessage.text.toString(), sentPI, null)
            }catch (e : java.lang.Exception){

            }
        }
    }

}