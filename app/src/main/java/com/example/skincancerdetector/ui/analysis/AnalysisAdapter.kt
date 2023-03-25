package com.example.skincancerdetector.ui.analysis

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.skincancerdetector.data.Disease
import com.example.skincancerdetector.data.ScanData
import com.example.skincancerdetector.databinding.ScanRowBinding

class AnalysisAdapter(
    private val scanData:ScanData,
    private val diseases:List<Disease>
) : RecyclerView.Adapter<ViewHolderScans>(){

    private lateinit var onItemClickCallback: OnItemClickCallback
    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    fun getName(key: String): String {
        val matchingDisease = diseases.find { disease -> disease.id == key }
        return matchingDisease?.name ?: "Unknown disease"
    }
    fun getPic(key: String): String {
        val matchingDisease = diseases.find { disease -> disease.id == key }
        return matchingDisease?.images?.get(0) ?: ""
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderScans {
        val binding = ScanRowBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return ViewHolderScans(binding)
    }

    override fun getItemCount(): Int {
        return scanData.result.size
    }

    override fun onBindViewHolder(holder: ViewHolderScans, position: Int) {
        val scanResultEntries = scanData.result.entries.toList() // get a list of key-value pairs
        scanResultEntries.forEachIndexed { index, (key, value) ->
            if (index == position) {
                holder.binding.tvRowTitle.text = getName(key)
                holder.binding.donutProgress.setProgress((value*100).toDouble(), 100.0)
                Glide.with(holder.binding.imgScanRow)
                    .load(getPic(key))
                    .into(holder.binding.imgScanRow)
                holder.binding.root.setOnClickListener {
                    onItemClickCallback.onItemClicked(key)
                }
            }
        }

    }
    interface OnItemClickCallback{
        fun onItemClicked(data:String)
    }

}

class ViewHolderScans(
  val binding : ScanRowBinding
):RecyclerView.ViewHolder(binding.root)
