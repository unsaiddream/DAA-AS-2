public class DynamicArray {
    private int[] elements;
    private int size;
    private final OperationMetrics metrics = new OperationMetrics();

    public DynamicArray() {
        elements = new int[4];
    }

    public void add(int value) {
        ensureCapacity();
        elements[size] = value;
        size++;
    }

    public void add(int index, int value) {
        checkPositionIndex(index);
        ensureCapacity();

        for (int i = size; i > index; i--) {
            elements[i] = elements[i - 1];
            metrics.addStep();
            metrics.addMove();
        }

        elements[index] = value;
        size++;
    }

    public int remove(int index) {
        checkElementIndex(index);

        int removed = elements[index];
        metrics.addStep();

        for (int i = index; i < size - 1; i++) {
            elements[i] = elements[i + 1];
            metrics.addStep();
            metrics.addMove();
        }

        size--;
        return removed;
    }

    public int get(int index) {
        checkElementIndex(index);
        metrics.addStep();
        return elements[index];
    }

    public boolean contains(int value) {
        for (int i = 0; i < size; i++) {
            int current = elements[i];
            metrics.addStep();
            metrics.addComparison();

            if (current == value) {
                return true;
            }
        }
        return false;
    }

    public int size() {
        return size;
    }

    public OperationMetrics metrics() {
        return metrics;
    }

    private void ensureCapacity() {
        if (size < elements.length) {
            return;
        }

        int[] larger = new int[elements.length * 2];

        for (int i = 0; i < size; i++) {
            larger[i] = elements[i];
            metrics.addStep();
            metrics.addMove();
        }

        elements = larger;
    }

    private void checkElementIndex(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException(
                    "index: " + index + ", size: " + size
            );
        }
    }

    private void checkPositionIndex(int index) {
        if (index < 0 || index > size) {
            throw new IndexOutOfBoundsException(
                    "index: " + index + ", size: " + size
            );
        }
    }
}