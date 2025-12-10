package com.example.post8_118

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.post8_118.databinding.ActivityMainBinding
import com.example.post8_118.databinding.DialogAddTaskBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: TaskViewModel
    private lateinit var taskAdapter: TaskAdapter
    private var selectedDate = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViewModel()
        setupRecyclerView()
        setupObservers()
        setupListeners()
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(this)[TaskViewModel::class.java]
    }

    private fun setupRecyclerView() {
        taskAdapter = TaskAdapter(
            onTaskClick = { task -> showEditTaskDialog(task) },
            onTaskLongClick = { task -> showDeleteConfirmation(task) },
            onTaskCheckChanged = { task ->
                viewModel.toggleTaskComplete(task.id, task.isCompleted)
            }
        )

        binding.recyclerViewTasks.apply {
            adapter = taskAdapter
            layoutManager = LinearLayoutManager(this@MainActivity)
        }
    }

    private fun setupObservers() {
        viewModel.tasks.observe(this) { tasks ->
            taskAdapter.submitList(tasks)
            binding.textEmpty.visibility = if (tasks.isEmpty()) View.VISIBLE else View.GONE
        }

        viewModel.loading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.error.observe(this) { error ->
            error?.let {
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
                viewModel.clearError()
            }
        }

        viewModel.operationSuccess.observe(this) { message ->
            message?.let {
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
                viewModel.clearSuccess()
            }
        }
    }

    private fun setupListeners() {
        binding.fabAdd.setOnClickListener {
            showAddTaskDialog()
        }
    }

    private fun showAddTaskDialog() {
        val dialogBinding = DialogAddTaskBinding.inflate(layoutInflater)
        selectedDate = ""

        val dialog = AlertDialog.Builder(this)
            .setTitle("Tambah Tugas Baru")
            .setView(dialogBinding.root)
            .setPositiveButton("Simpan", null)
            .setNegativeButton("Batal", null)
            .create()

        dialogBinding.buttonSelectDate.setOnClickListener {
            showDatePicker { date ->
                selectedDate = date
                dialogBinding.textDeadline.text = date
            }
        }

        dialog.setOnShowListener {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                val title = dialogBinding.editTitle.text.toString().trim()
                val description = dialogBinding.editDescription.text.toString().trim()

                when {
                    title.isEmpty() -> {
                        dialogBinding.editTitle.error = "Judul tidak boleh kosong"
                    }
                    selectedDate.isEmpty() -> {
                        Toast.makeText(this, "Pilih deadline", Toast.LENGTH_SHORT).show()
                    }
                    else -> {
                        viewModel.addTask(title, description, selectedDate)
                        dialog.dismiss()
                    }
                }
            }
        }

        dialog.show()
    }

    private fun showEditTaskDialog(task: Task) {
        val dialogBinding = DialogAddTaskBinding.inflate(layoutInflater)
        selectedDate = task.deadline

        dialogBinding.apply {
            editTitle.setText(task.title)
            editDescription.setText(task.description)
            textDeadline.text = task.deadline
        }

        val dialog = AlertDialog.Builder(this)
            .setTitle("Edit Tugas")
            .setView(dialogBinding.root)
            .setPositiveButton("Simpan", null)
            .setNegativeButton("Batal", null)
            .create()

        dialogBinding.buttonSelectDate.setOnClickListener {
            showDatePicker { date ->
                selectedDate = date
                dialogBinding.textDeadline.text = date
            }
        }

        dialog.setOnShowListener {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                val title = dialogBinding.editTitle.text.toString().trim()
                val description = dialogBinding.editDescription.text.toString().trim()

                when {
                    title.isEmpty() -> {
                        dialogBinding.editTitle.error = "Judul tidak boleh kosong"
                    }
                    selectedDate.isEmpty() -> {
                        Toast.makeText(this, "Pilih deadline", Toast.LENGTH_SHORT).show()
                    }
                    else -> {
                        val updatedTask = task.copy(
                            title = title,
                            description = description,
                            deadline = selectedDate
                        )
                        viewModel.updateTask(updatedTask)
                        dialog.dismiss()
                    }
                }
            }
        }

        dialog.show()
    }

    private fun showDeleteConfirmation(task: Task) {
        AlertDialog.Builder(this)
            .setTitle("Hapus Tugas")
            .setMessage("Yakin ingin menghapus \"${task.title}\"?")
            .setPositiveButton("Hapus") { _, _ ->
                viewModel.deleteTask(task.id)
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun showDatePicker(onDateSelected: (String) -> Unit) {
        val calendar = Calendar.getInstance()

        DatePickerDialog(
            this,
            { _, year, month, day ->
                calendar.set(year, month, day)
                val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                onDateSelected(dateFormat.format(calendar.time))
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }
}