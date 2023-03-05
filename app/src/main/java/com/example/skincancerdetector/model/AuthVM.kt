package com.example.skincancerdetector.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.skincancerdetector.data.Repository
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.launch

class AuthVM(private val repository: Repository) : ViewModel() {

    private val _loggedInUser = MutableLiveData<FirebaseUser?>()
    val loggedInUser: MutableLiveData<FirebaseUser?> = _loggedInUser

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    init {
        autoLogin()
    }

    private fun autoLogin(){
        _loggedInUser.value = repository.getUser()
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
}