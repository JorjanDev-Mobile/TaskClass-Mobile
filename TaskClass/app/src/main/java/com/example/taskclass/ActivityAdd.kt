package com.example.taskclass

import Controller.TaskController
import Entity.Course
import Entity.Task
import Entity.TaskStatus
import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.*

class ActivityAdd : AppCompatActivity() {

    private lateinit var etTitle: EditText
    private lateinit var etDueDate: EditText
    private lateinit var btnCalendar: ImageButton
    private lateinit var tvCourse: TextView
    private lateinit var btnSave: Button
    private lateinit var btnBack: ImageButton

    private lateinit var taskController: TaskController
    private var selectedDate: Date? = null
    private var selectedCourse: Course? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add)

        etTitle = findViewById(R.id.et_title_add)
        etDueDate = findViewById(R.id.et_due_date_add)
        btnCalendar = findViewById(R.id.btn_calendar)
        tvCourse = findViewById(R.id.tv_course_label)
        btnSave = findViewById(R.id.btn_save_add)
        btnBack = findViewById(R.id.btn_back)

        taskController = TaskController(this)

        setupCalendar()
        tvCourse.setOnClickListener { showCourseDialog() }
        btnSave.setOnClickListener { saveTask() }

        btnBack.setOnClickListener {
            finish()
        }
    }

    private fun setupCalendar() {
        btnCalendar.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
                val cal = Calendar.getInstance()
                cal.set(selectedYear, selectedMonth, selectedDay)
                selectedDate = cal.time

                val format = SimpleDateFormat("MMM dd", Locale.ENGLISH)
                etDueDate.setText(format.format(selectedDate!!))
            }, year, month, day).show()
        }
    }

    private fun showCourseDialog() {
        val allTasks = taskController.getAll()
        val courses = allTasks.mapNotNull { it.Course }.distinctBy { it.id }
        val courseNames = courses.map { it.name }.toMutableList()
        courseNames.add("Other...")

        AlertDialog.Builder(this)
            .setTitle("Select a course")
            .setItems(courseNames.toTypedArray()) { _, which ->
                val name = courseNames[which]
                if (name == "Other...") {
                    showCustomCourseDialog()
                } else {
                    selectedCourse = courses.find { it.name == name }
                    tvCourse.text = "Course: $name"
                }
            }
            .show()
    }

    private fun showCustomCourseDialog() {
        val input = EditText(this).apply { hint = "Course name" }
        AlertDialog.Builder(this)
            .setTitle("Custom course")
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
        }

        taskController.add(task)
        Toast.makeText(this, "Task saved", Toast.LENGTH_SHORT).show()
        setResult(RESULT_OK)
        finish()
    }
}