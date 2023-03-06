package com.example.skincancerdetector.ui.auth

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.example.skincancerdetector.R
import com.example.skincancerdetector.databinding.FragmentLoginBinding
import com.example.skincancerdetector.databinding.FragmentRegisterBinding
import com.example.skincancerdetector.model.AuthVM

class RegisterFragment : Fragment() {
    private lateinit var binding : FragmentRegisterBinding
    private lateinit var authViewModel: AuthVM

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentRegisterBinding.inflate(inflater,container,false)

        authViewModel = ViewModelProvider(requireActivity())[AuthVM::class.java]
        authViewModel.errorMessage.observe(viewLifecycleOwner){
            //Error handling disini, buat toast aja
        }

        binding.buttonRegister.setOnClickListener {//Register Function
            val email = binding.editTextEmail.text.toString()
            val password = binding.editTextPassword.text.toString()
            val confpass = binding.editTextConfirmPassword.text.toString()
            if (email.isEmpty() || password.isEmpty()) {
                // Buat Toast kalo Email atau Password kosong
                return@setOnClickListener
            }
            if(password!=confpass){
                // Buat Toast kalo password beda
                return@setOnClickListener
            }
            authViewModel.register(email, password)
        }

        return binding.root
    }
}