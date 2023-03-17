package com.example.todolistapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.todolistapp.model.Task
import com.example.todolistapp.databinding.TaskItemBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class TaskAdapter(
    private val tasks: MutableList<Task>,
    private val onTaskDeleteClickListener: OnTaskDeleteClickListener,
    private val onTaskCheckedChangeListener: OnTaskCheckedChangeListener // Add the new listener here
) : RecyclerView.Adapter<TaskAdapter.TaskItemBindingViewHolder>() {



    inner class TaskItemBindingViewHolder(val binding: TaskItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(task: Task) {
            binding.apply {
                cbCompleted.isChecked = task.isCompleted
                tvTaskDescription.text = task.description

                // Set a click listener for the CheckBox
                cbCompleted.setOnClickListener {
                    onTaskCheckedChangeListener.onTaskCheckedChange(task, cbCompleted.isChecked)
                }

                // Update the TextView style based on the task completion status
                tvTaskDescription.paint.isStrikeThruText = task.isCompleted

                //Delete icon
                imageViewDeleteTask.setOnClickListener {
                    onTaskDeleteClickListener.onTaskDeleteClick(adapterPosition)
                }

            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskItemBindingViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = TaskItemBinding.inflate(inflater, parent, false)
        return TaskItemBindingViewHolder(binding)
    }


    override fun onBindViewHolder(holder: TaskItemBindingViewHolder, position: Int) {
        val task = tasks[position]
        holder.bind(task)
    }

    override fun getItemCount() = tasks.size

    private fun toggleTaskCompletion(position: Int, isChecked: Boolean) {
        val task = tasks[position]
        val updatedTask = task.copy(isCompleted = isChecked)
        tasks[position] = updatedTask
        notifyItemChanged(position)
        CoroutineScope(Dispatchers.IO).launch {
            onTaskDeleteClickListener.updateTask(updatedTask)
        }
    }


    interface OnTaskDeleteClickListener {
        fun onTaskDeleteClick(position: Int)
        fun updateTask(task: Task)
        fun deleteTask(task: Task)
    }

    interface OnTaskCheckedChangeListener {
        fun onTaskCheckedChange(task: Task, isChecked: Boolean)
    }



    internal fun deleteTask(position: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            onTaskDeleteClickListener.deleteTask(tasks[position]) // Update this line
            tasks.removeAt(position)
            withContext(Dispatchers.Main) {
                notifyItemRemoved(position)
                notifyItemRangeChanged(position, tasks.size)
            }
        }
    }


}

