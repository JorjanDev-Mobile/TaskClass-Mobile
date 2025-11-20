package com.example.taskclass

import Controller.TaskController
import Entity.Course
import Entity.ImageData
import Entity.Task
import Entity.TaskStatus
import android.Manifest
import android.app.DatePickerDialog
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

class ActivityAdd : AppCompatActivity() {

    private lateinit var etTitle: EditText
    private lateinit var etDueDate: EditText
    private lateinit var btnCalendar: ImageButton
    private lateinit var tvCourse: TextView
    private lateinit var btnSave: Button
    private lateinit var btnBack: ImageButton
    private lateinit var btnAttachPhoto: ImageButton
    private lateinit var taskController: TaskController
    private var selectedDate: Date? = null
    private var selectedCourse: Course? = null
    private val tempImages = mutableListOf<ImageData>()

    private val takePhoto = registerForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap: Bitmap? ->
        if (bitmap != null) {
            val file = File(externalCacheDir, "IMG_${System.currentTimeMillis()}.jpg")
            FileOutputStream(file).use { out ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
            }

            tempImages.add(ImageData(uri = file.toURI().toString()))
            Toast.makeText(this, "Photo taken", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "The photo could not be taken.", Toast.LENGTH_SHORT).show()
        }
    }
    private val pickFromGallery = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            tempImages.add(ImageData(uri = it.toString()))
            Toast.makeText(this, "Gallery photo added", Toast.LENGTH_SHORT).show()
        }
    }
    private val requestCameraPermission = registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if (granted) {
            takePhoto.launch(null)
        } else {
            Toast.makeText(this, "Camera permit required", Toast.LENGTH_LONG).show()
        }
    }
    private val requestGalleryPermission = registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if (granted) pickFromGallery.launch("image/*")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add)

        etTitle = findViewById(R.id.et_title_add)
        etDueDate = findViewById(R.id.et_due_date_add)
        btnCalendar = findViewById(R.id.btn_calendar)
        tvCourse = findViewById(R.id.tv_course_label)
        btnSave = findViewById(R.id.btn_save_add)
        btnBack = findViewById(R.id.btn_back)
        btnAttachPhoto = findViewById(R.id.btn_attach_photo)
        taskController = TaskController(this)

        setupCalendar()
        tvCourse.setOnClickListener { showCourseDialog() }
        btnSave.setOnClickListener { saveTask() }
        btnBack.setOnClickListener { finish() }

        btnAttachPhoto.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Attach photo")
                .setItems(arrayOf("Camera", "Gallery", "Cancel")) { _, which ->
                    when (which) {
                        0 -> requestCameraPermission.launch(Manifest.permission.CAMERA)
                        1 -> requestGalleryPermission.launch(
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
                                Manifest.permission.READ_MEDIA_IMAGES
                            else
                                Manifest.permission.READ_EXTERNAL_STORAGE
                        )
                    }
                }
                .show()
        }
    }

    private fun setupCalendar() {
        btnCalendar.setOnClickListener {
            val calendar = Calendar.getInstance()
            DatePickerDialog(this, { _, y, m, d ->
                val cal = Calendar.getInstance().apply { set(y, m, d) }
                selectedDate = cal.time
                etDueDate.setText(SimpleDateFormat("MMM dd", Locale.ENGLISH).format(selectedDate!!))
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
        }
    }

    private fun showCourseDialog() {
        val allTasks = taskController.getAll()
        val courses = allTasks.mapNotNull { it.Course }.distinctBy { it.id }
        val names = courses.map { it.name }.toMutableList().apply { add("Other...") }

        AlertDialog.Builder(this)
            .setTitle("Select course")
            .setItems(names.toTypedArray()) { _, which ->
                if (names[which] == "Other...") showCustomCourseDialog()
                else {
                    selectedCourse = courses[which]
                    tvCourse.text = "Course: ${names[which]}"
                }
            }.show()
    }

    private fun showCustomCourseDialog() {
        val input = EditText(this).apply { hint = "Name of the course" }
        AlertDialog.Builder(this)
            .setTitle("Personalized course")
            .setView(input)
            .setPositiveButton("Accept") { _, _ ->
                val name = input.text.toString().trim()
                if (name.isNotEmpty()) {
                    selectedCourse = Course(UUID.randomUUID().toString(), name)
                    tvCourse.text = "Course: $name"
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun saveTask() {
        val title = etTitle.text.toString().trim()
        if (title.isEmpty()) {
            Toast.makeText(this, "Enter a title", Toast.LENGTH_SHORT).show()
            return
        }

        val task = Task().apply {
            Id = UUID.randomUUID().toString()
            Title = title
            DueDate = selectedDate
            Course = selectedCourse
            Status = TaskStatus.PENDING
            Delivered = false
            Images = tempImages.toMutableList()
        }
        task.Images = tempImages.toMutableList()

        taskController.add(task)
        Toast.makeText(this, "Saved task", Toast.LENGTH_SHORT).show()
        setResult(RESULT_OK)
        finish()
    }
}