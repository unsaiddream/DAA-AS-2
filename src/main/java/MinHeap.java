public class MinHeap {
    private int[] elements;
    private int size;
    private final OperationMetrics metrics = new OperationMetrics();

    public MinHeap() {
        elements = new int[4];
    }

    public void insert(int value) {
        ensureCapacity();

        int index = size;
        elements[index] = value;
        size++;

        while (index > 0) {
            int parent = (index - 1) / 2;

            if (compareAt(parent, index) <= 0) {
                break;
            }

            swap(parent, index);
            index = parent;
        }
    }

    public int peekMin() {
        checkNotEmpty();
        return read(0);
    }

    public int extractMin() {
        checkNotEmpty();

        int minimum = read(0);
        size--;

        if (size == 0) {
            return minimum;
        }

        elements[0] = read(size);
        metrics.addMove();
        int index = 0;

        while (true) {
            int left = 2 * index + 1;
            int right = left + 1;

            if (left >= size) {
                break;
            }

            int smallerChild = left;
            if (right < size && compareAt(right, left) < 0) {
                smallerChild = right;
            }

            if (compareAt(index, smallerChild) <= 0) {
                break;
            }

            swap(index, smallerChild);
            index = smallerChild;
        }

        return minimum;
    }

    public int size() {
        return size;
    }

    public OperationMetrics metrics() {
        return metrics;
    }

    private int read(int index) {
        metrics.addStep();
        return elements[index];
    }

    private int compareAt(int first, int second) {
        int firstValue = read(first);
        int secondValue = read(second);
        metrics.addComparison();
        return Integer.compare(firstValue, secondValue);
    }

    private void swap(int first, int second) {
        int firstValue = read(first);
        int secondValue = read(second);

        elements[first] = secondValue;
        metrics.addMove();
        elements[second] = firstValue;
        metrics.addMove();
    }

    private void ensureCapacity() {
        if (size < elements.length) {
            return;
        }

        int[] larger = new int[elements.length * 2];
        for (int i = 0; i < size; i++) {
            larger[i] = read(i);
            metrics.addMove();
        }
        elements = larger;
    }

    private void checkNotEmpty() {
        if (size == 0) {
            throw new IllegalStateException("Heap is empty");
        }
    }
}