package com.example.skincancerdetector.ui.scan

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.example.skincancerdetector.R
import com.example.skincancerdetector.data.Repository
import com.example.skincancerdetector.databinding.ActivityMainBinding
import com.example.skincancerdetector.model.*

class MainActivity : AppCompatActivity() {

    private val repository = Repository()
    //private val classifier = ImageClassifier(this)
    private lateinit var scanViewModel: ScanVM
    private lateinit var binding : ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        binding = ActivityMainBinding.inflate(layoutInflater)

        scanViewModel = ViewModelProvider(
            this,
            ScanViewModelFactory(
                repository,
                //classifier
            )
        )[ScanVM::class.java]

        scanViewModel.imageBitmap.observe(this){
            if(it!=null) {
                Navigation.findNavController(
                    this, binding.fragContainMain.id
                ).navigate(R.id.formFragment)
            }
        }

        scanViewModel.scanResult.observe(this){
        //Ini Nanti
        //            if(it!=null){
//                Navigation.findNavController(
//                    this,binding.fragContainMain.id
//                ).navigate(R.id.analysisFragment)
//            }
        }

        val resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                // There are no request codes
                val data: Intent? = result.data
                val imageBitmap = data?.extras?.get("data") as Bitmap
                scanViewModel.storeImage(imageBitmap,100)
            }
        }

        fun dispatchTakePictureIntent() {
            val cameraPermission = android.Manifest.permission.CAMERA
            val storagePermission = android.Manifest.permission.WRITE_EXTERNAL_STORAGE

            if (ContextCompat.checkSelfPermission(this, cameraPermission) != PackageManager.PERMISSION_GRANTED) {
                // Permission not granted, request it
                print("nyaa?")
            } else {
                // Permission granted, launch camera intent
                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                resultLauncher.launch(intent)
                print("Nyaaaa")
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
                    dispatchTakePictureIntent()
                    true
                }
                R.id.navigation_history -> {
                    //Kalo klik histori
                    true
                }
                else -> false
            }
        }

        setContentView(binding.root)
    }



    //AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA
}