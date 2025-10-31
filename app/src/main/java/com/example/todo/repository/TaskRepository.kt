package com.example.todo.repository

import androidx.lifecycle.LiveData
import com.example.todo.data.dao.TaskDao
import com.example.todo.data.model.TaskEntity

class TaskRepository(private val taskDao: TaskDao) {

    val allTasks: LiveData<List<TaskEntity>> = taskDao.getAllTasks()

    suspend fun insert(task: TaskEntity) {
        taskDao.insert(task)
    }

    suspend fun update(task: TaskEntity) {
        taskDao.update(task)
    }

    suspend fun delete(task: TaskEntity) {
        taskDao.delete(task)
    }

    fun getTaskById(taskId: Long): LiveData<TaskEntity> {
        return taskDao.getTaskById(taskId)
    }

    suspend fun deleteAll() {
        taskDao.deleteAll()
    }
}
