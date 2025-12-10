package com.example.post8_118

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class TaskViewModel : ViewModel() {
    private val repository = TaskRepository()

    private val _tasks = MutableLiveData<List<Task>>()
    val tasks: LiveData<List<Task>> = _tasks

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    private val _operationSuccess = MutableLiveData<String?>()
    val operationSuccess: LiveData<String?> = _operationSuccess

    init {
        loadTasks()
    }

    fun loadTasks() {
        viewModelScope.launch {
            _loading.value = true
            repository.getAllTasks().fold(
                onSuccess = { taskList ->
                    _tasks.value = taskList
                    _error.value = null
                },
                onFailure = { exception ->
                    _error.value = "Gagal memuat tasks: ${exception.message}"
                }
            )
            _loading.value = false
        }
    }

    fun addTask(title: String, description: String, deadline: String) {
        viewModelScope.launch {
            _loading.value = true
            val task = Task(
                title = title,
                description = description,
                deadline = deadline
            )

            repository.addTask(task).fold(
                onSuccess = {
                    _operationSuccess.value = "Task berhasil ditambahkan"
                    loadTasks()
                },
                onFailure = { exception ->
                    _error.value = "Gagal menambah task: ${exception.message}"
                }
            )
            _loading.value = false
        }
    }

    fun updateTask(task: Task) {
        viewModelScope.launch {
            _loading.value = true
            repository.updateTask(task).fold(
                onSuccess = {
                    _operationSuccess.value = "Task berhasil diupdate"
                    loadTasks()
                },
                onFailure = { exception ->
                    _error.value = "Gagal update task: ${exception.message}"
                }
            )
            _loading.value = false
        }
    }

    fun deleteTask(taskId: String) {
        viewModelScope.launch {
            _loading.value = true
            repository.deleteTask(taskId).fold(
                onSuccess = {
                    _operationSuccess.value = "Task berhasil dihapus"
                    loadTasks()
                },
                onFailure = { exception ->
                    _error.value = "Gagal hapus task: ${exception.message}"
                }
            )
            _loading.value = false
        }
    }

    fun toggleTaskComplete(taskId: String, isCompleted: Boolean) {
        viewModelScope.launch {
            repository.toggleTaskComplete(taskId, !isCompleted).fold(
                onSuccess = {
                    loadTasks()
                },
                onFailure = { exception ->
                    _error.value = "Gagal update status: ${exception.message}"
                }
            )
        }
    }

    fun clearError() {
        _error.value = null
    }

    fun clearSuccess() {
        _operationSuccess.value = null
    }
}