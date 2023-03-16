package com.example.todolistapp.adapters

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.example.todolistapp.R
import com.example.todolistapp.activity.MainActivity
import com.example.todolistapp.model.Task
import com.example.todolistapp.databinding.TaskItemBinding




class TaskAdapter(private val tasks: MutableList<Task>, private val onTaskDeleteClickListener: OnTaskDeleteClickListener) : RecyclerView.Adapter<TaskAdapter.TaskItemBindingViewHolder>() {



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
        Handler(Looper.getMainLooper()).post {
            val task = tasks[position]
            val updatedTask = task.copy(isCompleted = isChecked)
            tasks[position] = updatedTask
            notifyItemChanged(position)
        }
    }

    interface OnTaskDeleteClickListener {
        fun onTaskDeleteClick(position: Int)
    }

    internal fun deleteTask(position: Int) {
        tasks.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, tasks.size)
    }

}

