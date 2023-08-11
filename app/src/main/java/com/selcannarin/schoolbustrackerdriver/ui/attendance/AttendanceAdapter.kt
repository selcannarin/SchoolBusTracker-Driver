package com.selcannarin.schoolbustrackerdriver.ui.attendance

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.selcannarin.schoolbustrackerdriver.data.model.Student
import com.selcannarin.schoolbustrackerdriver.databinding.StudentAttendanceCardViewBinding

class AttendanceAdapter(
    private val attendanceList: List<Student>
) : RecyclerView.Adapter<AttendanceAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = StudentAttendanceCardViewBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val student = attendanceList[position]
        holder.bind(student)
    }

    override fun getItemCount(): Int {
        return attendanceList.size
    }

    inner class ViewHolder(private val binding: StudentAttendanceCardViewBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(student: Student) {
            binding.apply {
                textViewStudentName.text = student.student_name
                textViewStudentNumber.text = student.student_number.toString()
            }
        }
    }
}
