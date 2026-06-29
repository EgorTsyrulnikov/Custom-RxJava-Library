package com.customrx.schedulers;

/**
 * Static factory methods for returning standard Scheduler instances.
 */
public final class Schedulers {

    private static final Scheduler IO;
    private static final Scheduler COMPUTATION;
    private static final Scheduler SINGLE;

    static {
        IO = new IOThreadScheduler();
        COMPUTATION = new ComputationScheduler();
        SINGLE = new SingleThreadScheduler();
    }

    private Schedulers() {
        // No instances
    }

    public static Scheduler io() {
        return IO;
    }

    public static Scheduler computation() {
        return COMPUTATION;
    }

    public static Scheduler single() {
        return SINGLE;
    }
}
