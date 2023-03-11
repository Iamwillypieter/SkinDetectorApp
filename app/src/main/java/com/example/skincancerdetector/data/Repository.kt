package com.example.skincancerdetector.data
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import kotlinx.coroutines.tasks.await

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import java.util.Date

class Repository {

    private val firestore = Firebase.firestore
    private val storage = Firebase.storage
    private val scans = "scan_data"
    private val users = "user_data"
    private val auth = FirebaseAuth.getInstance()
    private val storageRef = storage.reference


    suspend fun addScan(scan: ScanData) {
        val scansCollection = firestore.collection("scans")
        scansCollection.add(scan).await()
    }

    fun getUser():FirebaseUser?{
        return auth.currentUser
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

    //Upload Image
    suspend fun uploadImage(image: Bitmap, fileName: String, userId: String): String {
        val baos = ByteArrayOutputStream()
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()
        val path = "$userId/Scans/$fileName.jpg"
        val imageRef = storageRef.child(path)
        return try {
            imageRef.putBytes(data).await()
            val downloadUrl = imageRef.downloadUrl.await()
            downloadUrl.toString()
        } catch (e: Exception) {
            throw e
        }
    }

    //Upload form data
    suspend fun createNewDocument(scanData: ScanData): String {
        val collectionRef = firestore.collection("scans")
        val documentRef = collectionRef.add(scanData).await()
        return documentRef.id
    }

    // Function to update a Firestore document with the download URL and analysis result
    suspend fun updateDocument(documentId: String, downloadUrl: String, result: Map<String,Float>): ScanData? {
        val documentRef = firestore.collection("scans").document(documentId)
        val updateData = mapOf(
            "imageUrl" to downloadUrl,
            "result" to result,
            "userId" to (getUser()?.uid ?: "")
        )
        documentRef.update(updateData).await()
        return documentRef.get().await().toObject()
    }



    //TODO: Buat fungsi buat auto login pake Google... (agak susah, emang, iya...)
}
