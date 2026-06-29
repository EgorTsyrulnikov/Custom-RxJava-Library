package com.customrx;

import com.customrx.core.Observable;
import com.customrx.schedulers.Schedulers;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

public class SchedulersTest {

    @Test
    public void testSubscribeOn() throws InterruptedException {
        TestObserver<String> observer = new TestObserver<>();
        List<String> threadNames = Collections.synchronizedList(new ArrayList<>());

        Observable.<String>create(emitter -> {
            threadNames.add(Thread.currentThread().getName());
            emitter.onNext("Test");
            emitter.onComplete();
        })
        .subscribeOn(Schedulers.io())
        .subscribe(observer);

        observer.await(); // wait for async completion

        assertTrue(observer.isCompleted());
        assertEquals(1, observer.getValues().size());
        assertEquals("Test", observer.getValues().get(0));
        
        // Ensure it ran on the IO scheduler thread
        assertTrue(threadNames.get(0).startsWith("RxIOThread"));
    }

    @Test
    public void testObserveOn() throws InterruptedException {
        TestObserver<String> observer = new TestObserver<>();
        List<String> mapThreadNames = Collections.synchronizedList(new ArrayList<>());
        List<String> observerThreadNames = Collections.synchronizedList(new ArrayList<>());

        Observable.<String>create(emitter -> {
            emitter.onNext("Event");
            emitter.onComplete();
        })
        .map(item -> {
            mapThreadNames.add(Thread.currentThread().getName());
            return item + "Mapped";
        })
        .observeOn(Schedulers.computation())
        .map(item -> {
            observerThreadNames.add(Thread.currentThread().getName());
            return item;
        })
        .subscribe(observer);

        observer.await();

        assertTrue(observer.isCompleted());
        assertEquals("EventMapped", observer.getValues().get(0));

        // First map is before observeOn, so it runs on main thread (or subscribe thread)
        assertEquals("main", mapThreadNames.get(0));
        
        // Second map is after observeOn, so it runs on computation thread
        assertTrue(observerThreadNames.get(0).startsWith("RxComputationThreadPool"));
    }

    @Test
    public void testSubscribeOnAndObserveOn() throws InterruptedException {
        TestObserver<String> observer = new TestObserver<>();
        List<String> sourceThreadNames = Collections.synchronizedList(new ArrayList<>());
        List<String> observerThreadNames = Collections.synchronizedList(new ArrayList<>());
        CountDownLatch latch = new CountDownLatch(1);

        Observable.<String>create(emitter -> {
            sourceThreadNames.add(Thread.currentThread().getName());
            emitter.onNext("Data");
            emitter.onComplete();
        })
        .subscribeOn(Schedulers.io())
        .observeOn(Schedulers.single())
        .map(item -> {
            observerThreadNames.add(Thread.currentThread().getName());
            return item;
        })
        .subscribe(new com.customrx.core.Observer<String>() {
            @Override
            public void onNext(String item) {
                observer.onNext(item);
            }

            @Override
            public void onError(Throwable t) {
                observer.onError(t);
                latch.countDown();
            }

            @Override
            public void onComplete() {
                observer.onComplete();
                latch.countDown();
            }
        });

        latch.await(2, TimeUnit.SECONDS);

        assertTrue(sourceThreadNames.get(0).startsWith("RxIOThread"));
        assertTrue(observerThreadNames.get(0).startsWith("RxSingleThread"));
        assertEquals("Data", observer.getValues().get(0));
    }
}
