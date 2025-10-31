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

class TaskListAdapter(
    private val onItemClick: (TaskEntity) -> Unit
) : ListAdapter<TaskEntity, TaskListAdapter.TaskViewHolder>(DiffCallback()) {

    class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.textTitle)
        val description: TextView = itemView.findViewById(R.id.textDescription)
        val date: TextView = itemView.findViewById(R.id.textDueDate)
        val priority: TextView = itemView.findViewById(R.id.textPriority)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_task, parent, false)
        return TaskViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = getItem(position)
        holder.title.text = task.title
        holder.description.text = task.description
        holder.date.text = task.dueDate
        holder.priority.text = task.priority

        // Click listener
        holder.itemView.setOnClickListener {
            onItemClick(task)
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<TaskEntity>() {
        override fun areItemsTheSame(oldItem: TaskEntity, newItem: TaskEntity) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: TaskEntity, newItem: TaskEntity) =
            oldItem == newItem
    }
}
