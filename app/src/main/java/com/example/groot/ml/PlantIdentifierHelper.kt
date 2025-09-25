package com.example.groot.ml

import android.content.Context
import android.graphics.Bitmap
import org.tensorflow.lite.Interpreter
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel

class PlantIdentifierHelper(
    private val context: Context,
    private val onResult: (String) -> Unit
) {
    private var interpreter: Interpreter? = null
    private val inputSize = 128 // match model input (128x128)

    init {
        loadModel()
    }

    private fun loadModel() {
        try {
            val assetFileDescriptor = context.assets.openFd("plant_model_compatible.tflite")
            val fileInputStream = assetFileDescriptor.createInputStream()
            val fileChannel = fileInputStream.channel
            val startOffset = assetFileDescriptor.startOffset
            val declaredLength = assetFileDescriptor.declaredLength
            val modelBuffer: MappedByteBuffer =
                fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)

            interpreter = Interpreter(modelBuffer)
        } catch (e: Exception) {
            onResult("Error loading model: ${e.message}")
        }
    }

    // Convert Bitmap → ByteBuffer (normalized floats)
    // Convert Bitmap → ByteBuffer (normalized floats)
    private fun bitmapToByteBuffer(bitmap: Bitmap): ByteBuffer {
        val byteBuffer =
            ByteBuffer.allocateDirect(4 * inputSize * inputSize * 3) // float32 = 4 bytes
        byteBuffer.order(ByteOrder.nativeOrder())

        // Ensure bitmap is mutable & ARGB_8888
        val safeBitmap = if (bitmap.config != Bitmap.Config.ARGB_8888) {
            bitmap.copy(Bitmap.Config.ARGB_8888, true)
        } else {
            bitmap
        }

        val scaledBitmap = Bitmap.createScaledBitmap(safeBitmap, inputSize, inputSize, true)
        val intValues = IntArray(inputSize * inputSize)
        scaledBitmap.getPixels(intValues, 0, scaledBitmap.width, 0, 0,
            scaledBitmap.width, scaledBitmap.height)

        var pixel = 0
        for (i in 0 until inputSize) {
            for (j in 0 until inputSize) {
                val value = intValues[pixel++]

                // Normalize to [0,1]
                val r = ((value shr 16) and 0xFF) / 255.0f
                val g = ((value shr 8) and 0xFF) / 255.0f
                val b = (value and 0xFF) / 255.0f

                byteBuffer.putFloat(r)
                byteBuffer.putFloat(g)
                byteBuffer.putFloat(b)
            }
        }

        return byteBuffer
    }


    fun classify(input: Bitmap) {
        if (interpreter == null) {
            onResult("Interpreter not initialized.")
            return
        }

        try {
            val inputBuffer = bitmapToByteBuffer(input)
            val outputBuffer = Array(1) { FloatArray(30) } // 30 classes

            interpreter!!.run(inputBuffer, outputBuffer)

            val (maxIdx, maxScore) = outputBuffer[0]
                .withIndex()
                .maxByOrNull { it.value }!!

            onResult("Predicted class $maxIdx (confidence ${(maxScore * 100).toInt()}%)")
        } catch (e: Exception) {
            onResult("Error during classification: ${e.message}")
        }
    }
}
