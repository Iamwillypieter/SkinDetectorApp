package com.example.skincancerdetector.model
//
//import android.content.Context
//import android.graphics.Bitmap
//import android.graphics.BitmapFactory
//import android.net.Uri
//import org.tensorflow.lite.Interpreter
//import java.io.FileInputStream
//import java.nio.ByteBuffer
//import java.nio.ByteOrder
//import java.nio.channels.FileChannel
//
//class ImageClassifier(private val context: Context) {
//
//    private lateinit var interpreter: Interpreter
//
//    init {
//        loadModel("model_unquant.tflite")
//    }
//
//    fun loadModel(modelFilename: String) {
//        val fileDescriptor = context.assets.openFd(modelFilename)
//        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
//        val modelBuffer = inputStream.channel.map(
//            FileChannel.MapMode.READ_ONLY,
//            fileDescriptor.startOffset,
//            fileDescriptor.declaredLength
//        )
//        interpreter = Interpreter(modelBuffer)
//    }
//
//    fun classifyImage(bitmap: Bitmap): Map<String, Float> {
//        val inputBuffer = ByteBuffer.allocateDirect(1 * IMAGE_SIZE * IMAGE_SIZE * 3 * 4)
//        inputBuffer.order(ByteOrder.nativeOrder())
//
//        val pixels = IntArray(IMAGE_SIZE * IMAGE_SIZE)
//        bitmap.getPixels(pixels, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)
//
//        var pixel = 0
//        for (i in 0 until IMAGE_SIZE) {
//            for (j in 0 until IMAGE_SIZE) {
//                val pixelValue = pixels[pixel++]
//
//                inputBuffer.putFloat(((pixelValue shr 16) and 0xFF) / 255f)
//                inputBuffer.putFloat(((pixelValue shr 8) and 0xFF) / 255f)
//                inputBuffer.putFloat((pixelValue and 0xFF) / 255f)
//            }
//        }
//
//        val outputBuffer = ByteBuffer.allocateDirect(1 * OUTPUT_CLASSES * 4)
//        outputBuffer.order(ByteOrder.nativeOrder())
//
//        interpreter.run(inputBuffer, outputBuffer)
//
//        outputBuffer.rewind()
//        val probabilities = FloatArray(OUTPUT_CLASSES)
//        outputBuffer.asFloatBuffer().get(probabilities)
//
//        val results = mutableMapOf<String, Float>()
//        for (i in probabilities.indices) {
//            results[LABELS[i]] = probabilities[i]
//        }
//
//        return results
//    }
//
//    companion object {
//        private const val IMAGE_SIZE = 224
//        private const val OUTPUT_CLASSES = 9
//        private val LABELS = arrayOf("Mel", "AK", "UNK", "VASC", "BKL", "NV", "BCC", "DF", "SCC")
//    }
//
//}