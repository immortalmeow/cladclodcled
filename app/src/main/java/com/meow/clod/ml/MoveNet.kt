package com.meow.clod.ml

import android.content.Context
import android.graphics.Bitmap
import android.graphics.PointF
import android.util.Log
import com.meow.clod.data.BodyPart
import com.meow.clod.data.KeyPoint
import com.meow.clod.data.Person
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.support.common.FileUtil
import org.tensorflow.lite.support.image.TensorImage

class MoveNet(context: Context) {
    private val interpreter: Interpreter
    private val imageSizeX: Int
    private val imageSizeY: Int

    init {
        val model = FileUtil.loadMappedFile(context, "movenet_thunder.tflite")
        interpreter = Interpreter(model)
        imageSizeX = interpreter.getInputTensor(0).shape()[1]
        imageSizeY = interpreter.getInputTensor(0).shape()[2]
    }

    fun estimatePoses(bitmap: Bitmap): List<Person> {
        try {
            val resizedBitmap = Bitmap.createScaledBitmap(bitmap, imageSizeX, imageSizeY, true)
            val tensorImage = TensorImage.fromBitmap(resizedBitmap)

            val outputShape = interpreter.getOutputTensor(0).shape()
            Log.d("MoveNet", "Output shape: ${outputShape.contentToString()}")

            val outputBuffer = Array(outputShape[0]) { Array(outputShape[1]) { Array(outputShape[2]) { FloatArray(outputShape[3]) } } }

            interpreter.run(tensorImage.buffer, outputBuffer)

            val keyPoints = mutableListOf<KeyPoint>()
            for (i in 0 until outputShape[2]) {
                val y = outputBuffer[0][0][i][0] * bitmap.height
                val x = outputBuffer[0][0][i][1] * bitmap.width
                val score = outputBuffer[0][0][i][2]
                keyPoints.add(KeyPoint(BodyPart.values()[i], PointF(x, y), score))
            }

            return listOf(Person(id = 0, keyPoints = keyPoints, score = keyPoints.map { it.score }.average().toFloat()))
        } catch (e: Exception) {
            Log.e("MoveNet", "Error estimating poses: ${e.message}", e)
            return emptyList()
        }
    }

    fun close() {
        interpreter.close()
    }

    companion object {
        private const val TAG = "MoveNet"
    }
}
