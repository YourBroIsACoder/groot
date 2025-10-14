package com.example.groot.ml

import android.content.Context
import android.graphics.Bitmap
import android.util.Log // <-- MAKE SURE THIS IMPORT IS HERE
import org.tensorflow.lite.DataType
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.support.common.FileUtil
import org.tensorflow.lite.support.common.ops.NormalizeOp
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.ResizeOp
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.io.IOException
import java.nio.MappedByteBuffer

// --- ADD A TAG FOR OUR LOGS ---
private const val TAG = "PlantIdentifierHelper"

class PlantIdentifierHelper(
    private val context: Context,
    private val onResult: (String) -> Unit
) {
    private val confidenceThreshold = 0.60f
    private val labels: List<String>
    private var interpreter: Interpreter? = null

    private val modelInputWidth = 224
    private val modelInputHeight = 224

    init {
        labels = try {
            val labelList = context.assets.open("labels.txt").bufferedReader().useLines { it.toList() }
            Log.d(TAG, "Labels loaded successfully. Found ${labelList.size} labels.") // LOG 1
            labelList
        } catch (e: IOException) {
            Log.e(TAG, "Error reading labels.txt", e)
            onResult("Error: Could not read labels file.")
            emptyList()
        }

        try {
            val model: MappedByteBuffer = FileUtil.loadMappedFile(context, "plant_model_final.tflite")
            val options = Interpreter.Options().apply { setNumThreads(4) }
            interpreter = Interpreter(model, options)
            Log.d(TAG, "TFLite Interpreter initialized successfully.") // LOG 2
        } catch (e: Exception) {
            Log.e(TAG, "Error initializing TFLite Interpreter.", e)
            onResult("Error: Could not load the ML model.")
        }
    }

    fun classify(bitmap: Bitmap) {
        if (interpreter == null || labels.isEmpty()) {
            Log.e(TAG, "Classifier not initialized, aborting classification.") // LOG 3
            onResult("Classifier not initialized. Please restart the app.")
            return
        }

        Log.d(TAG, "Starting classification for bitmap: ${bitmap.width}x${bitmap.height}") // LOG 4

        // 1. Resize and Normalize the input image
        val imageProcessor = ImageProcessor.Builder()
            .add(ResizeOp(modelInputHeight, modelInputWidth, ResizeOp.ResizeMethod.BILINEAR))
            .add(NormalizeOp(127.5f, 127.5f))
            .build()

        var tensorImage = TensorImage(DataType.FLOAT32)
        tensorImage.load(bitmap)
        tensorImage = imageProcessor.process(tensorImage)
        Log.d(TAG, "Image processed to ${tensorImage.width}x${tensorImage.height}") // LOG 5

        // 2. Prepare the output buffer
        val outputBuffer = TensorBuffer.createFixedSize(intArrayOf(1, labels.size), DataType.FLOAT32)

        // 3. Run inference
        try {
            interpreter?.run(tensorImage.buffer, outputBuffer.buffer.rewind())
        } catch (e: Exception) {
            Log.e(TAG, "Error running model inference.", e)
            onResult("Error processing image.")
            return
        }

        // 4. Process the output
        val outputArray = outputBuffer.floatArray
        // --- THIS IS THE MOST IMPORTANT LOG ---
        Log.d(TAG, "Raw output array (first 5 values): ${outputArray.take(5).joinToString()}") // LOG 6

        var maxIndex = -1
        var maxConfidence = 0f
        outputArray.forEachIndexed { index, confidence ->
            if (confidence > maxConfidence) {
                maxConfidence = confidence
                maxIndex = index
            }
        }

        Log.d(TAG, "Highest confidence is $maxConfidence at index $maxIndex") // LOG 7

        if (maxConfidence < confidenceThreshold) {
            Log.d(TAG, "Confidence ($maxConfidence) is below threshold ($confidenceThreshold). Rejecting.") // LOG 8
            onResult("Could not confidently identify this plant.\n\nPlease try with a clearer image.")
            return
        }

        val plantName = labels.getOrElse(maxIndex) { "Unknown Class ($maxIndex)" }

        Log.d(TAG, "Final Result: $plantName with confidence $maxConfidence") // LOG 9

        val resultText = "I think this is a... \n${plantName.replaceFirstChar { it.titlecase() }}\n(Confidence: ${"%.1f".format(maxConfidence * 100)}%)"
        onResult(resultText)
    }
}