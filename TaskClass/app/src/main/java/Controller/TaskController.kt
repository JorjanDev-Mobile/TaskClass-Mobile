package Controller

import android.content.Context
import Data.IDataManagerTask
import Data.SharedPrefsTaskManager
import Entity.Course
import Entity.Task
import Entity.TaskStatus

class TaskController(private val context: Context) {
    private val dataManager: IDataManagerTask = SharedPrefsTaskManager(context)

    fun add(task: Task) {
        dataManager.add(task)
    }

    fun update(task: Task) {
        dataManager.update(task)
    }

    fun getAll(): List<Task> = dataManager.getAll()

    fun getAllCourses(): List<Course> {
        return getAll()
            .mapNotNull { it.Course }
            .distinctBy { it.id }
    }

    fun getTasksByCourse(courseName: String): List<Task> {
        return getAll().filter { it.Course?.name == courseName }
    }
}