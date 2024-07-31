package com.meow.clod.ml

import android.graphics.PointF
import com.meow.clod.data.BodyPart
import com.meow.clod.data.Person
import kotlin.math.absoluteValue

class RepCounter {
    private var repCount = 0
    private var isDown = false

    fun countRep(pose: String, person: Person): Int {
        when (pose) {
            "squat" -> {
                val kneeAngle = calculateKneeAngle(person)
                if (kneeAngle < 90 && !isDown) {
                    isDown = true
                } else if (kneeAngle > 160 && isDown) {
                    isDown = false
                    repCount++
                }
            }
            "push_up" -> {
                val elbowAngle = calculateElbowAngle(person)
                if (elbowAngle < 90 && !isDown) {
                    isDown = true
                } else if (elbowAngle > 160 && isDown) {
                    isDown = false
                    repCount++
                }
            }
        }
        return repCount
    }

    private fun calculateKneeAngle(person: Person): Double {
        val hip = person.keyPoints.find { it.bodyPart == BodyPart.RIGHT_HIP }?.coordinate
        val knee = person.keyPoints.find { it.bodyPart == BodyPart.RIGHT_KNEE }?.coordinate
        val ankle = person.keyPoints.find { it.bodyPart == BodyPart.RIGHT_ANKLE }?.coordinate

        return calculateAngle(hip, knee, ankle)
    }

    private fun calculateElbowAngle(person: Person): Double {
        val shoulder = person.keyPoints.find { it.bodyPart == BodyPart.RIGHT_SHOULDER }?.coordinate
        val elbow = person.keyPoints.find { it.bodyPart == BodyPart.RIGHT_ELBOW }?.coordinate
        val wrist = person.keyPoints.find { it.bodyPart == BodyPart.RIGHT_WRIST }?.coordinate

        return calculateAngle(shoulder, elbow, wrist)
    }

    private fun calculateAngle(first: PointF?, middle: PointF?, last: PointF?): Double {
        if (first == null || middle == null || last == null) return 0.0

        val angle = Math.toDegrees(
            kotlin.math.atan2((last.y - middle.y).toDouble(), (last.x - middle.x).toDouble()) -
                    kotlin.math.atan2((first.y - middle.y).toDouble(), (first.x - middle.x).toDouble())
        ).absoluteValue

        return if (angle > 180.0) 360.0 - angle else angle
    }
}