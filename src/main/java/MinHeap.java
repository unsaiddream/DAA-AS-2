public class MinHeap {
    private int[] elements;
    private int size;

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

            if (elements[parent] <= elements[index]) {
                break;
            }

            swap(parent, index);
            index = parent;
        }
    }

    public int peekMin() {
        checkNotEmpty();
        return elements[0];
    }

    public int extractMin() {
        checkNotEmpty();

        int minimum = elements[0];
        size--;

        if (size == 0) {
            return minimum;
        }

        elements[0] = elements[size];
        int index = 0;

        while (true) {
            int left = 2 * index + 1;
            int right = left + 1;

            if (left >= size) {
                break;
            }

            int smallerChild = left;
            if (right < size && elements[right] < elements[left]) {
                smallerChild = right;
            }

            if (elements[index] <= elements[smallerChild]) {
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

    private void ensureCapacity() {
        if (size < elements.length) {
            return;
        }

        int[] larger = new int[elements.length * 2];
        for (int i = 0; i < size; i++) {
            larger[i] = elements[i];
        }
        elements = larger;
    }

    private void swap(int first, int second) {
        int temporary = elements[first];
        elements[first] = elements[second];
        elements[second] = temporary;
    }

    private void checkNotEmpty() {
        if (size == 0) {
            throw new IllegalStateException("heap is empty");
        }
    }
}