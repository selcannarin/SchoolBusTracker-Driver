package com.selcannarin.schoolbustrackerdriver.ui.attendance

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.selcannarin.schoolbustrackerdriver.databinding.FragmentAttendanceBinding

class AttendanceFragment : Fragment() {

    private lateinit var binding: FragmentAttendanceBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAttendanceBinding.inflate(inflater)
        return binding.root
    }

}