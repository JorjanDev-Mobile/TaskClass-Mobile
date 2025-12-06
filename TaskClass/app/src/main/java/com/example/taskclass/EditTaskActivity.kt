package com.example.taskclass

import Controller.TaskController
import Entity.Course
import Entity.ImageData
import Entity.Task
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
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

class EditTaskActivity : AppCompatActivity() {

    private lateinit var taskController: TaskController
    private lateinit var currentTask: Task

    // Vistas
    private lateinit var etTitle: EditText
    private lateinit var etDescription: EditText
    private lateinit var etDueDate: EditText
    private lateinit var btnCalendar: ImageButton
    private lateinit var cbReminder: CheckBox
    private lateinit var tvCourseLabel: TextView
    private lateinit var btnAttachPhoto: ImageButton
    private lateinit var btnUpdate: Button
    private lateinit var btnDelete: ImageButton
    private lateinit var toolbar: androidx.appcompat.widget.Toolbar

    private var selectedDate: Date? = null
    private var selectedCourse: Course? = null
    private var currentTaskId: String = ""

    private var currentPhotoPath: String? = null
    private val takePhoto = registerForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap: Bitmap? ->
        if (bitmap != null) {
            val file = File(externalCacheDir, "IMG_${System.currentTimeMillis()}.jpg")
            FileOutputStream(file).use { out ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
            }

            currentTask.Images.add(ImageData(uri = file.toURI().toString()))
            Toast.makeText(this, "Photo taken", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "The photo could not be taken", Toast.LENGTH_SHORT).show()
        }
    }

    private val pickFromGallery = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            currentTask.Images.add(ImageData(uri = it.toString()))
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
        setContentView(R.layout.activity_edit_task)

        taskController = TaskController(this)
        currentTaskId = intent.getStringExtra("TASK_ID") ?: ""

        currentTask = taskController.getAll().find { it.Id == currentTaskId } ?: run {
            Toast.makeText(this, "Task not found", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        initViews()
        loadTaskData()
        setupClickListeners()

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

    private fun initViews() {
        etTitle = findViewById(R.id.et_title_add)
        etDescription = findViewById(R.id.et_description_add)
        etDueDate = findViewById(R.id.et_due_date_add)
        btnCalendar = findViewById(R.id.btn_calendar)
        cbReminder = findViewById(R.id.cb_reminder_add)
        tvCourseLabel = findViewById(R.id.tv_course_label)
        btnAttachPhoto = findViewById(R.id.btn_attach_photo)
        btnUpdate = findViewById(R.id.btn_update)
        btnDelete = findViewById(R.id.btn_delete_task)
        toolbar = findViewById(R.id.toolbar_edit)
        toolbar.setNavigationOnClickListener { finish() }
    }

    private fun loadTaskData() {
        etTitle.setText(currentTask.Title)
        etDescription.setText(currentTask.Description ?: "")
        selectedDate = currentTask.DueDate
        selectedCourse = currentTask.Course
        cbReminder.isChecked = currentTask.hasReminder

        selectedDate?.let { date ->
            val sdf = SimpleDateFormat("dd 'de' MMMM", Locale.forLanguageTag("es-ES"))
            etDueDate.setText(sdf.format(date))
        } ?: etDueDate.setText("")

        tvCourseLabel.text = selectedCourse?.name?.let { "Course: $it" } ?: "Tap to select course"
    }

    private fun setupClickListeners() {
        btnCalendar.setOnClickListener { showDatePicker() }
        tvCourseLabel.setOnClickListener { showCourseDialog() }

        btnAttachPhoto.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Attach photo")
                .setItems(arrayOf("Take photo", "From gallery", "Cancel")) { _, which ->
                    when (which) {
                        0 -> requestCameraPermission.launch(Manifest.permission.CAMERA)
                        1 -> requestGalleryPermission.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                    }
                }
                .show()
        }

        btnUpdate.setOnClickListener { confirmUpdate() }
        btnDelete.setOnClickListener { confirmDelete() }
    }

    private fun showDatePicker() {
        val cal = Calendar.getInstance()
        selectedDate?.let { cal.time = it }
        DatePickerDialog(this, { _, y, m, d ->
            cal.set(y, m, d)
            selectedDate = cal.time
            etDueDate.setText(SimpleDateFormat("dd 'de' MMMM", Locale.forLanguageTag("es-ES")).format(selectedDate!!))
        }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show()
    }

    private fun showCourseDialog() {
        val allTasks = taskController.getAll()
        val courses = allTasks.mapNotNull { it.Course }.distinctBy { it.id }
        val names = courses.map { it.name }.toMutableList().apply { add("Other...") }

        AlertDialog.Builder(this)
            .setTitle("Select the course")
            .setItems(names.toTypedArray()) { _, which ->
                if (names[which] == "Other...") showCustomCourseDialog()
                else {
                    selectedCourse = courses[which]
                    tvCourseLabel.text = "Course: ${names[which]}"
                }
            }.show()
    }

    private fun showCustomCourseDialog() {
        val input = EditText(this).apply { hint = "Name of course" }
        AlertDialog.Builder(this)
            .setTitle("Personalized course")
            .setView(input)
            .setPositiveButton("Accept") { _, _ ->
                val name = input.text.toString().trim()
                if (name.isNotEmpty()) {
                    selectedCourse = Course(UUID.randomUUID().toString(), name)
                    tvCourseLabel.text = "Course: $name"
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun confirmUpdate() {
        AlertDialog.Builder(this)
            .setTitle("Update Task")
            .setMessage("Are you sure you want to update this task?")
            .setPositiveButton("Accept") { _, _ -> updateTask() }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun confirmDelete() {
        AlertDialog.Builder(this)
            .setTitle("Delete Task")
            .setMessage("Are you sure you want to delete this task?\n" +
                    "This action cannot be undone.")
            .setPositiveButton("Delete") { _, _ -> deleteTask() }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun updateTask() {
        val title = etTitle.text.toString().trim()
        if (title.isEmpty()) {
            Toast.makeText(this, "The title cannot be empty.", Toast.LENGTH_SHORT).show()
            return
        }

        currentTask.apply {
            Title = title
            Description = etDescription.text.toString().takeIf { it.isNotBlank() } ?: ""
            DueDate = selectedDate
            Course = selectedCourse
            hasReminder = cbReminder.isChecked
        }

        taskController.update(currentTask)
        Toast.makeText(this, "Updated Task", Toast.LENGTH_LONG).show()
        setResult(RESULT_OK)
        finish()
    }

    private fun deleteTask() {
        taskController.delete(currentTask)
        Toast.makeText(this, "Deleted Task", Toast.LENGTH_LONG).show()
        setResult(RESULT_OK)
        finish()
    }
}