package Util

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import Entity.Task

object NotificationUtil {
    fun scheduleReminder(context: Context, task: Task) {
        try {
            val due = task.DueDate ?: return
            val alarmMgr = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

            val intent = Intent(context, ReminderReceiver::class.java).apply {
                putExtra("taskId", task.Id)
                putExtra("taskTitle", task.Title)
            }

            val pending = PendingIntent.getBroadcast(context, task.Id.hashCode(), intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
            if (!alarmMgr.canScheduleExactAlarms()) {
                return
            }

            alarmMgr.setExact(AlarmManager.RTC_WAKEUP, due.time, pending)

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    fun cancelReminder(context: Context, taskId: String) {
        try {
            val alarmMgr = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val intent = Intent(context, ReminderReceiver::class.java)
            val pending = PendingIntent.getBroadcast(
                context,
                taskId.hashCode(),
                intent,
                PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
            )
            if (pending != null) {
                alarmMgr.cancel(pending)
            }
        } catch (e: Exception) {
            throw e
        }
    }
}
