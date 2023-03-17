package com.example.todolistapp.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.todolistapp.model.TaskEntity

@Dao
interface TaskDao {
    @Query("SELECT * FROM tasks")
    fun getAllTasks(): LiveData<List<TaskEntity>>

    @Insert
    suspend fun insertTask(task: TaskEntity): Long // Make sure this function returns a Long

    @Update
    suspend fun updateTask(task: TaskEntity)

    @Delete
    suspend fun deleteTask(taskEntity: TaskEntity)
}

