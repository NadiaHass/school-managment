package com.tasdjilati.ui.main.canteen

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
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.tasdjilati.R
import com.tasdjilati.data.entities.Student
import com.tasdjilati.databinding.FragmentCanteenMenuBinding
import com.tasdjilati.ui.main.students_list.StudentViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CanteenMenuFragment : Fragment() {
    private var _binding: FragmentCanteenMenuBinding? = null
    private val binding get() = _binding!!
    private val REQUEST_CODE: Int = 1
    private lateinit var studentViewModel: StudentViewModel
    private var studentsList: List<Student> = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentCanteenMenuBinding.inflate(inflater, container, false)


        studentViewModel = ViewModelProvider(this)[StudentViewModel::class.java]

        studentViewModel.getAllStudents.observe(viewLifecycleOwner , {
            studentsList = it
        })

        binding.btnSendMenu.setOnClickListener {
            val message = "Le menu de cette semaine est Dimanche : ${binding.etDay1.text} , " +
                    "Lundi: ${binding.etDay2.text} , Mardi: ${binding.etDay3.text} , " +
                    "Mercredi: ${binding.etDay4.text} , Jeudi: ${binding.etDay5.text} "

            sendMessages(message)
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


    private fun sendMessages(message: String) = lifecycleScope.launch {
        if (checkSmsPermission()){
            try{
                for (student in studentsList){
                    try{
                        sendSMS(student.numParent1 , message)
                        delay(50L)

                        sendSMS(student.numParent2 , message)
                        delay(50L)

                    }catch (e : Exception){

                    }
                }
                withContext(Dispatchers.Main){
                    Toast.makeText(requireContext() ,"Le processus est termine" , Toast.LENGTH_SHORT).show()

                }

            }catch (e : Exception){
                withContext(Dispatchers.Main){
                    Toast.makeText(requireContext() , e.message , Toast.LENGTH_SHORT).show()
                }
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
        val sms = SmsManager.getDefault()
        sms.sendTextMessage(phoneNumber, null, message, sentPI, deliveredPI)

    }

}