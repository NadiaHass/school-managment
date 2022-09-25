package com.tasdjilati.ui.transport

import android.app.Activity
import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.tasdjilati.R
import com.tasdjilati.data.entities.StudentTransport

class StudentTransportAdapter(
    private val studentsList: List<StudentTransport>,
    private val studentViewModel: StudentTransportViewModel,
    private val activity : Activity
)
    : RecyclerView.Adapter<StudentTransportAdapter.ItemViewHolder>() {

    class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var tvName: TextView = view.findViewById(R.id.tv_name)
        var moreImage : ImageView = view.findViewById(R.id.iv_more)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val adapterLayout = LayoutInflater.from(parent.context)
            .inflate(R.layout.student_rv_item, parent, false)
        return ItemViewHolder(adapterLayout)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.tvName.text = studentsList[position].name + " " + studentsList[position].surname
//        holder.tvName.text = studentsList[position].id.toString()

        holder.itemView.setOnClickListener {
            val bundle = Bundle()
            bundle.putSerializable("studentTransport" , studentsList[position])
            Navigation.findNavController(holder.itemView).navigate(R.id.action_transportStudentsFragment_to_showTransportStudentFragment , bundle)
        }

        holder.moreImage.setOnClickListener {
            deleteStudent(studentsList[position])
        }
    }

    private fun deleteStudent(student: StudentTransport) {
        val builder: AlertDialog.Builder = activity.let {
            AlertDialog.Builder(it)
        }

        builder.setMessage("Voulez vous vraiment supprimer l'eleve ?")
            .setTitle("Supprimer eleve")

        builder.apply {
            setPositiveButton("Oui") { dialog, id ->
                studentViewModel.deleteStudent(student)
            }
            setNegativeButton("Non") { dialog, id ->
                dialog.dismiss()
            }
        }
        val dialog: AlertDialog? = builder.create()

        dialog!!.show()

    }

    override fun getItemCount()= studentsList.size

}