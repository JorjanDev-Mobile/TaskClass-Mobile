package Data

import Entity.Task
import Entity.TaskStatus
import Entity.Course
import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SharedPrefsTaskManager(private val context: Context) : IDataManagerTask {

    private val prefs = context.getSharedPreferences("TaskClassPrefs", Context.MODE_PRIVATE)
    private val gson = Gson()
    private val KEY_TASKS = "tasks_list"

    override fun add(task: Task) {
        val tasks = getAll().toMutableList()
        if (task.Id.isBlank()) task.Id = System.currentTimeMillis().toString()
        tasks.add(task)
        saveTasks(tasks)
    }

    override fun update(task: Task) {
        val tasks = getAll().toMutableList()
        val index = tasks.indexOfFirst { it.Id == task.Id }
        if (index != -1) {
            tasks[index] = task
            saveTasks(tasks)
        }
    }

    override fun delete(task: Task) {
        val tasks = getAll().toMutableList()
        tasks.removeAll { it.Id == task.Id }
        saveTasks(tasks)
    }

    override fun getAll(): List<Task> {
        val json = prefs.getString(KEY_TASKS, null) ?: return emptyList()
        return try {
            gson.fromJson(json, object : TypeToken<List<Task>>() {}.type) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    override fun getById(id: String): Task? = getAll().find { it.Id == id }
    override fun getByCourse(courseId: String): List<Task> = getAll().filter { it.Course?.id == courseId }
    override fun filterByStatus(status: TaskStatus): List<Task> = getAll().filter { it.Status == status }

    private fun saveTasks(tasks: List<Task>) {
        val json = gson.toJson(tasks)
        prefs.edit().putString(KEY_TASKS, json).apply()
    }
}