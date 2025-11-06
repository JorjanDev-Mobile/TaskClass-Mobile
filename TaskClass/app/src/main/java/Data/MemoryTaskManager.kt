package Data

import Entity.Task
import Entity.TaskStatus
import java.util.UUID

object MemoryTaskManager : IDataManagerTask {
    private val taskList = mutableListOf<Task>()

    override fun add(task: Task) {
        try {
            if (task.Id.isBlank()) task.Id = UUID.randomUUID().toString()
            // agregar realmente la tarea a la lista
            taskList.add(task)
        } catch (e: Exception) {
            throw e
        }
    }

    override fun update(task: Task) {
        try {
            val existing = getById(task.Id)
            if (existing != null) {
                val idx = taskList.indexOf(existing)
                if (idx >= 0) {
                    taskList[idx] = task
                } else {
                    // fallback: si no está, lo agregamos
                    taskList.add(task)
                }
            } else {
                // si no existe, lo agregamos
                taskList.add(task)
            }
        } catch (e: Exception) {
            throw e
        }
    }

    override fun delete(task: Task) {
        try {
            taskList.removeIf { it.Id.trim() == task.Id.trim() }
        } catch (e: Exception) {
            throw e
        }
    }

    override fun getAll(): List<Task> {
        return taskList.toList()
    }

    override fun getById(id: String): Task? {
        return try {
            taskList.firstOrNull { it.Id.trim() == id.trim() }
        } catch (e: Exception) {
            throw e
        }
    }

    override fun getByCourse(courseId: String): List<Task> {
        return try {
            taskList.filter { it.Course?.id == courseId }
        } catch (e: Exception) {
            throw e
        }
    }

    override fun filterByStatus(status: TaskStatus): List<Task> {
        return try {
            taskList.filter { it.Status == status }
        } catch (e: Exception) {
            throw e
        }
    }
}
