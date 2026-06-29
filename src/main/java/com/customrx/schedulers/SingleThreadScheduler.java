package com.customrx.schedulers;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * A Scheduler backed by a single thread executor.
 * Suitable for strictly sequential execution.
 */
public class SingleThreadScheduler implements Scheduler {

    private final ExecutorService executor;

    public SingleThreadScheduler() {
        this.executor = Executors.newSingleThreadExecutor(new RxThreadFactory("RxSingleThread"));
    }

    @Override
    public void execute(Runnable task) {
        executor.submit(task);
    }
}
