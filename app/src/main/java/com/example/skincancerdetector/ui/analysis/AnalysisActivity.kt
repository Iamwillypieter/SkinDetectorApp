package com.example.skincancerdetector.ui.analysis

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.annotation.StringRes
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import com.example.skincancerdetector.R
import com.example.skincancerdetector.data.Repository
import com.example.skincancerdetector.data.ScanData
import com.example.skincancerdetector.databinding.ActivityAnalysisBinding
import com.example.skincancerdetector.databinding.ActivityMainBinding
import com.example.skincancerdetector.model.AnalysisVM
import com.example.skincancerdetector.model.AnalysisViewModelFactory
import com.example.skincancerdetector.model.ScanVM
import com.example.skincancerdetector.model.ScanViewModelFactory
import com.example.skincancerdetector.ui.scan.MainActivity

class AnalysisActivity : AppCompatActivity() {

    private val repository = Repository()
    private lateinit var binding : ActivityAnalysisBinding
    private lateinit var analysisViewModel : AnalysisVM
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAnalysisBinding.inflate(layoutInflater)
        val scanData = intent.getParcelableExtra<ScanData>(SCAN_DATA)

        analysisViewModel = ViewModelProvider(this, AnalysisViewModelFactory(repository))[AnalysisVM::class.java]
        if (scanData != null) {
            analysisViewModel.currentScan = scanData
        }

        setContentView(binding.root)
    }

    companion object{
        const val SCAN_DATA = "scan_data"
    }
}