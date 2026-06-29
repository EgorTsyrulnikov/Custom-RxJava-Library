package com.customrx.core;

import java.util.function.Function;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

class ObservableFlatMap<T, R> extends Observable<R> {

    private final Observable<T> source;
    private final Function<? super T, Observable<R>> mapper;

    ObservableFlatMap(Observable<T> source, Function<? super T, Observable<R>> mapper) {
        this.source = source;
        this.mapper = mapper;
    }

    @Override
    protected void subscribeActual(Observer<? super R> observer, AtomicBoolean isDisposed) {
        // active count starts at 1 for the main source
        AtomicInteger activeCount = new AtomicInteger(1);
        
        source.subscribeActual(new Observer<T>() {
            @Override
            public void onNext(T item) {
                if (isDisposed.get()) return;
                
                try {
                    Observable<R> innerObservable = mapper.apply(item);
                    activeCount.incrementAndGet();
                    
                    innerObservable.subscribeActual(new Observer<R>() {
                        @Override
                        public void onNext(R innerItem) {
                            if (!isDisposed.get()) {
                                // Serialize access to downstream onNext if needed, 
                                // but for basic requirements, calling directly is acceptable
                                observer.onNext(innerItem);
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
                            if (activeCount.decrementAndGet() == 0 && !isDisposed.get()) {
                                observer.onComplete();
                            }
                        }
                    }, isDisposed);
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
                if (activeCount.decrementAndGet() == 0 && !isDisposed.get()) {
                    observer.onComplete();
                }
            }
        }, isDisposed);
    }
}
