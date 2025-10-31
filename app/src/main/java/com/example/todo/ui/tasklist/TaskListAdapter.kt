package com.example.todo.ui.tasklist

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.todo.R
import com.example.todo.data.model.TaskEntity
import com.google.android.material.progressindicator.LinearProgressIndicator

class TaskListAdapter(
    private val onItemClick: (TaskEntity) -> Unit
) : ListAdapter<TaskEntity, TaskListAdapter.TaskViewHolder>(DiffCallback()) {

    class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val avatar: TextView = itemView.findViewById(R.id.textAvatar)
        val title: TextView = itemView.findViewById(R.id.textTitle)
        val progressText: TextView = itemView.findViewById(R.id.textProgress)
        val date: TextView = itemView.findViewById(R.id.textDueDate)
        val priority: TextView = itemView.findViewById(R.id.textPriority)
        val progressIndicator: LinearProgressIndicator =
            itemView.findViewById(R.id.progressIndicator)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_task, parent, false)
        return TaskViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = getItem(position)
        holder.title.text = task.title
        holder.date.text = task.dueDate
        holder.priority.text = task.priority

        // Replace description with progress percentage
        holder.progressText.text = "Progress | ${task.progress}%"
        holder.progressIndicator.progress = task.progress

        // Avatar first letter
        val firstLetter = task.title.firstOrNull()?.uppercaseChar()?.toString() ?: "?"
        holder.avatar.text = firstLetter

        // Tint avatar color based on priority
        val context = holder.itemView.context
        val color = when (task.priority.lowercase()) {
            "high" -> context.getColor(R.color.progress_high)
            "medium" -> context.getColor(R.color.progress_medium)
            else -> context.getColor(R.color.progress_low)
        }
        holder.avatar.background.setTint(color)

        // Progress color
        when {
            task.progress >= 80 -> holder.progressIndicator.setIndicatorColor(
                context.getColor(R.color.progress_high)
            )
            task.progress >= 40 -> holder.progressIndicator.setIndicatorColor(
                context.getColor(R.color.progress_medium)
            )
            else -> holder.progressIndicator.setIndicatorColor(
                context.getColor(R.color.progress_low)
            )
        }

        holder.itemView.setOnClickListener { onItemClick(task) }
    }

    class DiffCallback : DiffUtil.ItemCallback<TaskEntity>() {
        override fun areItemsTheSame(oldItem: TaskEntity, newItem: TaskEntity) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: TaskEntity, newItem: TaskEntity) =
            oldItem == newItem
    }
}
