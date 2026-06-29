package com.customrx.schedulers;

/**
 * Represents an object that schedules units of work.
 */
public interface Scheduler {
    
    /**
     * Schedules the given task for execution.
     * 
     * @param task the task to execute
     */
    void execute(Runnable task);
}
