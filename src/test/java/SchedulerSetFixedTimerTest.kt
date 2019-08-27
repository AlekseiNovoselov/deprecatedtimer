import org.junit.Test
import src.main.java.*
import src.main.java.models.PendingTask
import src.main.java.models.Task
import src.main.java.models.TaskExecutionNote
import kotlin.test.assertEquals

class SchedulerSetFixedTimerTest {

    @Test
    fun startOneTaskImmediately() {
        val logger = Logger()
        val tasks = listOf(PendingTask(calledTime = 0, delay = 0, task = Task("Task 1")))
        val scheduler = Scheduler(tasks = tasks, logger = logger)

        scheduler.run()

        val actualLogs = logger.logs
        val expectedLogs = listOf(TaskExecutionNote(startTime = 0, taskName = "Task 1"))
        assertEquals(expectedLogs, actualLogs)
    }

    @Test
    fun startOneTaskWithDelay() {
        val logger = Logger()
        val tasks = listOf(PendingTask(calledTime = 0, delay = 1000, task = Task("Task 1")))
        val scheduler = Scheduler(tasks = tasks, logger = logger)

        scheduler.run()

        val actualLogs = logger.logs
        val expectedLogs = listOf(TaskExecutionNote(startTime = 1000, taskName = "Task 1"))
        assertEquals(expectedLogs, actualLogs)
    }

    @Test
    fun startOneTaskWithDelayedCall() {
        val logger = Logger()
        val tasks = listOf(PendingTask(calledTime = 1000, delay = 0, task = Task("Task 1")))
        val scheduler = Scheduler(tasks = tasks, logger = logger)

        scheduler.run()

        val actualLogs = logger.logs
        val expectedLogs = listOf(TaskExecutionNote(startTime = 1000, taskName = "Task 1"))
        assertEquals(expectedLogs, actualLogs)
    }

    @Test
    fun startTwoNonOverlappingTasks() {
        val logger = Logger()
        val tasks = listOf(
                PendingTask(calledTime = 0, delay = 1000, task = Task("Task 1")),
                PendingTask(calledTime = 2000, delay = 1000, task = Task("Task 2"))
        )
        val scheduler = Scheduler(tasks = tasks, logger = logger)

        scheduler.run()

        val actualLogs = logger.logs
        val expectedLogs = listOf(
                TaskExecutionNote(startTime = 1000, taskName = "Task 1"),
                TaskExecutionNote(startTime = 3000, taskName = "Task 2")
        )
        assertEquals(expectedLogs, actualLogs)
    }

    @Test
    fun startTwoOverlappingTasks() {
        val logger = Logger()
        val tasks = listOf(
                PendingTask(calledTime = 0, delay = 1000, task = Task("Task 1")),
                PendingTask(calledTime = 500, delay = 700, task = Task("Task 2"))
        )
        val scheduler = Scheduler(tasks = tasks, logger = logger)

        scheduler.run()

        val actualLogs = logger.logs
        val expectedLogs = listOf(
                TaskExecutionNote(startTime = 1000, taskName = "Task 1"),
                TaskExecutionNote(startTime = 1200, taskName = "Task 2")
        )
        assertEquals(expectedLogs, actualLogs)
    }

    @Test
    fun startTwoSimultaneousTasks() {
        val logger = Logger()
        val tasks = listOf(
                PendingTask(calledTime = 500, delay = 700, task = Task("Task 1")),
                PendingTask(calledTime = 500, delay = 700, task = Task("Task 2"))
        )
        val scheduler = Scheduler(tasks = tasks, logger = logger)

        scheduler.run()

        val actualLogs = logger.logs
        val expectedLogs = listOf(
                TaskExecutionNote(startTime = 1200, taskName = "Task 1"),
                TaskExecutionNote(startTime = 1200, taskName = "Task 2")
        )
        assertEquals(expectedLogs, actualLogs)
    }

    @Test
    fun startFourTasks() {
        val logger = Logger()
        val tasks = listOf(
                PendingTask(calledTime = 0, delay = 1000, task = Task("Task 1")),
                PendingTask(calledTime = 500, delay = 700, task = Task("Task 2")),
                PendingTask(calledTime = 600, delay = 800, task = Task("Task 3")),
                PendingTask(calledTime = 500, delay = 0, task = Task("Task 4"))
        )
        val scheduler = Scheduler(tasks = tasks, logger = logger)

        scheduler.run()

        val actualLogs = logger.logs
        val expectedLogs = listOf(
                TaskExecutionNote(startTime = 500, taskName = "Task 4"),
                TaskExecutionNote(startTime = 1000, taskName = "Task 1"),
                TaskExecutionNote(startTime = 1200, taskName = "Task 2"),
                TaskExecutionNote(startTime = 1400, taskName = "Task 3")
        )
        assertEquals(expectedLogs, actualLogs)
    }

    @Test
    fun startManyTasks() {
        val logger = Logger()
        val tasks = listOf(
                PendingTask(calledTime = 0, delay = 0, task = Task("Task 1")),
                PendingTask(calledTime = 0, delay = 1000, task = Task("Task 2")),
                PendingTask(calledTime = 500, delay = 700, task = Task("Task 3")),
                PendingTask(calledTime = 500, delay = 700, task = Task("Task 4")),
                PendingTask(calledTime = 600, delay = 800, task = Task("Task 5")),
                PendingTask(calledTime = 500, delay = 0, task = Task("Task 6")),
                PendingTask(calledTime = 200, delay = 1500, task = Task("Task 7"))
        )
        val scheduler = Scheduler(tasks = tasks, logger = logger)

        scheduler.run()

        val actualLogs = logger.logs
        val expectedLogs = listOf(
                TaskExecutionNote(startTime = 0, taskName = "Task 1"),
                TaskExecutionNote(startTime = 500, taskName = "Task 6"),
                TaskExecutionNote(startTime = 1000, taskName = "Task 2"),
                TaskExecutionNote(startTime = 1200, taskName = "Task 4"),
                TaskExecutionNote(startTime = 1200, taskName = "Task 3"),
                TaskExecutionNote(startTime = 1400, taskName = "Task 5"),
                TaskExecutionNote(startTime = 1700, taskName = "Task 7")
        )
        assertEquals(expectedLogs, actualLogs)
    }
}