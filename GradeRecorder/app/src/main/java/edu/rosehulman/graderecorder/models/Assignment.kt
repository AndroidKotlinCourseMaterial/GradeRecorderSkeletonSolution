package edu.rosehulman.graderecorder.models

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Exclude

data class Assignment(
    var courseId: String="",
    var name: String="",
    var maxGrade: Double=0.0) {

    @get:Exclude var id: String? = null

    companion object {
        fun fromSnapshot(document: DocumentSnapshot): Assignment {
            val assignment = document.toObject(Assignment::class.java)!!
            assignment.id = document.id
            return assignment
        }
    }

}