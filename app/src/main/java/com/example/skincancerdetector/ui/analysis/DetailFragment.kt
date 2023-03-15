package com.example.skincancerdetector.ui.analysis

import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.skincancerdetector.R
import com.example.skincancerdetector.data.Disease
import com.example.skincancerdetector.databinding.FragmentAnalysisBinding
import com.example.skincancerdetector.databinding.FragmentDetailBinding
import com.example.skincancerdetector.model.AnalysisVM


class DetailFragment : Fragment() {

    private lateinit var binding : FragmentDetailBinding
    private lateinit var analysisViewModel : AnalysisVM
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {

        val value = arguments?.getString("key")
        binding =  FragmentDetailBinding.inflate(inflater,container,false)
        analysisViewModel = ViewModelProvider(requireActivity())[AnalysisVM::class.java]
        analysisViewModel.diseases.observe(requireActivity()){diseases ->
            for (disease in diseases) {
                Log.d("Eyy Fuck You", disease.id + " != " + value,)
                if (disease.id == value) {
                    Log.d("Fuck You Too", disease.id)
                    showData(disease)
                    break // exit the loop since we found the matching disease
                }
            }
        }

        // Inflate the layout for this fragment

        return binding.root
    }

    private fun showData(disease: Disease) {

        val imageContainer = binding.imageContainer
        val symptomContainer = binding.containerSymptom
        val treatContainer = binding.containerTreatment
        val preventContainer = binding.containerPreventive

        binding.tvDetailName.text = disease.name
        binding.tvDetailDescription.text = disease.description
        Log.d("FFFFUCK YOU!!!", disease.description)
        binding.tvDetailShortDesc.text = "Fuck Off"

        for (imageUrl in disease.images) {
            val imageView = ImageView(requireActivity())
            imageView.layoutParams = ViewGroup.LayoutParams(400, 300)
            Glide.with(this)
                .load(imageUrl)
                .into(imageView)
            imageContainer.addView(imageView)
        }

        addTextViews(disease.symptom, symptomContainer)
        addTextViews(disease.treatment, treatContainer)
        addTextViews(disease.prevent, preventContainer)
    }

    private fun addTextViews(strings: List<String>, container: LinearLayout) {
        for (string in strings) {
            val textView = TextView(requireActivity())
            textView.text = string
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
            val layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            layoutParams.bottomMargin = resources.getDimensionPixelSize(R.dimen.margin_bottom_text_view)
            textView.layoutParams = layoutParams
            container.addView(textView)
        }
    }

}