public class MyLinkedList {
    private static class Node {
        int value;
        Node next;

        Node(int value) {
            this.value = value;
        }
    }

    private Node head;
    private Node tail;
    private int size;
    private final OperationMetrics metrics = new OperationMetrics();

    public void add(int value) {
        Node node = new Node(value);

        if (size == 0) {
            head = node;
            metrics.addMove();
        } else {
            tail.next = node;
            metrics.addMove();
        }

        tail = node;
        metrics.addMove();
        size++;
    }

    public void add(int index, int value) {
        checkPositionIndex(index);

        if (index == size) {
            add(value);
            return;
        }

        Node node = new Node(value);

        if (index == 0) {
            node.next = head;
            metrics.addMove();
            head = node;
            metrics.addMove();
        } else {
            Node previous = nodeAt(index - 1);
            node.next = previous.next;
            metrics.addMove();
            previous.next = node;
            metrics.addMove();
        }

        size++;
    }

    public int remove(int index) {
        checkElementIndex(index);

        if (index == 0) {
            int removed = head.value;
            head = head.next;
            metrics.addMove();
            size--;

            if (size == 0) {
                tail = null;
                metrics.addMove();
            }
            return removed;
        }

        Node previous = nodeAt(index - 1);
        Node current = previous.next;
        metrics.addStep();

        int removed = current.value;
        previous.next = current.next;
        metrics.addMove();

        if (current == tail) {
            tail = previous;
            metrics.addMove();
        }

        size--;
        return removed;
    }

    public int get(int index) {
        checkElementIndex(index);
        return nodeAt(index).value;
    }

    public boolean contains(int value) {
        Node current = head;

        while (current != null) {
            metrics.addComparison();
            if (current.value == value) {
                return true;
            }

            if (current.next != null) {
                metrics.addStep();
            }
            current = current.next;
        }

        return false;
    }

    public int size() {
        return size;
    }

    public OperationMetrics metrics() {
        return metrics;
    }

    private Node nodeAt(int index) {
        Node current = head;

        for (int i = 0; i < index; i++) {
            current = current.next;
            metrics.addStep();
        }

        return current;
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