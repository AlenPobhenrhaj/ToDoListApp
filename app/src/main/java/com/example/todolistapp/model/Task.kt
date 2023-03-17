package com.example.todolistapp.model

data class Task(
    val id: Long,
    var description: String,
    var isCompleted: Boolean
) {
    companion object {
        fun fromEntity(entity: TaskEntity): Task {
            return Task(entity.id, entity.description, entity.isCompleted)
        }
    }

    fun toEntity(): TaskEntity {
        return TaskEntity(id, description, isCompleted)
    }
}


