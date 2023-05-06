package com.example.skincancerdetector.ui.scan

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.skincancerdetector.R
import com.example.skincancerdetector.data.ScanData
import com.example.skincancerdetector.databinding.FragmentAnalysisBinding
import com.example.skincancerdetector.databinding.FragmentFormBinding
import com.example.skincancerdetector.databinding.FragmentHistoryBinding
import com.example.skincancerdetector.model.ScanVM
import com.example.skincancerdetector.ui.analysis.AnalysisActivity
import com.example.skincancerdetector.ui.analysis.AnalysisAdapter


class HistoryFragment : Fragment() {

    private lateinit var binding : FragmentHistoryBinding
    private lateinit var adapter : HistoryAdapter
    private lateinit var scanViewModel: ScanVM

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentHistoryBinding.inflate(inflater,container,false)
        binding.rvHistory.layoutManager = LinearLayoutManager(this.context)
        scanViewModel = ViewModelProvider(requireActivity())[ScanVM::class.java]

        scanViewModel.allScanData.observe(requireActivity()){
            adapter = HistoryAdapter(it)
            adapter.setOnItemClickCallback(object : HistoryAdapter.OnItemClickCallback {
                override fun onItemClicked(data: ScanData) {
                    startActivity(
                        Intent(requireActivity(), AnalysisActivity::class.java)
                            .putExtra(AnalysisActivity.SCAN_DATA, data)
                    )
                }

                override fun deleteItem(data: String) {
                    scanViewModel.deleteScanData(data)
                    scanViewModel.getAllUserAnalysisData()
                }
            })
            binding.rvHistory.adapter = adapter
        }
        return binding.root
    }

}