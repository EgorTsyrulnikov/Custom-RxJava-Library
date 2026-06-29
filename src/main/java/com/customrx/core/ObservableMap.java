package com.customrx.core;

import java.util.function.Function;
import java.util.concurrent.atomic.AtomicBoolean;

class ObservableMap<T, R> extends Observable<R> {

    private final Observable<T> source;
    private final Function<? super T, ? extends R> mapper;

    ObservableMap(Observable<T> source, Function<? super T, ? extends R> mapper) {
        this.source = source;
        this.mapper = mapper;
    }

    @Override
    protected void subscribeActual(Observer<? super R> observer, AtomicBoolean isDisposed) {
        source.subscribeActual(new Observer<T>() {
            @Override
            public void onNext(T item) {
                if (isDisposed.get()) return;
                try {
                    R result = mapper.apply(item);
                    observer.onNext(result);
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
