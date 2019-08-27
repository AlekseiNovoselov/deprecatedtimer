package src.main.java

import src.main.java.models.PendingTask
import src.main.java.models.Task

fun main() {
    val tasks = listOf(
            PendingTask(0, 1000, Task("Task 1")),
            PendingTask(500, 700, Task("Task 2")),
            PendingTask(400, 200, Task("Task 3")),
            PendingTask(300, 900, Task("Task 4"))
    )
    val scheduler = Scheduler(tasks = tasks)
    scheduler.run()
}