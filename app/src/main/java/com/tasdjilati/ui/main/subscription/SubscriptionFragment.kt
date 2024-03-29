package com.tasdjilati.ui.main.subscription

import android.Manifest
import android.app.*
import android.content.*
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.telephony.SmsManager
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.DatePicker
import android.widget.TimePicker
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.tasdjilati.R
import com.tasdjilati.data.entities.Student
import com.tasdjilati.databinding.FragmentSubscriptionBinding
import com.tasdjilati.notifications.*
import com.tasdjilati.notifications.Notification
import com.tasdjilati.ui.main.students_list.StudentViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList

class SubscriptionFragment : Fragment() {
    private var studentsList: List<Student> = ArrayList()
    private var _binding: FragmentSubscriptionBinding? = null
    private val binding get() = _binding!!
    private val REQUEST_CODE: Int = 1
    private lateinit var studentViewModel: StudentViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View{
        _binding = FragmentSubscriptionBinding.inflate(inflater, container, false)

        studentViewModel = ViewModelProvider(this)[StudentViewModel::class.java]

        binding.etMessage.setText("Veillez régler la mensualité de l'établissement scolaire dans les brefs délais possible.")
        studentViewModel.getAllStudents.observe(viewLifecycleOwner , {
            studentsList = it
        })

        binding.btnSendMessage.setOnClickListener {
            sendMessages()
            Toast.makeText(requireActivity(), "Les sms on ete envoyee", Toast.LENGTH_LONG).show()
            showReminderDialog()
        }

        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel() {
        val name = "Notif channel"
        val desc = "A description for the channel"
        val importance = NotificationManager.IMPORTANCE_HIGH
        val channel = NotificationChannel(channelID , name , importance)
        channel.description = desc
        val notificationManager = activity?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    private fun sendMessages() = lifecycleScope.launch {
        if (checkSmsPermission()){
            for (student in studentsList){
                sendSMS(student.numParent1 , binding.etMessage.text.toString())
                sendSMS(student.numParent2 , binding.etMessage.text.toString())

                delay(200L)
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

    private fun showReminderDialog() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel()
        }

        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.subscription_reminder_dialog)

        val setReminder = dialog.findViewById<Button>(R.id.btn_remind_me)
        val datePicker = dialog.findViewById<DatePicker>(R.id.date_picker)
        val timePicker = dialog.findViewById<TimePicker>(R.id.time_picker)

        setReminder.setOnClickListener {
            val minute = timePicker.minute
            val hour = timePicker.hour
            val day = datePicker.dayOfMonth
            val month = datePicker.month
            val year = datePicker.year

            val calendar = Calendar.getInstance()
            calendar.set(year , month , day , hour , minute)

            scheduleNotification(calendar.timeInMillis)

            dialog.dismiss()
        }

        dialog.show()
    }

    private fun scheduleNotification(timeInMillis: Long) {
        val intent = Intent(requireActivity() , Notification::class.java)
        val title = "Rappel"
        val message = "Rappel sur l'envoi d'un message d'abonnement"
        intent.putExtra(titleExtra , title)
        intent.putExtra(messageExtra , message)

        val pendingIntent = PendingIntent.getBroadcast(
            requireContext() ,
            notificationID ,
            intent ,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        val alarmManager = activity?.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP ,
            timeInMillis ,
            pendingIntent
        )
        Toast.makeText(requireActivity(), "Rappel a ete definit", Toast.LENGTH_LONG).show()

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
}