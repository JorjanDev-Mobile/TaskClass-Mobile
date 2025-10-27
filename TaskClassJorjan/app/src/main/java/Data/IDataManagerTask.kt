package Data

import Entity.Task
import Entity.TaskStatus

interface IDataManagerTask {
    fun add(task: Task)
    fun update(task: Task)
    fun delete(task: Task)
    fun getAll(): List<Task>
    fun getById(id: String): Task?
    fun getByCourse(courseId: String): List<Task>
    fun filterByStatus(status: TaskStatus): List<Task>

}