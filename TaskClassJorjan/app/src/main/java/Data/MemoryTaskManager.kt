package Data

import Entity.Task
import Entity.TaskStatus
import java.util.UUID
import kotlin.collections.mutableListOf

object MemoryTaskManager: IDataManagerTask {
    private var taskList = mutableListOf<Task>()

    override fun add(task: Task){
        try {
            if (task.Id.isBlank()) task.Id = UUID.randomUUID().toString()
        }catch (e: Exception){
            throw  e
        }
    }

    override fun update(task: Task){
        try {
            val existing = getById(task.Id)
            if (existing != null){
                taskList.remove(existing)
            }
        }catch (e: Exception){
            throw e
        }
    }

    override fun delete(task: Task){
        try{
            taskList.removeIf { it.Id.trim() == task.Id.trim()}
        }catch (e: Exception){
            throw e
        }
    }

    override fun getAll(): List<Task>{
        return taskList
    }

    override fun getById(id: String): Task?{
        try {
            val result = taskList.filter { it.Id.trim() == id.trim() }
            return if (result.any()) result[0] else null
        }catch (e: Exception){
            throw e
        }
    }

    override fun getByCourse(courseId: String): List<Task> {
        try {
            return taskList.filter { it.Course?.id == courseId }
        }catch (e: Exception){
            throw e
        }
    }

    override fun filterByStatus(status: TaskStatus): List<Task> {
        try {
            return taskList.filter { it.Status == status }
        } catch (e: Exception) {
            throw e
        }
    }
}