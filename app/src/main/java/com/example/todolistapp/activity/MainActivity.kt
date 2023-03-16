package com.example.todolistapp.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.todolistapp.R
import com.example.todolistapp.adapters.TaskAdapter
import com.example.todolistapp.database.AppDatabase
import com.example.todolistapp.databinding.ActivityMainBinding
import com.example.todolistapp.databinding.DialogAddTaskBinding
import com.example.todolistapp.model.Task
import com.example.todolistapp.model.TaskEntity
import com.example.todolistapp.repository.TaskRepository
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity(), TaskAdapter.OnTaskDeleteClickListener, TaskAdapter.OnTaskUpdateListener {
    private var binding: ActivityMainBinding? = null
    private val tasks: MutableList<Task> = mutableListOf()
    private lateinit var taskRepository: TaskRepository
    private val taskAdapter: TaskAdapter by lazy {
        TaskAdapter(tasks, this, this, lifecycleScope)
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

        taskRepository = TaskRepository(AppDatabase.getInstance(this).taskDao())
        loadTasks()

        binding!!.rvTasks.adapter = taskAdapter
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
        val newTask = TaskEntity(id = 0, description = taskDescription, isCompleted = false)
        lifecycleScope.launch {
            taskRepository.insertTask(newTask)
            loadTasks()
        }
    }

    fun TaskEntity.toTask(): Task {
        return Task(id = this.id, description = this.description, isCompleted = this.isCompleted)
    }

    private fun loadTasks() {
        lifecycleScope.launch {
            tasks.clear()
            tasks.addAll(taskRepository.getAllTasks().map { it.toTask() })
            taskAdapter.notifyDataSetChanged()
        }
    }


    override fun onTaskUpdate(task: Task) {
        lifecycleScope.launch {
            taskRepository.updateTask(task)
            loadTasks()
        }
    }

    override fun onTaskDeleteClick(task: Task) {
        showDeleteTaskDialog(task)
    }



    private fun showDeleteTaskDialog(task: Task) {
        AlertDialog.Builder(this)
            .setTitle(R.string.delete_task_title)
            .setMessage(R.string.delete_task_message)
            .setPositiveButton(R.string.delete) { _, _ ->
                onTaskDeleteClick(task)
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }


    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}