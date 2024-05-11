package com.example.thetaskapp.repository

import com.example.thetaskapp.database.TaskDatabase
import com.example.thetaskapp.model.Task

class TaskRepository(private val db: TaskDatabase) {

    suspend fun insertTask(task: Task) = db.getTaskDao().insertTask(task)
    suspend fun deleteTask(task: Task) = db.getTaskDao().deleteTask(task)
    suspend fun updateTask(task: Task) = db.getTaskDao().updateTask(task)

    fun getAllTask() = db.getTaskDao().getAllTask()
    fun searchTask(query: String?) = db.getTaskDao().searchTask(query)
}