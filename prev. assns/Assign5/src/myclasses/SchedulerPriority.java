package myclasses;

import provided.Logger;
import provided.Process;
import provided.Scheduler;
import provided.SchedulerBase;

import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Queue;

/**
 * Class definition for Priority Scheduler.
 *
 * @author Ky Kartchner
 */
public class SchedulerPriority extends SchedulerBase implements Scheduler {
    private Queue<Process> readyQueue;
    private Logger logger;

    /**
     * Constructs a Priority Scheduler with the specified logger.
     *
     * @param logger The logger to use for logging.
     */
    public SchedulerPriority(Logger logger) {
        readyQueue = new PriorityQueue<>(5,
                Comparator.comparing(Process::getPriority)); // Highest priority (lowest number) comes first
        this.logger = logger;
    }

    /**
     * Used to notify the scheduler a new process has just entered the ready state.
     *
     * @param p The process to notify about.
     */
    @Override
    public void notifyNewProcess(Process p) {
        readyQueue.offer(p);
    }

    /**
     * Update the scheduling algorithm for a single CPU. Return either the currently running
     * process to keep it running or the next process to start running as applicable.
     *
     * @param cpu The process currently running on the cpu.
     * @return Reference to the process that should execute on the CPU; result might be null
     * if no process available for scheduling.
     */
    @Override
    public Process update(Process cpu) {
        /* Checks for determining how running process should be updated */
        if (cpu != null) {
            if (cpu.isBurstComplete()) { /* Check if process burst is complete */
                logger.log(String.format("Process %s burst complete", cpu.getName()));
                ++this.contextSwitches;

                if (cpu.isExecutionComplete()) { /* Check if execution is complete */
                    logger.log(String.format("Process %s execution complete", cpu.getName()));
                } else {
                    readyQueue.offer(cpu); // Unschedule the process by returning it to the ready queue
                }
            } else {
                return cpu; // Current process should continue running, so return it.
            }
        }
        /* Default case for when next process should be scheduled to run */
        Process nextProcess = readyQueue.poll();
        if (nextProcess != null) { // Schedule process received form readyQueue
            logger.log("Scheduled: " + nextProcess.getName());
            ++this.contextSwitches;
        }

        return nextProcess;
    }
}
