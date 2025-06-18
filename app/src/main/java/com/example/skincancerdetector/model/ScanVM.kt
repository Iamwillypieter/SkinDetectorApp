package com.example.skincancerdetector.model

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.lifecycle.*
import com.example.skincancerdetector.data.Repository
import com.example.skincancerdetector.data.ScanData
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.toObject
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

    private val _allScanData = MutableLiveData<Map<String, ScanData>>()
    val allScanData: LiveData<Map<String, ScanData>> = _allScanData

    private val _scanResult = MutableLiveData<ScanData?>()
    val scanResult: LiveData<ScanData?> = _scanResult

    val loadingScan = MutableLiveData<Boolean>()
    val errorKah = MutableLiveData<Boolean>()
    val modelCondition = MutableLiveData<Boolean>()

    private fun getUserId(): String? {
        return repository.getUser()?.uid
    }

    init{
        getAllUserAnalysisData()
        getModel()
    }

    fun logout() {
        viewModelScope.launch {
            try {
                repository.logout()
            } catch (e: Exception) {

            }
        }
    }

    fun storeImage(bitmap: Bitmap, quality: Int) {
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
        val compressedBitmap =
            BitmapFactory.decodeByteArray(outputStream.toByteArray(), 0, outputStream.size())
        _imageBitmap.value = compressedBitmap
    }

    private fun getModel(){
        repository.downloadModel(
            onSuccess = {
                modelCondition.value = true
            },
            onFailure = {
                modelCondition.value = false
            }
        )
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
                val result = actualAnalyze(bitmap)
                _scanResult.value = repository.updateDocument(documentId, downloadUrl, result, date)
                loadingScan.value = false
                errorKah.value = false
            } catch (e: Exception) {
                print("Nya???"+e)
                errorKah.value = true
            } finally {
                loadingScan.value = false
            }
        }
    }

    fun deleteScanData(id:String){
        viewModelScope.launch {
            repository.deleteDocument(id)
        }
    }

    fun getAllUserAnalysisData(){
        viewModelScope.launch {
            _allScanData.value = repository.getAllUserScanData()
                ?.let { convertQuerySnapshotToMap(it) }
        }

    }

    private fun actualAnalyze(bitmap: Bitmap): Map<String, Float> {

        val labels = listOf("AK", "BCC", "DF", "MEL", "NV", "BKL", "SK", "SCC", "VASC")
        Log.i("this", "actualAnalyze")
        val values = repository.analyze(bitmap)
        Log.i("analyzed_values", values.toString())
        return labels.zip(values!!.asIterable()).toMap()
    }
}

    fun convertQuerySnapshotToMap(snapshot: QuerySnapshot): Map<String, ScanData> {
        val map = mutableMapOf<String, ScanData>()
        for (document in snapshot.documents) {
            val documentId = document.id
            val scanData = document.toObject<ScanData>()
            map[documentId] = scanData!!
        }
        return map
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


