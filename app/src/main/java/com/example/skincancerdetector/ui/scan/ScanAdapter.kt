package com.example.skincancerdetector.ui.scan

import android.service.autofill.UserData
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.skincancerdetector.data.ScanData
import com.example.skincancerdetector.databinding.ScanRowBinding

class ScanAdapter(private val scanData:ScanData) : RecyclerView.Adapter<ViewHolderScans>(){
    // Put Someting Here :)
    private lateinit var onItemClickCallback: OnItemClickCallback
    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
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
                // set the data for this item in the ViewHolder
                holder.binding.tvRowTitle.text = key
                holder.binding.tvRowPercent.text = "%.2f%%".format(value * 100)
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
