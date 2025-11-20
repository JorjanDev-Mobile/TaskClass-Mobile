package Controller

import Data.IDataManagerTask
import Data.SharedPrefsTaskManager
import Entity.Course
import Entity.Task
import android.content.Context

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

    fun delete(task: Task) {
        val updatedList = dataManager.getAll().filter { it.Id != task.Id }
        (dataManager as SharedPrefsTaskManager).saveAll(updatedList)
    }
}