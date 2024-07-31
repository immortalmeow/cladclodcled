package com.meow.clod.visualization

import android.graphics.*
import com.meow.clod.data.BodyPart
import com.meow.clod.data.Person
import kotlin.math.abs
import kotlin.math.max

object VisualizationUtils {
    private const val CIRCLE_RADIUS = 6f
    private const val LINE_WIDTH = 4f
    private const val PERSON_ID_TEXT_SIZE = 30f
    private const val PERSON_ID_MARGIN = 6f

    private val bodyJoints = listOf(
        Pair(BodyPart.NOSE, BodyPart.LEFT_EYE),
        Pair(BodyPart.NOSE, BodyPart.RIGHT_EYE),
        Pair(BodyPart.LEFT_EYE, BodyPart.LEFT_EAR),
        Pair(BodyPart.RIGHT_EYE, BodyPart.RIGHT_EAR),
        Pair(BodyPart.NOSE, BodyPart.LEFT_SHOULDER),
        Pair(BodyPart.NOSE, BodyPart.RIGHT_SHOULDER),
        Pair(BodyPart.LEFT_SHOULDER, BodyPart.LEFT_ELBOW),
        Pair(BodyPart.LEFT_ELBOW, BodyPart.LEFT_WRIST),
        Pair(BodyPart.RIGHT_SHOULDER, BodyPart.RIGHT_ELBOW),
        Pair(BodyPart.RIGHT_ELBOW, BodyPart.RIGHT_WRIST),
        Pair(BodyPart.LEFT_SHOULDER, BodyPart.RIGHT_SHOULDER),
        Pair(BodyPart.LEFT_SHOULDER, BodyPart.LEFT_HIP),
        Pair(BodyPart.RIGHT_SHOULDER, BodyPart.RIGHT_HIP),
        Pair(BodyPart.LEFT_HIP, BodyPart.RIGHT_HIP),
        Pair(BodyPart.LEFT_HIP, BodyPart.LEFT_KNEE),
        Pair(BodyPart.LEFT_KNEE, BodyPart.LEFT_ANKLE),
        Pair(BodyPart.RIGHT_HIP, BodyPart.RIGHT_KNEE),
        Pair(BodyPart.RIGHT_KNEE, BodyPart.RIGHT_ANKLE)
    )

    fun drawBodyKeypoints(
        input: Bitmap,
        persons: List<Person>,
        isTrackerEnabled: Boolean = false
    ): Bitmap {
        val paintCircle = Paint().apply {
            strokeWidth = CIRCLE_RADIUS
            color = Color.RED
            style = Paint.Style.FILL
        }
        val paintLine = Paint().apply {
            strokeWidth = LINE_WIDTH
            color = Color.RED
            style = Paint.Style.STROKE
        }
        val paintText = Paint().apply {
            textSize = PERSON_ID_TEXT_SIZE
            color = Color.RED
            textAlign = Paint.Align.LEFT
        }
        val paintSquat = Paint().apply {
            color = Color.GREEN
            style = Paint.Style.STROKE
            strokeWidth = 5f
        }
        val paintPushUp = Paint().apply {
            color = Color.BLUE
            style = Paint.Style.STROKE
            strokeWidth = 5f
        }

        val output = input.copy(Bitmap.Config.ARGB_8888, true)
        val originalSizeCanvas = Canvas(output)
        persons.forEach { person ->
            if (isTrackerEnabled) {
                person.boundingBox?.let { boundingBox ->
                    val personIdX = max(0f, boundingBox.left)
                    val personIdY = max(0f, boundingBox.top)

                    originalSizeCanvas.drawText(
                        person.id.toString(),
                        personIdX,
                        personIdY - PERSON_ID_MARGIN,
                        paintText
                    )
                    originalSizeCanvas.drawRect(boundingBox, paintLine)
                }
            }
            bodyJoints.forEach { joint ->
                val pointA = person.keyPoints[joint.first.position].coordinate
                val pointB = person.keyPoints[joint.second.position].coordinate
                originalSizeCanvas.drawLine(pointA.x, pointA.y, pointB.x, pointB.y, paintLine)
            }

            person.keyPoints.forEach { point ->
                originalSizeCanvas.drawCircle(
                    point.coordinate.x,
                    point.coordinate.y,
                    CIRCLE_RADIUS,
                    paintCircle
                )
            }

            when {
                isSquatPosition(person) -> {
                    val leftHip = person.keyPoints[BodyPart.LEFT_HIP.position].coordinate
                    val leftKnee = person.keyPoints[BodyPart.LEFT_KNEE.position].coordinate
                    val leftAnkle = person.keyPoints[BodyPart.LEFT_ANKLE.position].coordinate

                    originalSizeCanvas.drawLine(leftHip.x, leftHip.y, leftKnee.x, leftKnee.y, paintSquat)
                    originalSizeCanvas.drawLine(leftKnee.x, leftKnee.y, leftAnkle.x, leftAnkle.y, paintSquat)
                    originalSizeCanvas.drawText("Squat", leftHip.x, leftHip.y - 30f, paintText)
                }
                isPushUpPosition(person) -> {
                    val leftShoulder = person.keyPoints[BodyPart.LEFT_SHOULDER.position].coordinate
                    val leftElbow = person.keyPoints[BodyPart.LEFT_ELBOW.position].coordinate
                    val leftWrist = person.keyPoints[BodyPart.LEFT_WRIST.position].coordinate

                    originalSizeCanvas.drawLine(leftShoulder.x, leftShoulder.y, leftElbow.x, leftElbow.y, paintPushUp)
                    originalSizeCanvas.drawLine(leftElbow.x, leftElbow.y, leftWrist.x, leftWrist.y, paintPushUp)
                    originalSizeCanvas.drawText("Push-up", leftShoulder.x, leftShoulder.y - 30f, paintText)
                }
            }
        }
        return output
    }

    private fun isSquatPosition(person: Person): Boolean {
        val leftHip = person.keyPoints[BodyPart.LEFT_HIP.position].coordinate
        val leftKnee = person.keyPoints[BodyPart.LEFT_KNEE.position].coordinate
        val leftAnkle = person.keyPoints[BodyPart.LEFT_ANKLE.position].coordinate

        val angle = calculateAngle(leftHip, leftKnee, leftAnkle)
        return angle in 80.0..100.0
    }

    private fun isPushUpPosition(person: Person): Boolean {
        val leftShoulder = person.keyPoints[BodyPart.LEFT_SHOULDER.position].coordinate
        val leftElbow = person.keyPoints[BodyPart.LEFT_ELBOW.position].coordinate
        val leftWrist = person.keyPoints[BodyPart.LEFT_WRIST.position].coordinate

        val angle = calculateAngle(leftShoulder, leftElbow, leftWrist)
        return angle in 80.0..100.0
    }

    private fun calculateAngle(first: PointF, mid: PointF, last: PointF): Double {
        val angle = Math.toDegrees(
            Math.atan2((last.y - mid.y).toDouble(), (last.x - mid.x).toDouble()) -
                    Math.atan2((first.y - mid.y).toDouble(), (first.x - mid.x).toDouble())
        )
        return abs(angle)
    }
}
