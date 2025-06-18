package com.example.skincancerdetector.data

import android.content.Context
import android.graphics.Bitmap
import java.io.File
import java.io.FileOutputStream

object LocalStorageHelper {
    private lateinit var appContext: Context

    fun init(context: Context) {
        appContext = context.applicationContext
    }

    fun saveImage(bitmap: Bitmap, fileName: String, userId: String): String {
        val dir = File(appContext.filesDir, "UserData/$userId/Scans")
        if (!dir.exists()) dir.mkdirs()

        val file = File(dir, "$fileName.jpg")
        val outputStream = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
        outputStream.flush()
        outputStream.close()

        return file.absolutePath
    }
}
