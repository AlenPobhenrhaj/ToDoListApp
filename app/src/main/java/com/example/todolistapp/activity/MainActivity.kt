package com.example.todolistapp.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.todolistapp.R
import com.example.todolistapp.adapters.TaskAdapter
import com.example.todolistapp.database.TaskDao
import com.example.todolistapp.database.TaskDatabase
import com.example.todolistapp.databinding.ActivityMainBinding
import com.example.todolistapp.databinding.DialogAddTaskBinding
import com.example.todolistapp.model.Task
import kotlinx.coroutines.*


class MainActivity : AppCompatActivity(),
    TaskAdapter.OnTaskDeleteClickListener,
    TaskAdapter.OnTaskCheckedChangeListener {

    private var binding: ActivityMainBinding? = null
    private val tasks: MutableList<Task> = mutableListOf()

    private val taskAdapter: TaskAdapter by lazy {
        TaskAdapter(tasks, this, this) // Pass 'this' as the new listener
    }

    private val taskDao: TaskDao by lazy {
        TaskDatabase.getInstance(this).taskDao()
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

        // Observe LiveData from the database
        taskDao.getAllTasks().observe(this) { taskEntities ->
            binding!!.rvTasks.post {
                tasks.clear()
                tasks.addAll(taskEntities.map { Task.fromEntity(it) })
                taskAdapter.notifyDataSetChanged()
            }
        }

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
        CoroutineScope(Dispatchers.IO).launch {
            val newTask = Task(id = 0L, description = taskDescription, isCompleted = false)
            val taskId = taskDao.insertTask(newTask.toEntity())

            withContext(Dispatchers.Main) {
                val updatedTask = newTask.copy(id = taskId)
                tasks.add(updatedTask)
                taskAdapter.notifyItemInserted(tasks.size - 1)
            }
        }
    }

    override fun updateTask(task: Task) {
        CoroutineScope(Dispatchers.IO).launch {
            taskDao.updateTask(task.toEntity())
        }
    }

    override fun deleteTask(task: Task) {
        CoroutineScope(Dispatchers.IO).launch {
            taskDao.deleteTask(task.toEntity()) // Update this line
            tasks.remove(task)
            withContext(Dispatchers.Main) {
                taskAdapter.notifyDataSetChanged()
            }
        }
    }

    override fun onTaskCheckedChange(task: Task, isChecked: Boolean) {
        // Update the task in the database
        CoroutineScope(Dispatchers.IO).launch {
            val updatedTask = task.copy(isCompleted = isChecked)
            taskDao.updateTask(updatedTask.toEntity())

            // Update the task in the tasks list
            withContext(Dispatchers.Main) {
                val position = tasks.indexOf(task)
                tasks[position] = updatedTask
                taskAdapter.notifyItemChanged(position)
            }
        }
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

