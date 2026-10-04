import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.PriorityQueue;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class MinHeapTest {

    @Test
    void emptyHeapThrows() {
        MinHeap heap = new MinHeap();

        assertEquals(0, heap.size());
        assertThrows(IllegalStateException.class, heap::peekMin);
        assertThrows(IllegalStateException.class, heap::extractMin);
    }

    @Test
    void oneElementCanBeInsertedAndExtracted() throws Exception {
        MinHeap heap = new MinHeap();

        heap.insert(7);
        assertHeapProperty(heap);
        assertEquals(7, heap.peekMin());
        assertEquals(7, heap.extractMin());
        assertEquals(0, heap.size());
        assertHeapProperty(heap);
    }

    @Test
    void extractsValuesInSortedOrder() throws Exception {
        MinHeap heap = new MinHeap();
        int[] values = {5, 1, 9, 1, -3, 8, 0};

        for (int value : values) {
            heap.insert(value);
            assertHeapProperty(heap);
        }

        int[] expected = {-3, 0, 1, 1, 5, 8, 9};
        for (int value : expected) {
            assertEquals(value, heap.extractMin());
            assertHeapProperty(heap);
        }
    }

    @Test
    void randomOperationsMatchPriorityQueue() throws Exception {
        MinHeap actual = new MinHeap();
        PriorityQueue<Integer> expected = new PriorityQueue<>();
        Random random = new Random(42);

        for (int i = 0; i < 500; i++) {
            if (expected.isEmpty() || random.nextBoolean()) {
                int value = random.nextInt(200) - 100;
                actual.insert(value);
                expected.add(value);
            } else {
                assertEquals(expected.remove().intValue(), actual.extractMin());
            }

            assertEquals(expected.size(), actual.size());
            if (!expected.isEmpty()) {
                assertEquals(expected.peek().intValue(), actual.peekMin());
            }
            assertHeapProperty(actual);
        }
    }

    private void assertHeapProperty(MinHeap heap) throws Exception {
        Field field = MinHeap.class.getDeclaredField("elements");
        field.setAccessible(true);
        int[] elements = (int[]) field.get(heap);

        for (int child = 1; child < heap.size(); child++) {
            int parent = (child - 1) / 2;
            assertTrue(elements[parent] <= elements[child],
                    "Heap property broken at child index " + child);
        }
    }
    @Test
    void countsReadsMovesAndComparisons() {
        MinHeap heap = new MinHeap();
        heap.insert(3);

        heap.metrics().reset();
        heap.insert(1);
        assertEquals(4, heap.metrics().getSteps());
        assertEquals(2, heap.metrics().getMoves());
        assertEquals(1, heap.metrics().getComparisons());

        heap.metrics().reset();
        assertEquals(1, heap.extractMin());
        assertEquals(2, heap.metrics().getSteps());
        assertEquals(1, heap.metrics().getMoves());
        assertEquals(0, heap.metrics().getComparisons());
    }
}