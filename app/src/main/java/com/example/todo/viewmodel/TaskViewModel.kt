package com.example.todo.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.todo.data.database.AppDatabase
import com.example.todo.data.model.TaskEntity
import com.example.todo.repository.TaskRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TaskViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: TaskRepository
    val allTasks: LiveData<List<TaskEntity>>

    init {
        val taskDao = AppDatabase.getDatabase(application).taskDao()
        repository = TaskRepository(taskDao)
        allTasks = repository.allTasks
    }

    fun insertTask(task: TaskEntity) = viewModelScope.launch(Dispatchers.IO) {
        repository.insert(task)
    }

    fun updateTask(task: TaskEntity) = viewModelScope.launch(Dispatchers.IO) {
        repository.update(task)
    }

    fun deleteTask(task: TaskEntity) = viewModelScope.launch(Dispatchers.IO) {
        repository.delete(task)
    }

    fun getTaskById(taskId: Long): LiveData<TaskEntity> {
        return repository.getTaskById(taskId)
    }

    fun deleteAllTasks() = viewModelScope.launch(Dispatchers.IO) {
        repository.deleteAll()
    }
}
