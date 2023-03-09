package com.example.skincancerdetector.ui.scan

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.skincancerdetector.databinding.FragmentFormBinding
import com.example.skincancerdetector.model.ScanVM

class FormFragment : Fragment() {
    private lateinit var binding : FragmentFormBinding
    private lateinit var scanViewModel: ScanVM
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentFormBinding.inflate(inflater,container,false)
        val name = binding.edName.text.toString()
        val bodyPart = binding.edBody.text.toString()
        val age = binding.edAge.text.toString().toIntOrNull()
        val gender = binding.edGender.text.toString()

        scanViewModel = ViewModelProvider(requireActivity())[ScanVM::class.java]
        val userId = scanViewModel.getUserId()

        scanViewModel.imageBitmap.observe(requireActivity()){
            Glide.with(this)
                .load(it)
                .circleCrop()
                .into(binding.imageView2)
        }

        scanViewModel.resultData.observe(requireActivity()){results ->
            if (results!=null && age!=null && userId!=null){
                scanViewModel.addScan(userId, name, bodyPart, age, gender, "null", results)
            }
        }

        binding.btnAnalyze.setOnClickListener {
            if (name.isEmpty() || bodyPart.isEmpty() || (age == null) || gender.isEmpty()) {
                // Make a toast that some of the form are empty
                scanViewModel.uploadPicture(name)
            }
            scanViewModel.uploadPicture(name)
        }

        return binding.root
    }
}