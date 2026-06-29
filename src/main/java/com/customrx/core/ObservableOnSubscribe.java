package com.customrx.core;

/**
 * A functional interface that has a {@code subscribe()} method that receives
 * an instance of an {@link Emitter} instance that allows pushing
 * events in a cancellation-safe, cohesive manner.
 *
 * @param <T> the value type pushed
 */
public interface ObservableOnSubscribe<T> {

    /**
     * Called for each Observer that subscribes.
     *
     * @param emitter the safe emitter instance, never null
     * @throws Exception on error
     */
    void subscribe(Emitter<T> emitter) throws Exception;
}
