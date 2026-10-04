import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class MyLinkedListTest {

    @Test
    void emptyListRejectsInvalidOperations() {
        MyLinkedList list = new MyLinkedList();

        assertEquals(0, list.size());
        assertFalse(list.contains(10));
        assertThrows(IndexOutOfBoundsException.class, () -> list.get(0));
        assertThrows(IndexOutOfBoundsException.class, () -> list.remove(0));
        assertThrows(IndexOutOfBoundsException.class, () -> list.add(1, 10));
    }

    @Test
    void insertsAtHeadMiddleAndTail() {
        MyLinkedList list = new MyLinkedList();

        list.add(20);
        list.add(0, 10);
        list.add(2, 40);
        list.add(2, 30);

        assertEquals(4, list.size());
        assertEquals(10, list.get(0));
        assertEquals(20, list.get(1));
        assertEquals(30, list.get(2));
        assertEquals(40, list.get(3));
    }

    @Test
    void removalsUpdateHeadAndTail() {
        MyLinkedList list = new MyLinkedList();
        list.add(10);
        list.add(20);
        list.add(30);

        assertEquals(10, list.remove(0));
        assertEquals(30, list.remove(1));
        list.add(40);

        assertEquals(20, list.get(0));
        assertEquals(40, list.get(1));
        assertEquals(40, list.remove(1));
        assertEquals(20, list.remove(0));

        list.add(50);
        assertEquals(1, list.size());
        assertEquals(50, list.get(0));
    }

    @Test
    void duplicatesAndInvalidIndexes() {
        MyLinkedList list = new MyLinkedList();
        list.add(7);
        list.add(7);

        assertTrue(list.contains(7));
        assertFalse(list.contains(8));
        assertThrows(IndexOutOfBoundsException.class, () -> list.get(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> list.get(2));
        assertThrows(IndexOutOfBoundsException.class, () -> list.add(3, 9));
        assertThrows(IndexOutOfBoundsException.class, () -> list.remove(2));

        list.remove(0);
        assertTrue(list.contains(7));
    }

    @Test
    void randomOperationsMatchArrayList() {
        MyLinkedList actual = new MyLinkedList();
        ArrayList<Integer> expected = new ArrayList<>();
        Random random = new Random(42);

        for (int operation = 0; operation < 500; operation++) {
            if (expected.isEmpty() || random.nextBoolean()) {
                int index = random.nextInt(expected.size() + 1);
                int value = random.nextInt(100);

                actual.add(index, value);
                expected.add(index, value);
            } else {
                int index = random.nextInt(expected.size());
                assertEquals(expected.remove(index).intValue(), actual.remove(index));
            }

            assertEquals(expected.size(), actual.size());
            for (int i = 0; i < expected.size(); i++) {
                assertEquals(expected.get(i).intValue(), actual.get(i));
            }
        }
    }
}
