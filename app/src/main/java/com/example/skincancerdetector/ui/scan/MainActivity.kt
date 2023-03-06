package com.example.skincancerdetector.ui.scan

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.Window
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import com.example.skincancerdetector.R
import com.example.skincancerdetector.data.Repository
import com.example.skincancerdetector.databinding.ActivityAuthBinding
import com.example.skincancerdetector.databinding.ActivityMainBinding
import com.example.skincancerdetector.model.*

class MainActivity : AppCompatActivity() {

    private val repository = Repository()
    private val classifier = ImageClassifier(this)
    private lateinit var scanViewModel: ScanVM
    private lateinit var binding : ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)

        scanViewModel = ViewModelProvider(
            this,
            ScanViewModelFactory(repository,classifier)
        )[ScanVM::class.java]

        //Buat masukin gambar kedalam viewmodel
        fun onImageCaptureResult(result: ActivityResult) {
            if (result.resultCode == Activity.RESULT_OK) {
                val imageBitmap = result.data?.extras?.get("data") as Bitmap
                scanViewModel.storeImage(imageBitmap,80)
            }
        }

        binding.navView.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.navigation_home -> {
                    // Kalo klik home (masih gatau mau naro apa di halaman home)
                    true
                }
                R.id.navigation_scan -> {
                    //Nyalain kamera/storage
                    val pickIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                    val chooserIntent = Intent.createChooser(pickIntent, "Select Image")
                    chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, arrayOf(cameraIntent))

                    if (chooserIntent.resolveActivity(this.packageManager) != null) {
                        registerForActivityResult(
                            ActivityResultContracts.StartActivityForResult(), ::onImageCaptureResult
                        ).launch(chooserIntent)
                    }
                    true
                }
                R.id.navigation_history -> {
                    // Handle History button click
                    true
                }
                else -> false
            }
        }

        setContentView(binding.root)
    }
}