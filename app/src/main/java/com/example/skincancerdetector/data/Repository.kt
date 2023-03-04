package com.example.skincancerdetector.data
import kotlinx.coroutines.tasks.await

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Date

class Repository {

    private val firestore = FirebaseFirestore.getInstance()
    private val scans = "scan_data"
    private val users = "user_data"
    private val auth = FirebaseAuth.getInstance()

    suspend fun addScan(scan: ScanData) {
        val scansCollection = firestore.collection("scans")
        scansCollection.add(scan).await()
    }

    suspend fun getScans(userId: String): List<ScanData> {
        val scansCollection = firestore.collection("scans")
        val query = scansCollection.whereEqualTo("userId", userId)
        val querySnapshot = query.get().await()
        return querySnapshot.toObjects(ScanData::class.java)
    }

    suspend fun getScan(scanId: String): ScanData? {
        val scansCollection = firestore.collection("scans")
        val documentSnapshot = scansCollection.document(scanId).get().await()
        return documentSnapshot.toObject(ScanData::class.java)
    }

    suspend fun updateScan(scanId: String, notes: String) {
        val scansCollection = firestore.collection("scans")
        val scanRef = scansCollection.document(scanId)
        val updateMap = mapOf("notes" to notes)
        scanRef.update(updateMap).await()
    }

    suspend fun deleteScan(scanId: String) {
        val scansCollection = firestore.collection("scans")
        scansCollection.document(scanId).delete().await()
    }

    suspend fun login(email: String, password: String): FirebaseUser {
        val authResult = auth.signInWithEmailAndPassword(email, password).await()
        return authResult.user ?: throw Exception("Login failed. User is null.")
    }

    suspend fun register(email: String, password: String): FirebaseUser {
        val authResult = auth.createUserWithEmailAndPassword(email, password).await()
        return authResult.user ?: throw Exception("Registration failed. User is null.")
    }

    fun logout() {
        auth.signOut()
    }


}

//id - The unique identifier of the scan.
//patientId - The ID of the patient who was scanned.
//userId - The ID of the user who performed the scan.
//image - The scanned image of the skin lesion.
//diagnosis - The diagnosis of the skin lesion, as determined by the machine learning algorithm.
//created - The timestamp of when the scan was created.

data class ScanData(
    val userId: String,
    val imageUrl: String,
    val date: Date,
    val results: Map<String, Float>,
)