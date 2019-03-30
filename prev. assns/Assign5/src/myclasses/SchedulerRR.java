package myclasses;

import provided.Logger;
import provided.Process;
import provided.Scheduler;
import provided.SchedulerBase;

import java.util.ArrayDeque;
import java.util.Queue;

/**
 * Class definition for Round-Robin Scheduler.
 *
 * @author Ky Kartchner
 */
public class SchedulerRR extends SchedulerBase implements Scheduler {
    private Queue<Process> readyQueue;
    private Logger logger;
    private int timeQuantum;

    /**
     * Constructs a Round-Robin Scheduler with the specified logger and timeQuantum.
     *
     * @param logger      The logger to use for logging.
     * @param timeQuantum The amount of time to use as the time quantum.
     */
    public SchedulerRR(Logger logger, int timeQuantum) {
        readyQueue = new ArrayDeque<>();
        this.logger = logger;
        this.timeQuantum = timeQuantum;

    }

    /**
     * Checks if the specified process has been running for time equal to a time quantum.
     *
     * @param p The process to check.
     * @return Return true if time quantum has completed; false if not.
     */
    private boolean isTimeQuantumComplete(Process p) {
        return (p.getElapsedTotal() > 0 && // Ensure time has passed, otherwise "0 % quantum == 0' would return true
                p.getElapsedTotal() % this.timeQuantum == 0);
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
     * process to keep it running or the next process to start running as is appropriate.
     *
     * @param cpu The process currently running on the cpu.
     * @return Reference to the process that should execute on the CPU; result might be null
     * if no process available for scheduling.
     */
    @Override
    public Process update(Process cpu) {
        /* Checks for determining how running process should be updated */
        if (cpu != null) {
            if (cpu.isExecutionComplete()) { /* Check if execution is complete */
                logger.log(String.format("Process %s execution complete", cpu.getName()));
                ++this.contextSwitches;
            } else if (isTimeQuantumComplete(cpu)) { /* Check if time quantum is complete */
                logger.log("Time quantum complete for process " + cpu.getName());
                readyQueue.offer(cpu); // Unschedule the process by returning it to the ready queue
                ++this.contextSwitches;
            } else {
                return cpu; // Current process should continue running, so return it.
            }
        }

        /* Default case for when next process should be scheduled to run */
        Process nextProcess = readyQueue.poll();
        if (nextProcess != null) { // Schedule process received from readyQueue if not null
            logger.log("Scheduled: " + nextProcess.getName());
            ++this.contextSwitches;
        }

        return nextProcess;
    }
}
