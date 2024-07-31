package com.meow.clod.ml

import android.content.Context
import android.util.Log
import com.meow.clod.data.Person
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.support.common.FileUtil

class PoseClassifier(context: Context) {
    private val interpreter: Interpreter
    private val labels: List<String>
    private val outputShape: IntArray

    init {
        val model = FileUtil.loadMappedFile(context, "pose_classifier.tflite")
        interpreter = Interpreter(model)
        labels = context.assets.open("labels.txt").bufferedReader().useLines { it.toList() }
        outputShape = interpreter.getOutputTensor(0).shape()
        Log.d("PoseClassifier", "Output shape: ${outputShape.contentToString()}")
    }

    fun classify(person: Person): String {
        try {
            val inputFeatures = person.keyPoints.flatMap { listOf(it.coordinate.x, it.coordinate.y, it.score) }.toFloatArray()

            // Create output buffer with correct shape
            val outputBuffer = Array(outputShape[0]) { FloatArray(outputShape[1]) }

            interpreter.run(arrayOf(inputFeatures), outputBuffer)

            // Get the first (and only) row of the output
            val scores = outputBuffer[0]

            // Find the index of the maximum score
            val maxIndex = scores.indices.maxByOrNull { scores[it] } ?: -1

            return if (maxIndex in labels.indices) labels[maxIndex] else "unknown"
        } catch (e: Exception) {
            Log.e("PoseClassifier", "Error classifying pose: ${e.message}", e)
            return "unknown"
        }
    }

    fun close() {
        interpreter.close()
    }
}