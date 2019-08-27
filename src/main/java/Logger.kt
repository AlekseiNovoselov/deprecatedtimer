package src.main.java

import src.main.java.models.TaskExecutionNote

class Logger {

    val logs = mutableListOf<TaskExecutionNote>()

    fun logTaskExecution(time: Int, taskId: String) {
        val task = TaskExecutionNote(time, taskId)
        logs.add(task)
    }
}