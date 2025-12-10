package com.example.post8_118

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.post8_118.databinding.ItemTaskBinding

class TaskAdapter(
    private val onTaskClick: (Task) -> Unit,
    private val onTaskLongClick: (Task) -> Unit,
    private val onTaskCheckChanged: (Task) -> Unit
) : ListAdapter<Task, TaskAdapter.TaskViewHolder>(TaskDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val binding = ItemTaskBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return TaskViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class TaskViewHolder(
        private val binding: ItemTaskBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(task: Task) {
            binding.apply {
                checkboxTask.isChecked = task.isCompleted
                textTitle.text = task.title
                textDescription.text = task.description
                textDeadline.text = task.deadline

                // Set alpha untuk completed tasks
                val alpha = if (task.isCompleted) 0.5f else 1.0f
                textTitle.alpha = alpha
                textDescription.alpha = alpha
                textDeadline.alpha = alpha

                // Click listeners
                root.setOnClickListener {
                    onTaskClick(task)
                }

                root.setOnLongClickListener {
                    onTaskLongClick(task)
                    true
                }

                checkboxTask.setOnCheckedChangeListener { _, isChecked ->
                    if (isChecked != task.isCompleted) {
                        onTaskCheckChanged(task)
                    }
                }

                // Delete button
                buttonDelete.setOnClickListener {
                    onTaskLongClick(task)
                }
            }
        }
    }

    class TaskDiffCallback : DiffUtil.ItemCallback<Task>() {
        override fun areItemsTheSame(oldItem: Task, newItem: Task): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Task, newItem: Task): Boolean {
            return oldItem == newItem
        }
    }
}