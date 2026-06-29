package com.customrx.core;

import com.customrx.schedulers.Scheduler;
import java.util.concurrent.atomic.AtomicBoolean;

class ObservableSubscribeOn<T> extends Observable<T> {

    private final Observable<T> source;
    private final Scheduler scheduler;

    ObservableSubscribeOn(Observable<T> source, Scheduler scheduler) {
        this.source = source;
        this.scheduler = scheduler;
    }

    @Override
    protected void subscribeActual(Observer<? super T> observer, AtomicBoolean isDisposed) {
        scheduler.execute(() -> {
            if (!isDisposed.get()) {
                source.subscribeActual(observer, isDisposed);
            }
        });
    }
}
