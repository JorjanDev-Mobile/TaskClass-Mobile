package Util

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

class ReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val taskId = intent.getStringExtra("taskId")
        val title = intent.getStringExtra("taskTitle") ?: "Tarea próxima a vencer"

        val builder = NotificationCompat.Builder(context, "task_channel")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("TaskClass")
            .setContentText("$title está por vencer")
            .setPriority(NotificationCompat.PRIORITY_HIGH)

        val notificationManager = NotificationManagerCompat.from(context)

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
            == PackageManager.PERMISSION_GRANTED
        ) {
            notificationManager.notify(
                taskId?.hashCode() ?: System.currentTimeMillis().toInt(),
                builder.build()
            )
        } else {
            // No hay permiso, no se puede notificar
        }
    }
}
