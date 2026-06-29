package com.customrx.core;

import com.customrx.schedulers.Scheduler;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * The Observable class is the non-backpressured, base reactive class.
 */
public abstract class Observable<T> {

    /**
     * Subclasses must implement this method to handle the actual subscription.
     *
     * @param observer the observer to receive items
     * @param isDisposed a flag that indicates if the subscription was cancelled
     */
    protected abstract void subscribeActual(Observer<? super T> observer, AtomicBoolean isDisposed);

    /**
     * Subscribes to the Observable and provides an Observer to receive events.
     *
     * @param observer the observer
     * @return a Disposable to cancel the subscription
     */
    public final Disposable subscribe(Observer<? super T> observer) {
        AtomicBoolean isDisposed = new AtomicBoolean(false);
        Disposable disposable = new Disposable() {
            @Override
            public void dispose() {
                isDisposed.set(true);
            }

            @Override
            public boolean isDisposed() {
                return isDisposed.get();
            }
        };

        Observer<T> wrapper = new Observer<T>() {
            @Override
            public void onNext(T item) {
                if (!isDisposed.get()) {
                    observer.onNext(item);
                }
            }

            @Override
            public void onError(Throwable t) {
                if (!isDisposed.get()) {
                    isDisposed.set(true);
                    observer.onError(t);
                }
            }

            @Override
            public void onComplete() {
                if (!isDisposed.get()) {
                    isDisposed.set(true);
                    observer.onComplete();
                }
            }
        };

        try {
            subscribeActual(wrapper, isDisposed);
        } catch (Throwable t) {
            wrapper.onError(t);
        }

        return disposable;
    }

    /**
     * Creates an Observable from the given ObservableOnSubscribe.
     */
    public static <T> Observable<T> create(ObservableOnSubscribe<T> source) {
        return new ObservableCreate<>(source);
    }

    public final <R> Observable<R> map(Function<? super T, ? extends R> mapper) {
        return new ObservableMap<>(this, mapper);
    }

    public final Observable<T> filter(Predicate<? super T> predicate) {
        return new ObservableFilter<>(this, predicate);
    }

    public final <R> Observable<R> flatMap(Function<? super T, Observable<R>> mapper) {
        return new ObservableFlatMap<>(this, mapper);
    }

    public final Observable<T> subscribeOn(Scheduler scheduler) {
        return new ObservableSubscribeOn<>(this, scheduler);
    }

    public final Observable<T> observeOn(Scheduler scheduler) {
        return new ObservableObserveOn<>(this, scheduler);
    }
}
