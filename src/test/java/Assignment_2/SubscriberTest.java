package Assignment_2;

import org.junit.jupiter.api.Test;

import Assignment_2.CircularArray;
import Assignment_2.CircularBuffer;
import Assignment_2.Subscriber;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

// Tests for Subscriber - the independent read cursor
// One assert per test, Arrange-Act-Assert structure
// Two tests at the bottom isolate Subscriber with a hand-written fake of CircularArray

public class SubscriberTest {

    @Test
    void readReturnsWrittenItem() {
        CircularBuffer<String> buf = new CircularBuffer<>(3);
        Subscriber<String> sub = buf.subscribe();
        buf.write("hello");

        String value = sub.read();

        assertEquals("hello", value);
    }

    @Test
    void readAdvancesReadHead() {
        CircularBuffer<String> buf = new CircularBuffer<>(3);
        Subscriber<String> sub = buf.subscribe();
        buf.write("a");

        sub.read();

        assertEquals(1, sub.getReadHead());
    }

    @Test
    void readEmptyBufferReturnsNull() {
        CircularBuffer<String> buf = new CircularBuffer<>(3);
        Subscriber<String> sub = buf.subscribe();

        String value = sub.read();

        assertNull(value);
    }

    @Test
    void readPastWriteHeadReturnsNull() {
        CircularBuffer<String> buf = new CircularBuffer<>(3);
        Subscriber<String> sub = buf.subscribe();
        buf.write("a");
        sub.read(); // consumes "a", cursor now at 1, nothing was written at seq 1

        String value = sub.read();

        assertNull(value);
    }

    @Test
    void getReadHeadDoesNotAdvance() {
        CircularBuffer<String> buf = new CircularBuffer<>(3);
        Subscriber<String> sub = buf.subscribe();
        buf.write("a");

        sub.getReadHead();
        sub.getReadHead();

        assertEquals("a", sub.read());
    }

    @Test
    void subscribersAreIndependent() {
        CircularBuffer<String> buf = new CircularBuffer<>(3);
        Subscriber<String> sub1 = buf.subscribe();
        Subscriber<String> sub2 = buf.subscribe();
        buf.write("a");
        buf.write("b");

        sub1.read();
        sub1.read();

        assertEquals(0, sub2.getReadHead());
    }

    @Test
    void readOverwrittenSlotReturnsNewValue() {
        // The writer laps a slow subscriber; the subscriber silently sees the new value
        CircularBuffer<String> buf = new CircularBuffer<>(2);
        Subscriber<String> sub = buf.subscribe();
        buf.write("a"); // seq 0 -> slot 0
        buf.write("b"); // seq 1 -> slot 1
        buf.write("c"); // seq 2 -> slot 0 (overwrites "a")

        String value = sub.read(); // reads seq 0, which now holds "c"

        assertEquals("c", value);
    }

    // --- Tests below isolate Subscriber from CircularArray with a hand-written fake.
    // --- The fake records which sequence number Subscriber asked for, so we can
    // --- verify behavior without depending on the real CircularArray's logic.

    // Fake CircularArray that ignores its size and remembers the last sequence asked for.
    private static class FakeArray extends CircularArray<String> {
        long lastSequenceAsked = -1;
        String valueToReturn = "stub-value";

        FakeArray() {
            super(1); // size is irrelevant for this fake; just satisfies the constructor
        }

        @Override
        public String get(long seq) {
            lastSequenceAsked = seq;
            return valueToReturn;
        }
    }

    @Test
    void readAsksBackingArrayForCurrentSeq() {
        FakeArray fake = new FakeArray();
        Subscriber<String> sub = new Subscriber<>(fake, 7);

        sub.read();

        assertEquals(7, fake.lastSequenceAsked);
    }

    @Test
    void readReturnsBackingArrayValue() {
        FakeArray fake = new FakeArray();
        fake.valueToReturn = "from-the-fake";
        Subscriber<String> sub = new Subscriber<>(fake, 0);

        String value = sub.read();

        assertEquals("from-the-fake", value);
    }
}
