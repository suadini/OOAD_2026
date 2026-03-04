package OO.Assignment_2B;

import java.util.concurrent.atomic.AtomicReferenceArray;

/**
 * Manages the fixed-size backing array for a circular buffer.
 * Responsible solely for slot-level storage and retrieval via modular indexing.
 * Has no awareness of sequence counters, readers, or writers.
 */
public class CircularArray<T> {

    private final AtomicReferenceArray<T> slots;
    private final int size;

    /**
     * Allocates a backing array of the given size.
     * @param size number of slots; must be a positive integer
     */
    public CircularArray(int size) {
        if (size < 1) {
            throw new IllegalArgumentException("Array size must be at least 1");
        }
        this.size = size;
        this.slots = new AtomicReferenceArray<>(size);
    }

    /**
     * Writes an item into the slot mapped from the given sequence number.
     * Overwrites whatever was previously in that slot without warning.
     */
    public void put(long seq, T item) {
        slots.set(toIndex(seq), item);
    }

    /**
     * Returns the item currently occupying the slot mapped from the given sequence.
     * Returns null if nothing has been written to that slot yet.
     */
    public T get(long seq) {
        return slots.get(toIndex(seq));
    }

    /** Returns the total number of slots in this array. */
    public int getSize() {
        return size;
    }

    /** Maps a monotonically increasing sequence number to a physical array index. */
    private int toIndex(long seq) {
        return (int) (seq % size);
    }
}
