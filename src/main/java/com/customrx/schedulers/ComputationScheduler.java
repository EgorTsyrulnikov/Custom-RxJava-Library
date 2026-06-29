package com.customrx.schedulers;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * A Scheduler backed by a fixed thread pool sized to the number of available processors.
 * Suitable for CPU-bound work.
 */
public class ComputationScheduler implements Scheduler {

    private final ExecutorService executor;

    public ComputationScheduler() {
        int processors = Runtime.getRuntime().availableProcessors();
        this.executor = Executors.newFixedThreadPool(processors, new RxThreadFactory("RxComputationThreadPool"));
    }

    @Override
    public void execute(Runnable task) {
        executor.submit(task);
    }
}
