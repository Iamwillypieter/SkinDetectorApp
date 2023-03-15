package com.example.skincancerdetector.model

import androidx.lifecycle.*
import com.example.skincancerdetector.data.Disease
import com.example.skincancerdetector.data.Repository
import com.example.skincancerdetector.data.ScanData
import kotlinx.coroutines.launch


class AnalysisVM(private val repository: Repository) :ViewModel() {

    var currentScan : ScanData? = null

    private val _diseases = MutableLiveData<List<Disease>>()
    val diseases : LiveData<List<Disease>> = _diseases

    init{
        getDiseaseData()
    }


    private fun getDiseaseData(){
        viewModelScope.launch {
            _diseases.value = repository.getAllDiseases()
        }
    }

}

class AnalysisViewModelFactory(
    private val repository: Repository,
    //private val classifier: ImageClassifier
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        when {
            modelClass.isAssignableFrom(AnalysisVM::class.java) -> {
                AnalysisVM(
                    repository,
                ) as T
            }
            else -> throw Throwable("Unknown ViewModel class: " + modelClass.name)
        }
}
