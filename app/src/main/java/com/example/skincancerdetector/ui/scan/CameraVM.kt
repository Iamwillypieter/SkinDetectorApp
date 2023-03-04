package com.example.skincancerdetector.ui.scan

import android.content.Context
import android.net.Uri
import android.os.Environment
import androidx.core.content.FileProvider
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.skincancerdetector.data.Repository
import com.example.skincancerdetector.data.ScanData
import com.example.skincancerdetector.model.ImageClassifier
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class CameraVM(
    private val repository: Repository,
    private val classifier: ImageClassifier
    ) : ViewModel() {

    private var _imageUri = MutableLiveData<Uri?>()
    val imageUri: LiveData<Uri?>
        get() = _imageUri

    fun captureImage(context: Context) {
        val file = createImageFile(context)
        _imageUri.value = file?.let { FileProvider.getUriForFile(context, "com.example.app.fileprovider", it) }
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