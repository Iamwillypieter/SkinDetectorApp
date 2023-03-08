package com.example.skincancerdetector.ui.auth

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.skincancerdetector.R
import com.example.skincancerdetector.databinding.FragmentLoginBinding
import com.example.skincancerdetector.model.AuthVM


class LoginFragment : Fragment() {
    private lateinit var binding : FragmentLoginBinding
    private lateinit var authViewModel: AuthVM

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentLoginBinding.inflate(inflater,container,false)

        authViewModel = ViewModelProvider(requireActivity())[AuthVM::class.java]
        authViewModel.errorMessage.observe(viewLifecycleOwner){
            //Error handling disini, buat toast aja
            //Stringnya
        }

//        binding.buttonLogin.setOnClickListener {
//            val email = binding.editTextEmail.text.toString()
//            val password = binding.editTextPassword.text.toString()
//            if (email.isEmpty() || password.isEmpty()) {
//                // Buat Toast kalo Email atau Password kosong
//                return@setOnClickListener
//            }
//            authViewModel.login(email, password)
//        } //Login Function

//        binding.buttonRegister.setOnClickListener {
//            findNavController().navigate(R.id.registerFragment)
//        }

        return binding.root
    }

}