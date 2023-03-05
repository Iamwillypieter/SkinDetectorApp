package com.example.skincancerdetector.data

import java.util.*

data class ScanData(//Reminder, no need to code for scanId because, because FIREBASE!!!
    val userId: String,
    val imageUrl: String,
    val date: Date,
    val bodypart: String,
    val personAge: Int,
    val tumourAge: Int,
    val notes: String,
    val results: Map<String, Float>,
)