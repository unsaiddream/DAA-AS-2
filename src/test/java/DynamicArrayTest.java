import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DynamicArrayTest {

    @Test
    void newArrayIsEmpty() {
        DynamicArray array = new DynamicArray();

        assertEquals(0, array.size());
        assertFalse(array.contains(10));
        assertThrows(IndexOutOfBoundsException.class, () -> array.get(0));
        assertThrows(IndexOutOfBoundsException.class, () -> array.remove(0));
    }

    @Test
    void addGrowsArrayAndPreservesValues() {
        DynamicArray array = new DynamicArray();

        for (int i = 0; i < 100; i++) {
            array.add(i);
        }

        assertEquals(100, array.size());
        for (int i = 0; i < 100; i++) {
            assertEquals(i, array.get(i));
        }
    }

    @Test
    void addAtFirstMiddleAndLastIndex() {
        DynamicArray array = new DynamicArray();
        array.add(10);
        array.add(30);

        array.add(0, 5);
        array.add(2, 20);
        array.add(array.size(), 40);

        assertEquals(5, array.size());
        assertEquals(5, array.get(0));
        assertEquals(10, array.get(1));
        assertEquals(20, array.get(2));
        assertEquals(30, array.get(3));
        assertEquals(40, array.get(4));
    }

    @Test
    void removeShiftsValuesLeft() {
        DynamicArray array = new DynamicArray();
        array.add(10);
        array.add(20);
        array.add(30);

        assertEquals(20, array.remove(1));
        assertEquals(2, array.size());
        assertEquals(10, array.get(0));
        assertEquals(30, array.get(1));
    }

    @Test
    void containsHandlesDuplicates() {
        DynamicArray array = new DynamicArray();
        array.add(7);
        array.add(7);

        assertTrue(array.contains(7));
        assertFalse(array.contains(8));

        array.remove(0);
        assertTrue(array.contains(7));
    }

    @Test
    void invalidIndexesThrowException() {
        DynamicArray array = new DynamicArray();
        array.add(10);

        assertThrows(IndexOutOfBoundsException.class, () -> array.get(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> array.get(1));
        assertThrows(IndexOutOfBoundsException.class, () -> array.add(-1, 5));
        assertThrows(IndexOutOfBoundsException.class, () -> array.add(2, 5));
        assertThrows(IndexOutOfBoundsException.class, () -> array.remove(1));
    }
    @Test
    void countsPhysicalOperations() {
        DynamicArray array = new DynamicArray();
        for (int i = 0; i < 4; i++) {
            array.add(i);
        }

        array.metrics().reset();
        array.add(4);
        assertEquals(4, array.metrics().getSteps());
        assertEquals(4, array.metrics().getMoves());
        assertEquals(0, array.metrics().getComparisons());

        array.metrics().reset();
        assertFalse(array.contains(9));
        assertEquals(5, array.metrics().getSteps());
        assertEquals(0, array.metrics().getMoves());
        assertEquals(5, array.metrics().getComparisons());
    }
}