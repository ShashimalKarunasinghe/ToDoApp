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

    class TaskViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val avatar: TextView = view.findViewById(R.id.textAvatar)
        val title: TextView = view.findViewById(R.id.textTitle)
        val progressText: TextView = view.findViewById(R.id.textProgress)
        val date: TextView = view.findViewById(R.id.textDueDate)
        val priority: TextView = view.findViewById(R.id.textPriority)
        val progressIndicator: LinearProgressIndicator = view.findViewById(R.id.progressIndicator)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_task, parent, false)
        return TaskViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = getItem(position)
        val context = holder.itemView.context

        // --- Title, Date, Priority ---
        holder.title.text = task.title
        holder.date.text = task.dueDate ?: "No due date"
        holder.priority.text = task.priority ?: "Unknown"

        // --- Progress ---
        holder.progressText.text = "Progress | ${task.progress}%"
        holder.progressIndicator.progress = task.progress

        // --- Avatar (first letter of title) ---
        val firstLetter = task.title?.firstOrNull()?.uppercaseChar()?.toString() ?: "?"
        holder.avatar.text = firstLetter

        // --- Avatar tint color based on priority ---
        val avatarColor = when (task.priority?.lowercase()) {
            "high" -> context.getColor(R.color.progress_high)
            "medium" -> context.getColor(R.color.progress_medium)
            else -> context.getColor(R.color.progress_low)
        }
        holder.avatar.background?.setTint(avatarColor)

        // --- Progress indicator color ---
        val indicatorColor = when {
            task.progress >= 80 -> context.getColor(R.color.progress_high)
            task.progress >= 40 -> context.getColor(R.color.progress_medium)
            else -> context.getColor(R.color.progress_low)
        }
        holder.progressIndicator.setIndicatorColor(indicatorColor)

        // --- Click listener ---
        holder.itemView.setOnClickListener { onItemClick(task) }
    }

    private class DiffCallback : DiffUtil.ItemCallback<TaskEntity>() {
        override fun areItemsTheSame(oldItem: TaskEntity, newItem: TaskEntity): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: TaskEntity, newItem: TaskEntity): Boolean =
            oldItem == newItem
    }
}
