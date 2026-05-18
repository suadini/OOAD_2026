package Assignment_2;

import org.junit.jupiter.api.Test;

import Assignment_2.CircularArray;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

// Tests for CircularArray - the storage layer
// One assert per test, Arrange-Act-Assert structure

public class CircularArrayTest {

    @Test
    void getSize() {
        CircularArray<String> arr = new CircularArray<>(5);

        int size = arr.getSize();

        assertEquals(5, size);
    }

    @Test
    void getOnNewArrayReturnsNull() {
        CircularArray<String> arr = new CircularArray<>(3);

        String value = arr.get(0);

        assertNull(value);
    }

    @Test
    void putThenGet() {
        CircularArray<String> arr = new CircularArray<>(4);

        arr.put(0, "hello");

        assertEquals("hello", arr.get(0));
    }

    @Test
    void putWrapsAroundCapacity() {
        CircularArray<String> arr = new CircularArray<>(3);
        arr.put(0, "first");

        arr.put(3, "wrapped"); // 3 % 3 == 0

        assertEquals("wrapped", arr.get(0));
    }

    @Test
    void getWrapsAroundCapacity() {
        CircularArray<String> arr = new CircularArray<>(4);
        arr.put(2, "item");

        String value = arr.get(6); // 6 % 4 == 2

        assertEquals("item", value);
    }

    @Test
    void putOverwritesSameSlot() {
        CircularArray<String> arr = new CircularArray<>(2);
        arr.put(0, "old");

        arr.put(0, "new");

        assertEquals("new", arr.get(0));
    }

    @Test
    void putNullStoresNull() {
        CircularArray<String> arr = new CircularArray<>(2);
        arr.put(0, "value");

        arr.put(0, null);

        assertNull(arr.get(0));
    }

    @Test
    void constructorRejectsZeroSize() {
        assertThrows(IllegalArgumentException.class, () -> new CircularArray<String>(0));
    }

    @Test
    void constructorRejectsNegativeSize() {
        assertThrows(IllegalArgumentException.class, () -> new CircularArray<String>(-3));
    }

    @Test
    void putDoesNotAffectOtherSlots() {
        CircularArray<String> arr = new CircularArray<>(3);

        arr.put(0, "a");

        assertNull(arr.get(1));
    }
}
