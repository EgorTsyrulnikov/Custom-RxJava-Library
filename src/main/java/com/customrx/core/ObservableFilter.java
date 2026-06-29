package com.customrx.core;

import java.util.function.Predicate;
import java.util.concurrent.atomic.AtomicBoolean;

class ObservableFilter<T> extends Observable<T> {

    private final Observable<T> source;
    private final Predicate<? super T> predicate;

    ObservableFilter(Observable<T> source, Predicate<? super T> predicate) {
        this.source = source;
        this.predicate = predicate;
    }

    @Override
    protected void subscribeActual(Observer<? super T> observer, AtomicBoolean isDisposed) {
        source.subscribeActual(new Observer<T>() {
            @Override
            public void onNext(T item) {
                if (isDisposed.get()) return;
                try {
                    boolean pass = predicate.test(item);
                    if (pass) {
                        observer.onNext(item);
                    }
                } catch (Throwable t) {
                    observer.onError(t);
                }
            }

            @Override
            public void onError(Throwable t) {
                if (!isDisposed.get()) {
                    observer.onError(t);
                }
            }

            @Override
            public void onComplete() {
                if (!isDisposed.get()) {
                    observer.onComplete();
                }
            }
        }, isDisposed);
    }
}
