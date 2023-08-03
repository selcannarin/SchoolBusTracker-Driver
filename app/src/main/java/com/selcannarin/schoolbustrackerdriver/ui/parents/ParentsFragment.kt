package com.selcannarin.schoolbustrackerdriver.ui.parents

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.selcannarin.schoolbustrackerdriver.databinding.FragmentParentsBinding
import com.selcannarin.schoolbustrackerdriver.ui.MainActivity

class ParentsFragment : Fragment() {

    private lateinit var binding: FragmentParentsBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentParentsBinding.inflate(inflater)
        return binding.root
    }
}




