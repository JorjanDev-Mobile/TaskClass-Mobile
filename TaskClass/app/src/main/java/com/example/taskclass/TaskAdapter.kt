package com.example.taskclass

import Entity.Task
import Entity.TaskStatus
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.RadioButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.*

class TaskAdapter(
    private val tasks: MutableList<Task>,
    private val onTaskClick: (Task) -> Unit,
    private val onCompleteToggle: (Task, Boolean) -> Unit,
    private val onDeliveredToggle: (Task, Boolean) -> Unit
) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvTitle: TextView = itemView.findViewById(R.id.tv_task_title)
        val tvCourse: TextView = itemView.findViewById(R.id.tv_task_course)
        val tvDueDate: TextView = itemView.findViewById(R.id.tv_task_due_date)  // ← FECHA
        val rbCompleted: RadioButton = itemView.findViewById(R.id.rb_completed)
        val rbNotCompleted: RadioButton = itemView.findViewById(R.id.rb_not_completed)
        val cbDelivered: CheckBox = itemView.findViewById(R.id.cb_delivered)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_task, parent, false)
        return TaskViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = tasks[position]

        holder.tvTitle.text = task.Title
        holder.tvCourse.text = task.Course?.name ?: "No course"


        holder.tvDueDate.text = task.DueDate?.let {
            SimpleDateFormat("dd 'de' MMMM", Locale.forLanguageTag("es-ES")).format(it)
        } ?: "No date"

        holder.rbCompleted.isChecked = task.Status == TaskStatus.COMPLETED
        holder.rbNotCompleted.isChecked = task.Status == TaskStatus.PENDING
        holder.cbDelivered.isChecked = task.Delivered == true

        holder.itemView.setOnClickListener { onTaskClick(task) }
        holder.rbCompleted.setOnClickListener { onCompleteToggle(task, true) }
        holder.rbNotCompleted.setOnClickListener { onCompleteToggle(task, false) }
        holder.cbDelivered.setOnCheckedChangeListener { _, isChecked ->
            onDeliveredToggle(task, isChecked)
        }
    }

    override fun getItemCount() = tasks.size

    fun updateTasks(newTasks: List<Task>) {
        tasks.clear()
        tasks.addAll(newTasks)
        notifyDataSetChanged()
    }
}