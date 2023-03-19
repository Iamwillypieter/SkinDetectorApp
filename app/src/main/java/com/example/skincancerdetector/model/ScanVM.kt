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

    private val _allScanData = MutableLiveData<List<ScanData>>()
    val allScanData: LiveData<List<ScanData>> = _allScanData

    private val _scanResult = MutableLiveData<ScanData?>()
    val scanResult: LiveData<ScanData?> = _scanResult

    val loadingScan = MutableLiveData<Boolean>()
    val errorKah = MutableLiveData<Boolean>()

    private fun getUserId(): String? {
        return repository.getUser()?.uid
    }

    init{
        getAllUserAnalysisData()
    }

    fun storeImage(bitmap: Bitmap, quality: Int) {
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
        val compressedBitmap =
            BitmapFactory.decodeByteArray(outputStream.toByteArray(), 0, outputStream.size())
        _imageBitmap.value = compressedBitmap
    }

    private fun getCurrentDateAsString(format: String = "yyyy-MM-dd-mm-ss"): String {
        val dateFormat = SimpleDateFormat(format, Locale.getDefault())
        return dateFormat.format(Date())
    }

    fun handleAnalyzeButtonClick(scanData: ScanData) {
        viewModelScope.launch {
            loadingScan.value = true
            try {
                print("Access Bitmap")
                val bitmap = imageBitmap.value
                print("Get User ID")
                val userId = getUserId() ?: throw Exception("User ID is null")
                if (bitmap == null) {
                    throw Exception("Bitmap is null")
                }
                val date = getCurrentDateAsString()
                print("Make new Document")
                val documentId = repository.createNewDocument(scanData)
                val fileName = "${scanData.patientName}${date}"
                val downloadUrl = repository.uploadImage(bitmap, fileName, userId)
                val result = fakeAnalyze()
                _scanResult.value = repository.updateDocument(documentId, downloadUrl, result, date)
                errorKah.value = false
            } catch (e: Exception) {
                print("Nya???"+e)
                errorKah.value = true
            } finally {
                loadingScan.value = false
            }
        }
    }

    private fun getAllUserAnalysisData(){
        viewModelScope.launch {
            _allScanData.value = repository.getAllUserScanData()
        }

    }


//    suspend fun createNewDocument(scanData: ScanData): String {
//        return withContext(Dispatchers.IO) {
//            repository.createNewDocument(scanData)
//
//        }
//    }
//
//    suspend fun uploadImage(bitmap: Bitmap, fileName: String, userId: String): String {
//        return withContext(Dispatchers.IO) {
//            repository.uploadImage(bitmap, fileName, userId)
//        }
//    }
//
//    suspend fun updateDocument(
//        documentId: String,
//        downloadUrl: String,
//        result: Map<String, Float>
//    ) :ScanData?{
//        return withContext(Dispatchers.IO) {
//            repository.updateDocument(documentId, downloadUrl, result)
//        }
//    }


    private suspend fun fakeAnalyze(): Map<String, Float> {
        val labels = listOf("ML", "AK", "UNK", "VASC", "BKL", "NV", "BCC", "DF", "SCC")
        delay(10000)
        return labels.associateWith { Random.nextFloat() }
    }
}

class ScanViewModelFactory(
    private val repository: Repository,
    //private val classifier: ImageClassifier
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        when {
            modelClass.isAssignableFrom(ScanVM::class.java) -> {
                ScanVM(
                    repository,
                ) as T
            }
            else -> throw Throwable("Unknown ViewModel class: " + modelClass.name)
        }
}


