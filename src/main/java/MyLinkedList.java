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

    public void add(int value) {
        Node node = new Node(value);

        if (size == 0) {
            head = node;
        } else {
            tail.next = node;
        }

        tail = node;
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
            head = node;
        } else {
            Node previous = nodeAt(index - 1);
            node.next = previous.next;
            previous.next = node;
        }

        size++;
    }

    public int remove(int index) {
        checkElementIndex(index);

        int removed;

        if (index == 0) {
            removed = head.value;
            head = head.next;
            size--;

            if (size == 0) {
                tail = null;
            }
            return removed;
        }

        Node previous = nodeAt(index - 1);
        Node current = previous.next;
        removed = current.value;
        previous.next = current.next;

        if (current == tail) {
            tail = previous;
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
            if (current.value == value) {
                return true;
            }
            current = current.next;
        }

        return false;
    }

    public int size() {
        return size;
    }

    private Node nodeAt(int index) {
        Node current = head;

        for (int i = 0; i < index; i++) {
            current = current.next;
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