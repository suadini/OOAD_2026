package OO.Assignment_2B;

import java.util.concurrent.atomic.AtomicLong;

/**
 * An independent read cursor attached to a {@link CircularBuffer}.
 *
 * <p>Each subscriber maintains its own sequence counter so that multiple
 * subscribers can consume the same buffer at different rates without
 * interfering with each other. Consuming an item does not remove it from
 * the buffer; it only advances this subscriber's local cursor.
 *
 * <p>If the writer has overwritten the slot that this subscriber is about to
 * read, the method returns whatever is currently in that slot (which may be
 * newer data or null). Callers must handle this case if ordering guarantees
 * are required.
 */
public class Subscriber<T> {

    private final CircularArray<T> array;
    private final AtomicLong readHead;

    /**
     * Package-private — subscribers are created via {@link CircularBuffer#subscribe()}.
     * @param array         the shared backing array
     * @param startSequence the sequence position at which this subscriber begins reading
     */
    Subscriber(CircularArray<T> array, long startSequence) {
        this.array = array;
        this.readHead = new AtomicLong(startSequence);
    }

    /**
     * Reads the item at the current read position and advances the cursor by one.
     * @return the item at the current position, or null if nothing was written there
     */
    public T read() {
        long seq = readHead.getAndIncrement();
        return array.get(seq);
    }

    /** Returns the sequence number of the next item this subscriber will read. */
    public long getReadHead() {
        return readHead.get();
    }
}
