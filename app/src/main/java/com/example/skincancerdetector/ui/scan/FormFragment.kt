package com.example.skincancerdetector.ui.scan

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.skincancerdetector.R
import com.example.skincancerdetector.databinding.FragmentFormBinding
import com.example.skincancerdetector.databinding.FragmentLoginBinding
import com.example.skincancerdetector.model.AuthVM
import com.example.skincancerdetector.model.ScanVM

class FormFragment : Fragment() {
    private lateinit var binding : FragmentFormBinding
    private lateinit var scanViewModel: ScanVM
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentFormBinding.inflate(inflater,container,false)

        scanViewModel = ViewModelProvider(requireActivity())[ScanVM::class.java]
        scanViewModel.imageBitmap.observe(requireActivity()){
            Glide.with(this)
                .load(it)
                .circleCrop()
                .into(binding.imageView2)
        }
        return binding.root
    }
}