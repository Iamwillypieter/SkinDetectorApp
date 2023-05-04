package com.example.skincancerdetector.data

import android.os.Parcelable
import com.google.firebase.firestore.PropertyName
import kotlinx.parcelize.Parcelize
import java.util.*


@Parcelize
data class ScanData(
    val userId: String = "",
    val patientName: String = "",
    val bodyPart: String = "",
    val age: Int = 0,
    val gender: String = "",
    val result: Map<String, Float> = emptyMap(),
    val imageUrl : String = "",
    val timestamp : String = "",
):Parcelable

data class UserData(
    val userId: String,
    val name: String,
    val age: Int,
    val gender: String
)

data class Disease(
    @PropertyName("id") val id: String = "",
    @PropertyName("Name") val name: String = "",
    @PropertyName("Description") val description: String = "",
    @PropertyName("Symptom") val symptom: List<String> = emptyList(),
    @PropertyName("Treatment") val treatment: List<String> = emptyList(),
    @PropertyName("Prevent") val prevent: List<String> = emptyList(),
    @PropertyName("Images") val images: List<String> = emptyList()
)