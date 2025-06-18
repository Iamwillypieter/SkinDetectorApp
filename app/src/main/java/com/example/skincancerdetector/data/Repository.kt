package com.example.skincancerdetector.data

import android.R.attr.bitmap
import android.graphics.Bitmap
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.google.firebase.ml.modeldownloader.CustomModel
import com.google.firebase.ml.modeldownloader.CustomModelDownloadConditions
import com.google.firebase.ml.modeldownloader.DownloadType
import com.google.firebase.ml.modeldownloader.FirebaseModelDownloader
import kotlinx.coroutines.tasks.await
import org.tensorflow.lite.DataType
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.ResizeOp
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.io.ByteArrayOutputStream
import java.io.File


class Repository() {

    private val firestore = Firebase.firestore

    private val scans = "scans"
    private val users = "user_data"
    private val disease = "diseases"
    private val auth = FirebaseAuth.getInstance()
    private var interpreter : Interpreter? = null

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
        return try {
            LocalStorageHelper.saveImage(image, fileName, userId)
        } catch (e: Exception) {
            throw e
        }
    }

    //Upload form data
    suspend fun createNewDocument(scanData: ScanData): String {
        val collectionRef = firestore.collection(scans)
        val documentRef = collectionRef.add(scanData).await()
        return documentRef.id
    }

    // Function to update a Firestore document with the download URL and analysis result
    suspend fun updateDocument(
        documentId: String,
        downloadUrl: String,
        result: Map<String, Float>,
        timestamp: String,
    ): ScanData? {
        val documentRef = firestore.collection(scans).document(documentId)
        val updateData = mapOf(
            "imageUrl" to downloadUrl,
            "result" to result,
            "userId" to (getUser()?.uid ?: ""),
            "timestamp" to timestamp
        )
        documentRef.update(updateData).await()
        return documentRef.get().await().toObject()
    }

    suspend fun deleteDocument(documentId: String){
        firestore.collection(scans).document(documentId).delete().await()
    }

    suspend fun getSpecificScanData(documentId: String):ScanData?{
        val documentRef = firestore.collection(scans).document(documentId)
        return documentRef.get().await().toObject()
    }

    fun downloadModel(
        onSuccess: (Interpreter?) -> Unit,
        onFailure: (Exception) -> Unit,
    ){
        val conditions = CustomModelDownloadConditions.Builder()
            // Also possible: .requireCharging() and .requireDeviceIdle()
            .build()
        FirebaseModelDownloader.getInstance()
            .getModel("Skin-Disease-Detector", DownloadType.LOCAL_MODEL_UPDATE_IN_BACKGROUND,
                conditions)
            .addOnSuccessListener { model: CustomModel? ->
                val modelFile = model?.file
                if (modelFile != null) {
                    interpreter = Interpreter(modelFile)
                    onSuccess(interpreter)
                }
                else
                    onFailure(Exception("Oh Nyooo~"))
            }
    }

    fun analyze(bitmap: Bitmap):FloatArray?{
        val input= processor(bitmap)
        Log.i("repo", "analyze")
        if(interpreter!=null && input!=null){
            return runInference(interpreter!!,input)
        }
        else return null

    }
    
    private fun processor(bitmap: Bitmap): TensorImage? {
        Log.i("repo", "processor")
        val imageProcessor = ImageProcessor.Builder()
            .add(ResizeOp(180, 180, ResizeOp.ResizeMethod.BILINEAR))
            .build()
        var tensorImage = TensorImage(DataType.FLOAT32)
        tensorImage.load(bitmap)
        return imageProcessor.process(tensorImage)
    }



    private fun runInference(interpreter: Interpreter, input: TensorImage): FloatArray {
        Log.i("repo", "inference")
        val outputShape = intArrayOf(1, 9)
        val outputType = DataType.FLOAT32
        val outputBuffer = TensorBuffer.createFixedSize(outputShape, outputType).buffer
        outputBuffer.rewind()

        interpreter.run(input.buffer, outputBuffer)

        outputBuffer.rewind()

        val outputArray = FloatArray(9)
        outputBuffer.asFloatBuffer().get(outputArray)

        return outputArray
    }


    suspend fun getAllUserScanData(): QuerySnapshot? {
        val userId = getUser()?.uid
        return if(userId!=null){
            val documentRef = firestore.collection(scans).whereEqualTo("userId",userId)
            documentRef.get().await()
        } else null
    }

    suspend fun getAllDiseases(): List<Disease> {
        val collectionRef = firestore.collection(disease)
        return collectionRef.get().await().map{it.toObject()}
    }
    //TODO: Buat fungsi buat auto login pake Google... (agak susah, emang, iya...)
}
