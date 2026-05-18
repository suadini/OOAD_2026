package Assignment_2;

import org.junit.jupiter.api.Test;

import Assignment_2.CircularBuffer;
import Assignment_2.Subscriber;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

// Tests for CircularBuffer - the public buffer object
// One assert per test, Arrange-Act-Assert structure

public class CircularBufferTest {

    @Test
    void getCapacity() {
        CircularBuffer<String> buf = new CircularBuffer<>(5);

        int capacity = buf.getCapacity();

        assertEquals(5, capacity);
    }

    @Test
    void writeHeadStartsAtZero() {
        CircularBuffer<String> buf = new CircularBuffer<>(3);

        long head = buf.getWriteHead();

        assertEquals(0, head);
    }

    @Test
    void writeAdvancesWriteHead() {
        CircularBuffer<String> buf = new CircularBuffer<>(3);

        buf.write("a");
        buf.write("b");
        buf.write("c");

        assertEquals(3, buf.getWriteHead());
    }

    @Test
    void writePastCapacityKeepsAdvancing() {
        // Writer must always succeed; capacity 2 with 4 writes still moves head to 4
        CircularBuffer<String> buf = new CircularBuffer<>(2);

        buf.write("a");
        buf.write("b");
        buf.write("c");
        buf.write("d");

        assertEquals(4, buf.getWriteHead());
    }

    @Test
    void subscribeReturnsSubscriber() {
        CircularBuffer<String> buf = new CircularBuffer<>(3);

        Subscriber<String> sub = buf.subscribe();

        assertNotNull(sub);
    }

    @Test
    void newSubscriberStartsAtZero() {
        CircularBuffer<String> buf = new CircularBuffer<>(3);

        Subscriber<String> sub = buf.subscribe();

        assertEquals(0, sub.getReadHead());
    }

    @Test
    void subscribeStartsAtWriteHead() {
        CircularBuffer<String> buf = new CircularBuffer<>(3);
        buf.write("a");
        buf.write("b");

        Subscriber<String> sub = buf.subscribe();

        assertEquals(2, sub.getReadHead());
    }

    @Test
    void twoSubscribersStartAtSamePosition() {
        CircularBuffer<String> buf = new CircularBuffer<>(3);
        buf.write("a");
        Subscriber<String> sub1 = buf.subscribe();

        Subscriber<String> sub2 = buf.subscribe();

        assertEquals(sub1.getReadHead(), sub2.getReadHead());
    }

    @Test
    void constructorRejectsZeroCapacity() {
        assertThrows(IllegalArgumentException.class, () -> new CircularBuffer<String>(0));
    }

    @Test
    void constructorRejectsNegativeCapacity() {
        assertThrows(IllegalArgumentException.class, () -> new CircularBuffer<String>(-1));
    }
}
