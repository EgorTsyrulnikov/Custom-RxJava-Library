package com.customrx;

import com.customrx.core.Observer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class TestObserver<T> implements Observer<T> {

    private final List<T> values = Collections.synchronizedList(new ArrayList<>());
    private volatile Throwable error;
    private volatile boolean completed;
    private final CountDownLatch latch = new CountDownLatch(1);

    @Override
    public void onNext(T item) {
        values.add(item);
    }

    @Override
    public void onError(Throwable t) {
        this.error = t;
        latch.countDown();
    }

    @Override
    public void onComplete() {
        this.completed = true;
        latch.countDown();
    }

    public void await() throws InterruptedException {
        if (!latch.await(5, java.util.concurrent.TimeUnit.SECONDS)) {
            throw new RuntimeException("TestObserver timed out waiting for completion");
        }
    }

    public List<T> getValues() {
        return values;
    }

    public Throwable getError() {
        return error;
    }

    public boolean isCompleted() {
        return completed;
    }
}
