package OO.Assignment_2B;

import java.util.concurrent.atomic.AtomicLong;

/**
 * The central buffer object. Owns the write sequence counter and the backing
 * array, and acts as the factory for {@link Subscriber} instances.
 *
 * <p>Only a single thread should call {@link #publish} at a time (single-writer
 * contract). Any number of threads may call {@link #subscribe} to obtain
 * independent read cursors.
 *
 * <p>When the write position laps a slow subscriber, that subscriber's next
 * read will return stale or null data — no exception is thrown.
 */
public class CircularBuffer<T> {

    private final CircularArray<T> array;
    private final AtomicLong nextWrite;

    /**
     * Creates a buffer with the specified capacity.
     * @param capacity maximum number of items held before overwriting begins
     */
    public CircularBuffer(int capacity) {
        this.array = new CircularArray<>(capacity);
        this.nextWrite = new AtomicLong(0L);
    }

    /**
     * Writes one item to the buffer at the current write position, then
     * advances the write sequence by one.
     */
    public void write(T item) {
        long seq = nextWrite.getAndIncrement();
        array.put(seq, item);
    }

    /**
     * Creates and returns a new {@link Subscriber} positioned at the current
     * write head. The subscriber will read items published from this point on.
     */
    public Subscriber<T> subscribe() {
        return new Subscriber<>(array, nextWrite.get());
    }

    /** Returns the sequence number at which the next write will occur. */
    public long getWriteHead() {
        return nextWrite.get();
    }

    /** Returns the fixed capacity of this buffer. */
    public int getCapacity() {
        return array.getSize();
    }
}
