package edu.rosehulman.graderecorder.models

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Exclude

data class Owner(var username: String="") {
    @get:Exclude var id: String = ""
    var courses = HashMap<String, Boolean>()

    fun addCourse(courseId: String) {
        courses[courseId] = true
    }

    fun containsCourse(courseKey: String): Boolean {
        return courses.containsKey(courseKey)
    }

    companion object {
        fun fromSnapshot(document: DocumentSnapshot): Owner {
            val owner = document.toObject(Owner::class.java)!!
            owner.id = document.id
            return owner
        }
    }
}