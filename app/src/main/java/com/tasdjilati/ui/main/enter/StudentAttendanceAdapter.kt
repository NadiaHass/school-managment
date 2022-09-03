package com.tasdjilati.ui.main.enter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.tasdjilati.R
import com.tasdjilati.data.entities.StudentEnterAttendance

class StudentAttendanceAdapter (
    private val studentsList: List<StudentEnterAttendance>
)
    : RecyclerView.Adapter<StudentAttendanceAdapter.ItemViewHolder>() {

    class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var tvName: TextView = view.findViewById(R.id.tv_name)
        var doneImage : ImageView = view.findViewById(R.id.iv_done)
        var errorImage : ImageView = view.findViewById(R.id.iv_error)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val adapterLayout = LayoutInflater.from(parent.context)
            .inflate(R.layout.student_attendance_rv_item, parent, false)
        return ItemViewHolder(adapterLayout)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.tvName.text = studentsList[position].name + " " + studentsList[position].surname
        if (studentsList[position].attendance == 1){
            holder.errorImage.visibility = View.GONE
            holder.doneImage.visibility = View.VISIBLE
        }
    }

    override fun getItemCount()= studentsList.size

}