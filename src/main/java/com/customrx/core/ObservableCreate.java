package com.customrx.core;

import java.util.concurrent.atomic.AtomicBoolean;

class ObservableCreate<T> extends Observable<T> {

    private final ObservableOnSubscribe<T> source;

    ObservableCreate(ObservableOnSubscribe<T> source) {
        this.source = source;
    }

    @Override
    protected void subscribeActual(Observer<? super T> observer, AtomicBoolean isDisposed) {
        CreateEmitter<T> emitter = new CreateEmitter<>(observer, isDisposed);
        try {
            source.subscribe(emitter);
        } catch (Throwable t) {
            emitter.onError(t);
        }
    }

    static final class CreateEmitter<T> implements Emitter<T> {

        private final Observer<? super T> observer;
        private final AtomicBoolean isDisposed;
        private boolean done;

        CreateEmitter(Observer<? super T> observer, AtomicBoolean isDisposed) {
            this.observer = observer;
            this.isDisposed = isDisposed;
        }

        @Override
        public void onNext(T value) {
            if (value == null) {
                onError(new NullPointerException("onNext called with null"));
                return;
            }
            if (!isDisposed.get() && !done) {
                observer.onNext(value);
            }
        }

        @Override
        public void onError(Throwable error) {
            if (error == null) {
                error = new NullPointerException("onError called with null");
            }
            if (!isDisposed.get() && !done) {
                done = true;
                observer.onError(error);
            }
        }

        @Override
        public void onComplete() {
            if (!isDisposed.get() && !done) {
                done = true;
                observer.onComplete();
            }
        }

        @Override
        public boolean isDisposed() {
            return isDisposed.get();
        }
    }
}
