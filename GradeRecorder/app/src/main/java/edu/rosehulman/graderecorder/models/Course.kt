package edu.rosehulman.graderecorder.models

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Exclude

class Course(var name: String="", uid: String="") {
    var owners = HashMap<String, Boolean>()
    @get:Exclude var id: String = ""

    init {
        owners[uid] = true
    }

    fun addOwner(username: String) {
        owners[username] = true
    }

    companion object {
        fun fromSnapshot(document: DocumentSnapshot): Course {
            val course = document.toObject(Course::class.java)!!
            course.id = document.id
            return course
        }
    }

    override fun toString(): String {
        return "name: $name, owners: $owners"
    }
}