package com.example.skincancerdetector.ui.auth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
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
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(binding.root)

        authViewModel = ViewModelProvider(this, AuthViewModelFactory(repository))[AuthVM::class.java]

        authViewModel.loggedInUser.observe(this){
            if(it==null)
                Navigation.findNavController(
                    this, binding.fragContainAuth.id
                ).navigate(R.id.loginFragment)
            else
                startActivity(
                    Intent(this, MainActivity::class.java).apply {
                        addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or
                                Intent.FLAG_ACTIVITY_NEW_TASK or
                                Intent.FLAG_ACTIVITY_CLEAR_TASK
                        )
                    }
                )
        }

    }
}