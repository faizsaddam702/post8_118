package com.example.post8_118

data class Task(
    var id: String = "",
    val title: String = "",
    val description: String = "",
    val deadline: String = "",
    var isCompleted: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
) {
    // Constructor kosong untuk Firebase
    constructor() : this("", "", "", "", false, 0L)
}