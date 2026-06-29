package com.customrx.schedulers;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * A Scheduler backed by a cached thread pool. 
 * Suitable for IO-bound work.
 */
public class IOThreadScheduler implements Scheduler {

    private final ExecutorService executor;

    public IOThreadScheduler() {
        this.executor = Executors.newCachedThreadPool(new RxThreadFactory("RxIOThread"));
    }

    @Override
    public void execute(Runnable task) {
        executor.submit(task);
    }
}
