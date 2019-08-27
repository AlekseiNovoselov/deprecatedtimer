import org.junit.Test
import src.main.java.Logger
import src.main.java.MODE
import src.main.java.Scheduler
import src.main.java.models.PendingTask
import src.main.java.models.Task
import src.main.java.models.TaskExecutionNote
import kotlin.test.assertEquals

class SchedulerSetDeprecatedTimerTest {

    @Test
    fun startOneTaskImmediately() {
        val logger = Logger()
        val tasks = listOf(PendingTask(calledTime = 0, delay = 0, task = Task("Task 1")))
        val scheduler = Scheduler(tasks = tasks, logger = logger, mode = MODE.DEPRECATED)

        scheduler.run()

        val actualLogs = logger.logs
        val expectedLogs = listOf(TaskExecutionNote(startTime = 0, taskName = "Task 1"))
        assertEquals(expectedLogs, actualLogs)
    }

    @Test
    fun startOneTaskWithDelay() {
        val logger = Logger()
        val tasks = listOf(PendingTask(calledTime = 0, delay = 1000, task = Task("Task 1")))
        val scheduler = Scheduler(tasks = tasks, logger = logger, mode = MODE.DEPRECATED)

        scheduler.run()

        val actualLogs = logger.logs
        val expectedLogs = listOf(TaskExecutionNote(startTime = 1000, taskName = "Task 1"))
        assertEquals(expectedLogs, actualLogs)
    }

    @Test
    fun startOneTaskWithDelayedCall() {
        val logger = Logger()
        val tasks = listOf(PendingTask(calledTime = 1000, delay = 0, task = Task("Task 1")))
        val scheduler = Scheduler(tasks = tasks, logger = logger, mode = MODE.DEPRECATED)

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
        val scheduler = Scheduler(tasks = tasks, logger = logger, mode = MODE.DEPRECATED)

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
        val scheduler = Scheduler(tasks = tasks, logger = logger, mode = MODE.DEPRECATED)

        scheduler.run()

        val actualLogs = logger.logs
        val expectedLogs = listOf(TaskExecutionNote(startTime = 1200, taskName = "Task 2"))
        assertEquals(expectedLogs, actualLogs)
    }

    @Test
    fun startTwoSimultaneousTasks() {
        val logger = Logger()
        val tasks = listOf(
                PendingTask(calledTime = 500, delay = 700, task = Task("Task 1")),
                PendingTask(calledTime = 500, delay = 700, task = Task("Task 2"))
        )
        val scheduler = Scheduler(tasks = tasks, logger = logger, mode = MODE.DEPRECATED)

        scheduler.run()

        val actualLogs = logger.logs
        val expectedLogs = listOf(TaskExecutionNote(startTime = 1200, taskName = "Task 2"))
        assertEquals(expectedLogs, actualLogs)
    }

    @Test
    fun startManyTasks() {
        val logger = Logger()
        val tasks = listOf(
                PendingTask(calledTime = 0, delay = 1000, task = Task("Task 1")),
                PendingTask(calledTime = 400, delay = 0, task = Task("Task 2")),
                PendingTask(calledTime = 500, delay = 700, task = Task("Task 3")),
                PendingTask(calledTime = 600, delay = 800, task = Task("Task 4"))
        )
        val scheduler = Scheduler(tasks = tasks, logger = logger, mode = MODE.DEPRECATED)

        scheduler.run()

        val actualLogs = logger.logs
        val expectedLogs = listOf(
                TaskExecutionNote(startTime = 400, taskName = "Task 2"),
                TaskExecutionNote(startTime = 1400, taskName = "Task 4")
        )
        assertEquals(expectedLogs, actualLogs)
    }
}