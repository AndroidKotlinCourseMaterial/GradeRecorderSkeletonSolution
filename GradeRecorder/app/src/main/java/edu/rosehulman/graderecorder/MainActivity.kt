package edu.rosehulman.graderecorder

import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v7.app.AppCompatActivity
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import edu.rosehulman.graderecorder.models.Assignment
import edu.rosehulman.graderecorder.models.Course
import edu.rosehulman.graderecorder.models.Owner
import edu.rosehulman.graderecorder.models.Student
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private val doInitialSetup = false

    private val baseRef = FirebaseFirestore.getInstance()
    private val courseRef = baseRef.collection("courses")
    private val assignmentRef = baseRef.collection("assignments")
    private val studentRef = baseRef.collection("students")
    private val ownerRef = baseRef.collection("owners")
    private val gradeEntryRef = baseRef.collection("gradeentries")

    private val courses = ArrayList<Course>()

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_home -> {
                getStudentsInCourse(idFromName("CSSE483"))
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_dashboard -> {
                // I know this is kind of dumb since I know the name. But
                // this method was called from a place where I did just have a
                // course id.
                getCourseNameForCourseId(idFromName("CSSE479"))
                // getAssignmentsInCourse(idFromName("CSSE374"))
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_notifications -> {
                // TODO: Use deep query.
                // TODO (later): Also add owner (must be both way)
                getCoursesForOwner2("boutell")
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        pushInitialCourses()

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
    }

    private fun getStudentsInCourse(courseId: String) {
        studentRef.whereEqualTo("courseId", courseId).get()
            .addOnSuccessListener { snapshot: QuerySnapshot ->
                var students = snapshot.toObjects(Student::class.java)
                message.text = students.toString()
            }
    }

    private fun getCourseNameForCourseId(courseId: String) {
        courseRef.document(courseId).get()
            .addOnSuccessListener { snapshot: DocumentSnapshot ->
                message.text = snapshot["name"] as String
            }
    }

    private fun getAssignmentsInCourse(courseId: String) {
        assignmentRef.whereEqualTo("courseId", courseId).get()
            .addOnSuccessListener { snapshot: QuerySnapshot ->
                var assignments = snapshot.toObjects(Student::class.java)
                message.text = assignments.toString()
            }
    }

    private fun addOwnerForCourse(ownerId: String, courseId: String) {
        ownerRef.document(ownerId).get()
            .addOnSuccessListener { snapshot: DocumentSnapshot ->
                val owner = Owner.fromSnapshot(snapshot)
                owner.addCourse(courseId)
                ownerRef.document(ownerId).set(owner)
            }
        courseRef.document(courseId).get()
            .addOnSuccessListener { snapshot: DocumentSnapshot ->
                val course = Course.fromSnapshot(snapshot)
                course.addOwner(ownerId)
                courseRef.document(courseId).set(course)
            }
    }

    fun getCoursesForOwner(ownerName: String) {
        var path = FieldPath.of("owners", ownerName)
        courseRef.whereEqualTo(path, true).get()
            .addOnSuccessListener { snapshot: QuerySnapshot ->
                var courses = snapshot.toObjects(Course::class.java)
                message.text = courses.toString()
            }
    }

    fun getCoursesForOwner2(ownerName: String) {
//        // Need to get owner.id for name. Sometimes this is done already.
          // If we need to get it, we can run into trouble. See definition of this
          // helper function below.
//        var ownerId = getOwnerIdForName(ownerName)
//        ownerRef.document(ownerName).get()
//            .addOnSuccessListener { snapshot: DocumentSnapshot ->
//                val courseIds = snapshot["courses"] as Map<String, Boolean>
//                val courses = ArrayList<Course>()
//                for (courseId in courseIds.keys) {
//                    courseRef.document(courseId).get()
//                        .addOnSuccessListener { courseSnapshot: DocumentSnapshot ->
//                            val course = Course.fromSnapshot(courseSnapshot)
//                            courses.add(course)
//                            message.text = courses.toString()
//                        }
//                }
//                // How will we know when all have been loaded?
//            }
    }

    // How do we calculate this?
//    private fun getOwnerIdForName(ownerName: String): String {
//        ownerRef.get().addOnSuccessListener { snapshot: QuerySnapshot ->
//            val owners = ArrayList<Owner>()
//            for (document in snapshot.documents) {
//                if (Owner.fromSnapshot(document).username == ownerName) {
//                    return document.id
//                }
//            }
//        }
//    }

    // Setup
    private fun pushInitialCourses() {
        if (doInitialSetup) {
            courseRef.add(Course("CSSE483", "boutell"))
            courseRef.add(Course("CSSE479", "chenette"))
            courseRef.add(Course("CSSE374", "hays"))
        }
        getCourses()
    }

    private fun getCourses() {
        courseRef.get().addOnSuccessListener { snapshot ->
            courses.clear()
            for (document in snapshot) {
                courses.add(Course.fromSnapshot(document))
            }
            addAssignmentsForCourses()
            addOwnersForCourses()
        }
    }

    private fun addAssignmentsForCourses() {
        if (doInitialSetup) {
            val courseIdForCSSE483 = idFromName("CSSE483")
            val courseIdForCSSE374 = idFromName("CSSE374")
            val courseIdForCSSE479 = idFromName("CSSE479")

            assignmentRef.add(Assignment(courseIdForCSSE483, "Lab1", 10.0))
            assignmentRef.add(Assignment(courseIdForCSSE483, "Exam1", 45.0))
            assignmentRef.add(Assignment(courseIdForCSSE483, "Layouts", 5.0))

            assignmentRef.add(Assignment(courseIdForCSSE374, "ReadingQuiz1", 10.0))
            assignmentRef.add(Assignment(courseIdForCSSE374, "Project M1 Design", 20.0))

            assignmentRef.add(Assignment(courseIdForCSSE479, "Vigenere", 100.0))
            assignmentRef.add(Assignment(courseIdForCSSE479, "AES", 80.0))

            studentRef.add(Student(courseIdForCSSE483, "Jianan", "Pang", "pangj"))
            studentRef.add(Student(courseIdForCSSE483, "Alex", "Dripchak", "dripchar"))

            studentRef.add(Student(courseIdForCSSE374, "Eugene", "Kim", "kime2"))
            studentRef.add(Student(courseIdForCSSE374, "Yang", "Gao", "gaoy2"))
            studentRef.add(Student(courseIdForCSSE374, "Matthew", "Lyons", "lyonsmj"))

            studentRef.add(Student(courseIdForCSSE479, "Jacob", "Gathof", "gathofjd"))
        }
    }

    private fun addOwnersForCourses() {
        if (doInitialSetup) {
            val boutell = Owner("boutell")
            boutell.addCourse(idFromName("CSSE483"))
            boutell.addCourse(idFromName("CSSE479"))
            ownerRef.add(boutell)

            // Owners and courses are 2-way, so if Boutell is an owner of 479,
            // we must record it in the owner (done above) and in the course:
            val csse479 = courseFromName("CSSE479")
            csse479.addOwner("boutell")
            courseRef.document(idFromName("CSSE479")).set(csse479)

            // Add other owners
            val chenette = Owner("chenette")
            chenette.addCourse(idFromName("CSSE479"))
            ownerRef.add(chenette)

            val hays = Owner("hays")
            hays.addCourse(idFromName("CSSE374"))
            ownerRef.add(hays)

            val austinin = Owner("austinin")
            austinin.addCourse(idFromName("CSSE483"))
            ownerRef.add(austinin)

            val lewistd = Owner("lewistd")
            lewistd.addCourse(idFromName("CSSE483"))
            ownerRef.add(lewistd)

            val csse483 = courseFromName("CSSE483")
            csse483.addOwner("austinin")
            csse483.addOwner("lewistd")
            courseRef.document(idFromName("CSSE483")).set(csse483)
        }
    }

    private fun idFromName(courseName: String): String {
        return courses.find { it.name == courseName }!!.id
    }

    private fun courseFromName(courseName: String): Course {
        return courses.find { it.name == courseName }!!
    }
}
