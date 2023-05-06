package com.example.skincancerdetector.ui.scan

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.skincancerdetector.data.ScanData
import com.example.skincancerdetector.databinding.HistoryRowBinding
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.toObject

class HistoryAdapter(private val allScanData : Map<String, ScanData>): RecyclerView.Adapter<ViewHolderAnalysis>() {

    private lateinit var onItemClickCallback: OnItemClickCallback
    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderAnalysis {
        val binding = HistoryRowBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return ViewHolderAnalysis(binding)
    }

    override fun getItemCount(): Int {
        return allScanData.size
    }

    override fun onBindViewHolder(holder: ViewHolderAnalysis, position: Int) {
        val (documentId, scanData) = allScanData.entries.toList()[position]
        with(holder) {
            with(scanData) {
                binding.tvRowHistTitle.text = patientName
                binding.tvRowHistBody.text = bodyPart
                binding.tvRowHistDate.text = timestamp
                Glide.with(binding.ivRowHist)
                    .load(imageUrl)
                    .into(binding.ivRowHist)
                binding.root.setOnClickListener {
                    onItemClickCallback.onItemClicked(scanData)
                }
                binding.deleteButton.setOnClickListener {
                    onItemClickCallback.deleteItem(documentId)
                }
            }
        }
    }

    interface OnItemClickCallback{
        fun onItemClicked(data:ScanData)
        fun deleteItem(data:String)
    }
}

class ViewHolderAnalysis(
    val binding : HistoryRowBinding
):RecyclerView.ViewHolder(binding.root)
