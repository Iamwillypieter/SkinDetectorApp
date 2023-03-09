package com.example.skincancerdetector.model

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.lifecycle.*
import com.example.skincancerdetector.data.Repository
import com.example.skincancerdetector.data.ScanData
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.*
import kotlin.random.Random

class ScanVM(
    private val repository: Repository,
    //private val classifier: ImageClassifier
    ) : ViewModel() {

    private val _imageBitmap = MutableLiveData<Bitmap?>()
    val imageBitmap: LiveData<Bitmap?> = _imageBitmap

    private val loadingScan = MutableLiveData<Boolean>()

    private val _resultData = MutableLiveData<Map<String,Float>?>()
    val resultData : LiveData<Map<String,Float>?> = _resultData

    var path : String? = null

    fun getUserId():String?{
        return repository.getUser()?.uid
    }

    fun storeImage(bitmap: Bitmap, quality:Int) {
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
        val compressedBitmap = BitmapFactory.decodeByteArray(outputStream.toByteArray(), 0, outputStream.size())
        _imageBitmap.value = compressedBitmap
    }

    private val _scans = MutableLiveData<List<ScanData>>()
    val scans: LiveData<List<ScanData>>
        get() = _scans

    fun addScan(
        scanId: String,
        name: String,
        bodyPart: String,
        age: Int,
        gender: String,
        note: String,
        results: Map<String, Float>
    ) {
        viewModelScope.launch {
            val scan = path?.let { ScanData(scanId,name,bodyPart, age, gender, note, results, it) }
            if (scan != null) {
                repository.addScan(scan)
            }
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
    fun getCurrentDateAsString(format: String = "yyyy-MM-dd"): String {
        val dateFormat = SimpleDateFormat(format, Locale.getDefault())
        return dateFormat.format(Date())
    }

    fun uploadPicture(name:String) {
        val fileName = name + getCurrentDateAsString()
        path = repository.upload(
            imageBitmap.value,
            fileName,
            repository.getUser()!!.uid
        )
        analyzePicture()
    }

    fun analyzePicture(){
        viewModelScope.launch{
            loadingScan.value = true
            val data = fakeAnalyze()
            _resultData.value = data.also {
                loadingScan.value = false
            }
        }
    }

    private suspend fun fakeAnalyze(): Map<String, Float> {
        val labels = listOf("MEL", "AK", "UNK", "VASC", "BKL", "NV", "BCC", "DF", "SCC")
        delay(10000)
        return labels.associateWith { Random.nextFloat() }
    }

    //    fun analyze(): Map<String, Float>? { //This is for TfLite Model, but currently unused
//        return if (imageBitmap.value!=null) {
//            classifier.classifyImage(imageBitmap.value!!)
//        } else null
//    }

}

class ScanViewModelFactory(
    private val repository: Repository,
    //private val classifier: ImageClassifier
    ) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        when {
            modelClass.isAssignableFrom(ScanVM::class.java)->{
                ScanVM(
                    repository,
                    //classifier
                ) as T
            }
            else -> throw Throwable("Unknown ViewModel class: " + modelClass.name)
        }
}