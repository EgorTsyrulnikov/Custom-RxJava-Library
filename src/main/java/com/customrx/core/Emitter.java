package com.customrx.core;

/**
 * Base interface for emitting signals in a push-fashion.
 *
 * @param <T> the value type emitted
 */
public interface Emitter<T> {

    /**
     * Signal a normal value.
     *
     * @param value the value to signal, not null
     */
    void onNext(T value);

    /**
     * Signal a Throwable exception.
     *
     * @param error the Throwable to signal, not null
     */
    void onError(Throwable error);

    /**
     * Signal a completion.
     */
    void onComplete();
    
    /**
     * Returns true if the downstream is disposed and will no longer accept events.
     * 
     * @return true if the downstream is disposed
     */
    boolean isDisposed();
}
