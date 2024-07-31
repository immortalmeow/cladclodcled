package com.meow.clod.utils

import android.graphics.Bitmap
import android.graphics.Matrix

object ImageUtils {
    fun rotateBitmap(source: Bitmap, angle: Float): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(angle)
        return Bitmap.createBitmap(source, 0, 0, source.width, source.height, matrix, true)
    }
}