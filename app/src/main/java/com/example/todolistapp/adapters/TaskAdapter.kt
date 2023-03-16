package com.example.todolistapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.todolistapp.databinding.TaskItemBinding
import androidx.lifecycle.lifecycleScope
import com.example.todolistapp.activity.MainActivity
import com.example.todolistapp.model.Task
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


class TaskAdapter(
    private val tasks: MutableList<Task>,
    private val onTaskDeleteClickListener: OnTaskDeleteClickListener,
    private val onTaskUpdateListener: OnTaskUpdateListener,
    private val coroutineScope: CoroutineScope
) : RecyclerView.Adapter<TaskAdapter.TaskItemBindingViewHolder>() {



    inner class TaskItemBindingViewHolder(val binding: TaskItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(task: Task) {
            binding.apply {
                cbCompleted.isChecked = task.isCompleted
                tvTaskDescription.text = task.description

                // Set a click listener for the CheckBox
                cbCompleted.setOnCheckedChangeListener { _, isChecked ->
                    toggleTaskCompletion(adapterPosition, isChecked)
                }

                // Update the TextView style based on the task completion status
                tvTaskDescription.paint.isStrikeThruText = task.isCompleted

                //Delete icon
                imageViewDeleteTask.setOnClickListener {
                    onTaskDeleteClickListener.onTaskDeleteClick(tasks[adapterPosition])
                }


            }
        }
    }

    private fun toggleTaskCompletion(position: Int, isChecked: Boolean) {
        val task = tasks[position]
        val updatedTask = task.copy(isCompleted = isChecked)
        onTaskUpdateListener.onTaskUpdate(updatedTask)
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


    interface OnTaskDeleteClickListener {
        fun onTaskDeleteClick(task: Task)
    }


    interface OnTaskUpdateListener {
        fun onTaskUpdate(task: Task)
    }


}

