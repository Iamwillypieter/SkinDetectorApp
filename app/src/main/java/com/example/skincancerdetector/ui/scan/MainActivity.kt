package com.example.skincancerdetector.ui.scan

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.example.skincancerdetector.R
import com.example.skincancerdetector.data.Repository
import com.example.skincancerdetector.databinding.ActivityMainBinding
import com.example.skincancerdetector.model.*
import com.example.skincancerdetector.ui.analysis.AnalysisActivity
import com.example.skincancerdetector.ui.auth.AuthActivity
import com.example.skincancerdetector.ui.utility.LoadingFragment

class MainActivity : AppCompatActivity() {

    private val repository = Repository()
    //private val classifier = ImageClassifier(this)
    private lateinit var scanViewModel: ScanVM
    val cameraPermission = android.Manifest.permission.CAMERA
    val storagePermission = android.Manifest.permission.READ_EXTERNAL_STORAGE
    val mediaPermission = android.Manifest.permission.ACCESS_MEDIA_LOCATION

    private lateinit var binding : ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        scanViewModel = ViewModelProvider(this, ScanViewModelFactory(repository))[ScanVM::class.java]

        scanViewModel.imageBitmap.observe(this){
            if(it!=null) {
                Navigation.findNavController(
                    this, binding.fragContainMain.id
                ).navigate(R.id.formFragment)
            }
        }




        scanViewModel.scanResult.observe(this){
            if(it!=null){
                val intent = Intent(this, MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finishAffinity()
                startActivity(
                    Intent(this, AnalysisActivity::class.java)
                        .putExtra(AnalysisActivity.SCAN_DATA, it)
                )
            }
        }



        scanViewModel.loadingScan.observe(this){ isLoading ->
            if (isLoading){
                supportFragmentManager.beginTransaction()
                    .replace(binding.fragContainMain.id, LoadingFragment())
                    .commit()
                binding.navView.visibility = View.GONE
            }
        }

        val resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                // There are no request codes
                val data: Intent? = result.data
                val imageBitmap = data?.extras?.get("data") as Bitmap
                scanViewModel.storeImage(imageBitmap,100)
            }
        }
        val documentLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            // Handle the result of the file picker intent here
            // The selected image can be loaded from the URI using a ContentResolver
            val contentResolver = applicationContext.contentResolver
            val imageBitmap = MediaStore.Images.Media.getBitmap(contentResolver, uri)
            scanViewModel.storeImage(imageBitmap, 100)
        }
        fun dispatchTakePictureIntent() {
            if(scanViewModel.modelCondition.value == true) {


                val pickImage = "Pick Image"
                val takePhoto = "Take Photo"

                val options = arrayOf<CharSequence>(pickImage, takePhoto)
                val builder = AlertDialog.Builder(this)
                builder.setTitle("Select Image Source")
                builder.setItems(options) { dialog, item ->
                    when (options[item]) {
                        pickImage -> {
                            if (ContextCompat.checkSelfPermission(this, storagePermission) != PackageManager.PERMISSION_GRANTED && (ContextCompat.checkSelfPermission(this,mediaPermission)) != PackageManager.PERMISSION_GRANTED
                            ) {
                                ActivityCompat.requestPermissions(
                                    this,
                                    arrayOf(storagePermission,mediaPermission),
                                    4
                                )
                            } else {
                                // Permission granted, launch file picker intent
                                val intent = Intent(Intent.ACTION_GET_CONTENT)
                                intent.type = "image/*" // only allow image file types
                                documentLauncher.launch(intent.type)
                            }
                        }
                        takePhoto -> {
                            if (ContextCompat.checkSelfPermission(
                                    this,
                                    cameraPermission
                                ) != PackageManager.PERMISSION_GRANTED
                            ) {
                                // Permission not granted, request it
                                ActivityCompat.requestPermissions(
                                    this,
                                    arrayOf(cameraPermission),
                                    6
                                )
                            } else {
                                // Permission granted, launch camera intent
                                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                                resultLauncher.launch(intent)
                            }
                        }
                    }
                }
                builder.show()
            }
            else {
                Toast.makeText(this,"Model Not Downloaded, Please Wait", Toast.LENGTH_SHORT).show()
            }
        }


        binding.navView.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.navigation_home -> {
                    // Kalo klik home (masih gatau mau naro apa di halaman home)
                    Navigation.findNavController(
                        this, binding.fragContainMain.id
                    ).navigate(R.id.homeFragment)
                    true
                }
                R.id.navigation_scan -> {
                    //Nyalain kamera/storage
                    dispatchTakePictureIntent()
                    true
                }
                R.id.navigation_history -> {
                    Navigation.findNavController(
                        this, binding.fragContainMain.id
                    ).navigate(R.id.historyFragment)
                    true
                }
                else -> false
            }
        }



        setContentView(binding.root)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.logout -> {
                scanViewModel.logout()
                startActivity(
                    Intent(this,AuthActivity::class.java).apply {
                        addFlags(
                            Intent.FLAG_ACTIVITY_CLEAR_TOP or
                                    Intent.FLAG_ACTIVITY_NEW_TASK or
                                    Intent.FLAG_ACTIVITY_CLEAR_TASK
                        )
                    }
                )
            }

        }
        return true
    }

}