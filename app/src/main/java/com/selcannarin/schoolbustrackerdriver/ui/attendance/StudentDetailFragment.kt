package com.selcannarin.schoolbustrackerdriver.ui.attendance

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.selcannarin.schoolbustrackerdriver.R
import com.selcannarin.schoolbustrackerdriver.databinding.FragmentStudentDetailBinding
import com.selcannarin.schoolbustrackerdriver.ui.MainActivity

class StudentDetailFragment : Fragment() {

    private lateinit var binding: FragmentStudentDetailBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentStudentDetailBinding.bind(view)
        (activity as MainActivity).setBottomNavVisibilityVisible()

        val toolbar = (activity as AppCompatActivity).supportActionBar
        toolbar?.title = "Student Detail"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_student_detail, container, false)
    }

}