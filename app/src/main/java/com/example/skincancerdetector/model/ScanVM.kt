package com.example.skincancerdetector.model

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import androidx.lifecycle.*
import com.example.skincancerdetector.data.Repository
import com.example.skincancerdetector.data.ScanData
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

class ScanVM(
    private val repository: Repository,
    private val classifier: ImageClassifier
    ) : ViewModel() {

    private var _imageUri = MutableLiveData<Uri?>()
    val imageUri: LiveData<Uri?> = _imageUri

    private val _imageBitmap = MutableLiveData<Bitmap?>()
    val imageBitmap: LiveData<Bitmap?> = _imageBitmap

    fun storeImage(bitmap: Bitmap, quality:Int) {
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
        val compressedBitmap = BitmapFactory.decodeByteArray(outputStream.toByteArray(), 0, outputStream.size())
        _imageBitmap.value = compressedBitmap
    }

    private fun createImageFile(context: Context): File? {
        // Create an image file name
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        val storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        )
    }

    private fun analyze(image:Uri){
        classifier.classifyImage(image)
    }

    private val _scans = MutableLiveData<List<ScanData>>()
    val scans: LiveData<List<ScanData>>
        get() = _scans

    fun addScan(scan: ScanData) {
        viewModelScope.launch {
            repository.addScan(scan)
        }
    }

    fun getScans(userId: String) {
        viewModelScope.launch {
            val scans = repository.getScans(userId)
            _scans.value = scans
        }
    }

    fun updateScan(scanId: String, notes: String) {
        viewModelScope.launch {
            repository.updateScan(scanId, notes)
        }
    }

    fun deleteScan(scanId: String) {
        viewModelScope.launch {
            repository.deleteScan(scanId)
        }
    }

}

class ScanViewModelFactory(
    private val repository: Repository,
    private val classifier: ImageClassifier
    ) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        when {
            modelClass.isAssignableFrom(ScanVM::class.java)->{
                ScanVM(repository,classifier) as T
            }
            else -> throw Throwable("Unknown ViewModel class: " + modelClass.name)
        }
}