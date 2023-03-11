package com.example.skincancerdetector.data

import java.util.*

data class ScanData(//Reminder, no need to code for scanId because, because FIREBASE!!!
    val userId: String = "",
    val patientName: String = "",
    val bodyPart: String = "",
    val age: Int = 0,
    val gender: String = "",
    val result: Map<String, Float> = emptyMap(),
    val imageUrl : String = ""
)

data class UserData(
    val userId: String,
    val name: String,
    val age: Int,
    val gender: String
)