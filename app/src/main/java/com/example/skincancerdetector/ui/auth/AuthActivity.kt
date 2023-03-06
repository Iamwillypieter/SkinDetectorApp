package com.example.skincancerdetector.ui.auth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.example.skincancerdetector.R
import com.example.skincancerdetector.data.Repository
import com.example.skincancerdetector.databinding.ActivityAuthBinding
import com.example.skincancerdetector.model.AuthVM
import com.example.skincancerdetector.model.AuthViewModelFactory
import com.example.skincancerdetector.ui.scan.MainActivity

class AuthActivity : AppCompatActivity() {
    private val repository = Repository()
    private lateinit var authViewModel:AuthVM
    private lateinit var binding : ActivityAuthBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthBinding.inflate(layoutInflater)

        authViewModel = ViewModelProvider(this, AuthViewModelFactory(repository))
            .get(AuthVM::class.java)

        authViewModel.loggedInUser.observe(this){
            if(it==null)
                Navigation.findNavController(
                    this, R.id.fragmentContainerView
                ).navigate(R.id.loginFragment)
            else
                startActivity(
                    Intent(this,MainActivity::class.java)
                )
        }

        setContentView(binding.root)

    }
}