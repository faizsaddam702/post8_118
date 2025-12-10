package com.example.post8_118

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await

class TaskRepository {
    private val db = FirebaseFirestore.getInstance()
    private val tasksCollection = db.collection("tasks")

    // Tambah Task
    suspend fun addTask(task: Task): Result<String> {
        return try {
            val docRef = tasksCollection.add(task).await()
            Result.success(docRef.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Ambil Semua Tasks
    suspend fun getAllTasks(): Result<List<Task>> {
        return try {
            val snapshot = tasksCollection
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .await()

            val tasks = snapshot.documents.mapNotNull { doc ->
                doc.toObject(Task::class.java)?.apply {
                    id = doc.id
                }
            }
            Result.success(tasks)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Update Task
    suspend fun updateTask(task: Task): Result<Unit> {
        return try {
            tasksCollection.document(task.id)
                .set(task)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Hapus Task
    suspend fun deleteTask(taskId: String): Result<Unit> {
        return try {
            tasksCollection.document(taskId)
                .delete()
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Toggle Complete Status
    suspend fun toggleTaskComplete(taskId: String, isCompleted: Boolean): Result<Unit> {
        return try {
            tasksCollection.document(taskId)
                .update("completed", isCompleted)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}