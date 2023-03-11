package com.example.skincancerdetector.ui.scan

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.skincancerdetector.R
import com.example.skincancerdetector.data.ScanData
import com.example.skincancerdetector.databinding.FragmentAnalysisBinding
import com.example.skincancerdetector.databinding.FragmentFormBinding
import com.example.skincancerdetector.model.ScanVM

class AnalysisFragment : Fragment() {
    private lateinit var binding : FragmentAnalysisBinding
    private lateinit var scanViewModel: ScanVM
    private lateinit var adapter : ScanAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentAnalysisBinding.inflate(inflater,container,false)
        binding.rvScans.layoutManager = LinearLayoutManager(this.context)

        scanViewModel = ViewModelProvider(requireActivity())[ScanVM::class.java]
        scanViewModel.imageBitmap.observe(requireActivity()){
            Glide.with(this)
                .load(it)
                .circleCrop()
                .into(binding.ivAnalysis)
        }

        scanViewModel.scanResult.observe(requireActivity()){
            if(it!=null) {
                adapter = ScanAdapter(it)
                adapter.setOnItemClickCallback(object : ScanAdapter.OnItemClickCallback {
                    override fun onItemClicked(data: String) {
                        //D O   S O M E T H I N G
                    }
                })

                binding.rvScans.adapter = adapter
            }
        }


        // Inflate the layout for this fragment
        return binding.root
    }
}