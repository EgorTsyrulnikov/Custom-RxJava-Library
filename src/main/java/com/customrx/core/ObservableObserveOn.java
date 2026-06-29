package com.customrx.core;

import com.customrx.schedulers.Scheduler;
import java.util.concurrent.atomic.AtomicBoolean;

class ObservableObserveOn<T> extends Observable<T> {

    private final Observable<T> source;
    private final Scheduler scheduler;

    ObservableObserveOn(Observable<T> source, Scheduler scheduler) {
        this.source = source;
        this.scheduler = scheduler;
    }

    @Override
    protected void subscribeActual(Observer<? super T> observer, AtomicBoolean isDisposed) {
        source.subscribeActual(new Observer<T>() {
            @Override
            public void onNext(T item) {
                if (isDisposed.get()) return;
                scheduler.execute(() -> {
                    if (!isDisposed.get()) {
                        observer.onNext(item);
                    }
                });
            }

            @Override
            public void onError(Throwable t) {
                if (isDisposed.get()) return;
                scheduler.execute(() -> {
                    if (!isDisposed.get()) {
                        observer.onError(t);
                    }
                });
            }

            @Override
            public void onComplete() {
                if (isDisposed.get()) return;
                scheduler.execute(() -> {
                    if (!isDisposed.get()) {
                        observer.onComplete();
                    }
                });
            }
        }, isDisposed);
    }
}
