package edu.rosehulman.graderecorder.models

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Exclude

data class Student(
    var courseId: String="",
    var firstName: String="",
    var lastName: String="",
    var username: String="") {

    @get:Exclude
    var id: String? = null

    companion object {
        fun fromSnapshot(document: DocumentSnapshot): Student {
            val student = document.toObject(Student::class.java)!!
            student.id = document.id
            return student
        }
    }
}