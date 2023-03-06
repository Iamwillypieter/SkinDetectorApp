package com.example.skincancerdetector.model

import android.content.Context
import androidx.lifecycle.*
import com.example.skincancerdetector.data.Repository
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.launch

class AuthVM(private val repository: Repository) : ViewModel() {

    private val _loggedInUser = MutableLiveData<FirebaseUser?>()
    val loggedInUser: MutableLiveData<FirebaseUser?> = _loggedInUser

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    init{
        autoLogin()
    }

    fun autoLogin(): FirebaseUser? {
        _loggedInUser.value = repository.getUser()
        return _loggedInUser.value
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            try {
                val user = repository.login(email, password)
                _loggedInUser.value = user
            } catch (e: Exception) {
                _errorMessage.value = e.message
            }
        }
    }

    fun register(email: String, password: String) {
        viewModelScope.launch {
            try {
                val user = repository.register(email, password)
                _loggedInUser.value = user
            } catch (e: Exception) {
                _errorMessage.value = e.message
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            try {
                repository.logout()
                _loggedInUser.value = null
            } catch (e: Exception) {
                _errorMessage.value = e.message
            }
        }
    }

    // Function to sign in with Google...
    // Yeah fuck that, no

}

class AuthViewModelFactory(
    private val repository: Repository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        when {
            modelClass.isAssignableFrom(AuthVM::class.java)->{
                AuthVM(repository) as T
            }
            else -> throw Throwable("Unknown ViewModel class: " + modelClass.name)
        }
}