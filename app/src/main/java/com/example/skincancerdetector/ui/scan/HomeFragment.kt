package com.example.skincancerdetector.ui.scan

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import com.example.skincancerdetector.R
import com.example.skincancerdetector.databinding.FragmentHistoryBinding
import com.example.skincancerdetector.databinding.FragmentHomeBinding
import com.example.skincancerdetector.model.AnalysisVM
import com.example.skincancerdetector.model.ScanVM

class HomeFragment : Fragment() {

    private lateinit var binding : FragmentHomeBinding
    private lateinit var scanViewModel: ScanVM
    val cameraPermission = android.Manifest.permission.CAMERA
    val mediaPermission = android.Manifest.permission.ACCESS_MEDIA_LOCATION
    val storagePermission = android.Manifest.permission.READ_EXTERNAL_STORAGE

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        scanViewModel = ViewModelProvider(requireActivity())[ScanVM::class.java]
        scanViewModel.modelCondition.observe(requireActivity()){
            if(it){
                binding.tvHome.text = "Model Ready To Use"

            }
            else{
                binding.tvHome.text = "Model Failed To Download, Check Your Internet Connection"
            }
        }



        return binding.root
    }

}