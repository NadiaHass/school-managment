package com.tasdjilati.ui.main.event

import android.Manifest
import android.app.Activity
import android.app.PendingIntent
import android.content.*
import android.content.pm.PackageManager
import android.net.Uri
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
import com.tasdjilati.R
import com.tasdjilati.data.entities.Student
import com.tasdjilati.databinding.FragmentSubscriptionBinding
import com.tasdjilati.ui.main.students_list.StudentViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


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

    private fun sendMessages() = lifecycleScope.launch {
        if (checkSmsPermission()){
            try{
                for (student in studentsList){
                    sendSMS(student.numParent1 , binding.etMessage.text.toString())
                    delay(200L)
                    sendSMS(student.numParent2 , binding.etMessage.text.toString())
                    delay(200L)
                }
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

    private fun sendSMS(phoneNumber : String , message : String) {
        val SENT = "SMS_SENT"
        val DELIVERED = "SMS_DELIVERED"
        val sentPI = PendingIntent.getBroadcast(activity!!, 0, Intent(
            SENT), 0)
        val deliveredPI = PendingIntent.getBroadcast(activity!!, 0,
            Intent(DELIVERED), 0)

        // ---when the SMS has been sent---
        activity?.registerReceiver(object : BroadcastReceiver() {
            override fun onReceive(arg0: Context?, arg1: Intent?) {
                when (resultCode) {
                    Activity.RESULT_OK -> {
                        val values = ContentValues()
                        values.put("address",
                            phoneNumber) // txtPhoneNo.getText().toString());
                        values.put("body", "Votre enfant est present")

                        activity?.contentResolver?.insert(
                            Uri.parse("content://sms/sent"), values)
                        Toast.makeText(requireContext(), "SMS sent",
                            Toast.LENGTH_SHORT).show()
                    }
                    SmsManager.RESULT_ERROR_GENERIC_FAILURE -> Toast.makeText(requireContext(),
                        "Generic failure",
                        Toast.LENGTH_SHORT).show()
                    SmsManager.RESULT_ERROR_NO_SERVICE -> Toast.makeText(requireContext(),
                        "No service",
                        Toast.LENGTH_SHORT).show()
                    SmsManager.RESULT_ERROR_NULL_PDU -> Toast.makeText(requireContext(), "Null PDU",
                        Toast.LENGTH_SHORT).show()
                    SmsManager.RESULT_ERROR_RADIO_OFF -> Toast.makeText(requireContext(),
                        "Radio off",
                        Toast.LENGTH_SHORT).show()
                }
            }
        }, IntentFilter(SENT))

        // ---when the SMS has been delivered---
        activity?.registerReceiver(object : BroadcastReceiver() {
            override fun onReceive(arg0: Context?, arg1: Intent?) {
                when (resultCode) {
                    Activity.RESULT_OK -> Toast.makeText(requireContext(), "SMS delivered",
                        Toast.LENGTH_SHORT).show()
                    Activity.RESULT_CANCELED -> Toast.makeText(requireContext(),
                        "SMS not delivered",
                        Toast.LENGTH_SHORT).show()
                }
            }
        }, IntentFilter(DELIVERED))
        val sms = SmsManager.getDefault()
        sms.sendTextMessage(phoneNumber, null, message, sentPI, deliveredPI)

    }
}