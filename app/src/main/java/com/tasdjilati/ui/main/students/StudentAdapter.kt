package com.tasdjilati.ui.main.students

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.LiveData
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.tasdjilati.R
import com.tasdjilati.data.entities.Student

class StudentAdapter(
    private val studentsList: List<Student>
)
    : RecyclerView.Adapter<StudentAdapter.ItemViewHolder>() {

    class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var tvName: TextView = view.findViewById(R.id.tv_name)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val adapterLayout = LayoutInflater.from(parent.context)
            .inflate(R.layout.student_rv_item, parent, false)
        return ItemViewHolder(adapterLayout)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.tvName.text = studentsList[position].name + " " + studentsList[position].surname

        holder.itemView.setOnClickListener {
            val bundle = Bundle()
            bundle.putSerializable("student" , studentsList[position])
            Navigation.findNavController(holder.itemView).navigate(R.id.action_studentsListFragment_to_viewStudentFragment , bundle)
        }
        }

    override fun getItemCount()= studentsList.size

}