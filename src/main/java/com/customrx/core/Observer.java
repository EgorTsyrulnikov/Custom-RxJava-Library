package com.customrx.core;

/**
 * Provides a mechanism for receiving push-based notifications.
 *
 * @param <T> the type of item the Observer expects to observe
 */
public interface Observer<T> {

    /**
     * Provides the Observer with a new item to observe.
     *
     * @param item the item emitted by the Observable
     */
    void onNext(T item);

    /**
     * Notifies the Observer that the Observable has experienced an error condition.
     *
     * @param t the exception encountered
     */
    void onError(Throwable t);

    /**
     * Notifies the Observer that the Observable has finished sending push-based notifications.
     */
    void onComplete();
}
