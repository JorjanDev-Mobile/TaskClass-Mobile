package Controller


import Data.IDataManagerTask
import Data.MemoryTaskManager
import Entity.Task
import Entity.TaskStatus
import android.content.Context
import com.example.taskclassjorjan.R
import Util.NotificationUtil

class TaskController {

    private var dataManager: IDataManagerTask = MemoryTaskManager
    private var context: Context

    constructor(context: Context) {
        this.context = context
    }

    fun add(task: Task) {
        try {
            dataManager.add(task)
            // programar recordatorio si aplica
            if (task.DueDate != null) {
                NotificationUtil.scheduleReminder(context, task)
                task.ReminderScheduled = true
            }
        } catch (e: Exception) {
            throw Exception(context.getString(R.string.ErrorMsgAddTask)) // agrega este string
        }
    }

    fun update(task: Task) {
        try {
            dataManager.update(task)
            // reprogramar o cancelar recordatorio
            if (task.DueDate != null) {
                NotificationUtil.scheduleReminder(context, task)
                task.ReminderScheduled = true
            } else {
                NotificationUtil.cancelReminder(context, task.Id)
                task.ReminderScheduled = false
            }
        } catch (e: Exception) {
            throw Exception(context.getString(R.string.ErrorMsgUpdateTask))
        }
    }

    fun delete(task: Task) {
        try {
            dataManager.delete(task)
            NotificationUtil.cancelReminder(context, task.Id)
        } catch (e: Exception) {
            throw Exception(context.getString(R.string.ErrorMsgDeleteTask))
        }
    }

    fun getAll(): List<Task> {
        try {
            return dataManager.getAll()
        } catch (e: Exception) {
            throw Exception(context.getString(R.string.ErrorMsgAllTasks))
        }
    }

    fun getById(id: String): Task {
        try {
            val result = dataManager.getById(id)
            if (result == null) {
                throw Exception(context.getString(R.string.ErrorMsgGetByIdTask))
            } else {
                return result
            }
        } catch (e: Exception) {
            throw Exception(context.getString(R.string.ErrorMsgGetByIdTask))
        }
    }

    fun getByCourse(courseId: String): List<Task> {
        try {
            return dataManager.getByCourse(courseId)
        } catch (e: Exception) {
            throw Exception(context.getString(R.string.ErrorMsgTasksByCourse))
        }
    }

    fun getByStatus(status: TaskStatus): List<Task> {
        try {
            return dataManager.filterByStatus(status)
        } catch (e: Exception) {
            throw Exception(context.getString(R.string.ErrorMsgTasksByStatus))
        }
    }

    fun markAsDone(taskId: String) {
        try {
            val task = getById(taskId)
            task.Status = TaskStatus.DONE
            update(task)
        } catch (e: Exception) {
            throw Exception(context.getString(R.string.ErrorMsgMarkDone))
        }
    }
}