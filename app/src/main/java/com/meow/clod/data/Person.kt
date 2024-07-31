package com.meow.clod.data

import android.graphics.PointF
import android.graphics.RectF

data class Person(
    val id: Int, // Tambahkan id
    val keyPoints: List<KeyPoint>,
    val score: Float,
    val boundingBox: RectF? = null // Tambahkan boundingBox
)

data class KeyPoint(val bodyPart: BodyPart, val coordinate: PointF, val score: Float)

enum class BodyPart(val position: Int) {
    NOSE(0), LEFT_EYE(1), RIGHT_EYE(2), LEFT_EAR(3), RIGHT_EAR(4),
    LEFT_SHOULDER(5), RIGHT_SHOULDER(6), LEFT_ELBOW(7), RIGHT_ELBOW(8),
    LEFT_WRIST(9), RIGHT_WRIST(10), LEFT_HIP(11), RIGHT_HIP(12),
    LEFT_KNEE(13), RIGHT_KNEE(14), LEFT_ANKLE(15), RIGHT_ANKLE(16)
}
