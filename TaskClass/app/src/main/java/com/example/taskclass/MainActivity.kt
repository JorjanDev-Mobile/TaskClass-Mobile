package com.example.taskclass

import Controller.TaskController
import Entity.Task
import Entity.TaskStatus
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerTasks: RecyclerView
    private lateinit var btnAllCourses: Button
    private lateinit var btnCourse1: Button
    private lateinit var btnCourse2: Button
    private lateinit var btnAddWork: Button
    private lateinit var tvEmpty: TextView

    private lateinit var taskController: TaskController
    private lateinit var taskAdapter: TaskAdapter
    private val allTasks = mutableListOf<Task>()

    private val addTaskLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            refreshAll()
        }
    }

    private val editTaskLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            loadTasks()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerTasks = findViewById(R.id.recycler_tasks)
        btnAllCourses = findViewById(R.id.btnAllCourse_main)
        btnCourse1 = findViewById(R.id.btnCourse1_main)
        btnCourse2 = findViewById(R.id.btnCourse2_main)
        btnAddWork = findViewById(R.id.addWork_main)
        tvEmpty = findViewById(R.id.tv_empty)

        taskController = TaskController(this)

        recyclerTasks.layoutManager = LinearLayoutManager(this)
        taskAdapter = TaskAdapter(
            tasks = allTasks,
            onTaskClick = { task ->
                val intent = Intent(this@MainActivity, EditTaskActivity::class.java)
                intent.putExtra("TASK_ID", task.Id)
                editTaskLauncher.launch(intent)
            },
            onCompleteToggle = { task, isCompleted ->
                task.Status = if (isCompleted) TaskStatus.COMPLETED else TaskStatus.PENDING
                taskController.update(task)
                loadTasks()
            },
            onDeliveredToggle = { task, isChecked ->
                task.Delivered = isChecked
                taskController.update(task)
                loadTasks()
            }
        )

        findViewById<RecyclerView>(R.id.recycler_tasks).adapter = taskAdapter

        refreshAll()

        btnAddWork.setOnClickListener {
            addTaskLauncher.launch(Intent(this, ActivityAdd::class.java))
        }

        btnAllCourses.setOnClickListener { filterTasksByCourse("All") }
        btnCourse1.setOnClickListener { filterTasksByCourse(btnCourse1.text.toString()) }
        btnCourse2.setOnClickListener { filterTasksByCourse(btnCourse2.text.toString()) }
    }

    private fun refreshAll() {
        loadTasks()
        loadCourses()
    }

    private fun loadTasks(){
        val tasks = taskController.getAll()
        allTasks.clear()
        allTasks.addAll(tasks)

        taskAdapter.updateTasks(allTasks)

        tvEmpty.visibility = if (tasks.isEmpty()) View.VISIBLE else View.GONE
        recyclerTasks.visibility = if (tasks.isEmpty()) View.GONE else View.VISIBLE
    }

    private fun loadCourses() {
        val courses = taskController.getAllCourses()
        val courseNames = mutableListOf("All")
        courseNames.addAll(courses.map { it.name })

        btnAllCourses.text = courseNames[0]
        btnCourse1.text = courseNames.getOrNull(1) ?: "Course 1"
        btnCourse2.text = courseNames.getOrNull(2) ?: "Course 2"
    }

    private fun filterTasksByCourse(courseName: String) {
        val filtered = if (courseName == "All") {
            taskController.getAll()
        } else {
            taskController.getTasksByCourse(courseName)
        }
        taskAdapter.updateTasks(filtered)
    }
}