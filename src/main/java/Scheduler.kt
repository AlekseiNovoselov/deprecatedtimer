package src.main.java

import src.main.java.models.PendingTask
import src.main.java.models.ScheduledTask
import src.main.java.models.Task
import java.util.PriorityQueue

enum class MODE {
    DEPRECATED, FIXED
}

class Scheduler(
        private val tasks: List<PendingTask>,
        private val logger: Logger = Logger(),
        private val mode: MODE = MODE.FIXED
) {

    private var systemTime = 0
    private val maxTime = tasks.map { it.calledTime + it.delay }.max() ?: 0
    private var currentTask: Task? = null
    private var executeTime: Int = Int.MAX_VALUE

    private val scheduledTasks = PriorityQueue<ScheduledTask>(compareBy { it.startTime })

    fun run() {
        while (systemTime <= maxTime) {
            callTasks()
            executeCurrentTask()
            systemTime++
        }
    }

    private fun callTasks() {
        val tasks = tasks.filter { pendingTask -> pendingTask.calledTime == systemTime }
        tasks.forEach { task ->
            if (mode == MODE.DEPRECATED) {
                setDeprecatedTimer(task.delay, task.task)
            } else {
                setFixedTimer(task.delay, task.task)
            }
        }
    }

    private fun executeCurrentTask() {
        if (executeTime == systemTime) {
            currentTask!!.execute()
            if (currentTask!!.name.isNotEmpty()) {
                logger.logTaskExecution(systemTime, currentTask!!.name)
            }
        }
    }

    @Deprecated("use @setFixedTimer")
    private fun setDeprecatedTimer(delay: Int, task: Task) {
        currentTask = task
        executeTime = systemTime + delay
    }

    private fun setFixedTimer(delay: Int, task: Task) {
        val startTime = systemTime + delay
        val scheduledTask = ScheduledTask(startTime, task)
        scheduledTasks.add(scheduledTask)
        val nextExecuteTime = scheduledTasks.peek().startTime

        if (startTime <= nextExecuteTime) {
            val checkTaskAction = createCheckTaskAction()
            setDeprecatedTimer(delay, checkTaskAction)
        }
    }

    private fun createCheckTaskAction(): Task {
        return object : Task(name = "") {

            override fun execute() {
                executeClosestTasks()
                scheduleNextTask()
            }
        }
    }

    private fun executeClosestTasks() {
        val currentTask = scheduledTasks.peek() ?: return
        while (currentTask.startTime == scheduledTasks.peek()?.startTime) {
            val nextTask = scheduledTasks.peek()
            scheduledTasks.poll()

            nextTask.task.execute()
            logger.logTaskExecution(systemTime, nextTask.task.name)
        }
    }

    private fun scheduleNextTask() {
        val nextTask = scheduledTasks.peek() ?: return
        val delay = nextTask.startTime - systemTime
        setDeprecatedTimer(delay, createCheckTaskAction())
    }
}