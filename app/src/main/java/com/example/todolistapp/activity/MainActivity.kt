package com.example.todolistapp.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.todolistapp.R
import com.example.todolistapp.adapters.TaskAdapter
import com.example.todolistapp.databinding.ActivityMainBinding
import com.example.todolistapp.databinding.DialogAddTaskBinding
import com.example.todolistapp.model.Task


class MainActivity : AppCompatActivity(), TaskAdapter.OnTaskDeleteClickListener {
    private var binding: ActivityMainBinding? = null
    private val tasks: MutableList<Task> = mutableListOf()

    private val taskAdapter: TaskAdapter by lazy {
        TaskAdapter(tasks, this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)


        binding!!.rvTasks.layoutManager = LinearLayoutManager(this)
        binding!!.rvTasks.adapter = taskAdapter


        binding!!.fabActionButton.setOnClickListener {
            showAddTaskDialog()
        }

        //ToolBar
        setSupportActionBar(binding!!.toolbar)

    }

    private fun showAddTaskDialog() {
        val dialogBinding = DialogAddTaskBinding.inflate(layoutInflater)
        val alertDialog = AlertDialog.Builder(this)
            .setTitle(R.string.add_task)
            .setView(dialogBinding.root)
            .setPositiveButton(R.string.add) { _, _ ->
                val taskDescription = dialogBinding.editTextTaskDescription.text.toString()
                if (taskDescription.isNotBlank()) {
                    addTask(taskDescription)
                }
            }
            .setNegativeButton(R.string.cancel, null)
            .create()

        alertDialog.show()
    }

    private fun addTask(taskDescription: String) {
        val newTask = Task(id = (tasks.size + 1), description = taskDescription, isCompleted = false)
        tasks.add(newTask)
        taskAdapter.notifyItemInserted(tasks.size - 1)
    }

    override fun onTaskDeleteClick(position: Int) {
        showDeleteTaskDialog(position)
    }

    private fun showDeleteTaskDialog(position: Int) {
        AlertDialog.Builder(this)
            .setTitle(R.string.delete_task_title)
            .setMessage(R.string.delete_task_message)
            .setPositiveButton(R.string.delete) { _, _ ->
                taskAdapter.deleteTask(position)
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}