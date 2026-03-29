package structures;

public class SinglyLinkedList implements IntStructure {
    private static final int LIST_OVERHEAD_BYTES = 24;
    private static final int NODE_BYTES = 24;

    private Node head;
    private int size;

    private static final class Node {
        private int value;
        private Node next;

        private Node(int value) {
            this.value = value;
        }
    }

    @Override
    public void insertFirst(int value) {
        Node node = new Node(value);
        node.next = head;
        head = node;
        size++;
    }

    @Override
    public void insertLast(int value) {
        Node node = new Node(value);
        if (head == null) {
            head = node;
            size++;
            return;
        }

        Node current = head;
        while (current.next != null) {
            current = current.next;
        }
        current.next = node;
        size++;
    }

    @Override
    public void insertMiddle(int value) {
        int index = size / 2;
        if (index == 0) {
            insertFirst(value);
            return;
        }

        Node previous = getNodeAt(index - 1);
        Node node = new Node(value);
        node.next = previous.next;
        previous.next = node;
        size++;
    }

    @Override
    public boolean delete(int value) {
        if (head == null) {
            return false;
        }
        if (head.value == value) {
            head = head.next;
            size--;
            return true;
        }

        Node previous = head;
        Node current = head.next;
        while (current != null) {
            if (current.value == value) {
                previous.next = current.next;
                size--;
                return true;
            }
            previous = current;
            current = current.next;
        }
        return false;
    }

    @Override
    public int search(int value) {
        Node current = head;
        int index = 0;
        while (current != null) {
            if (current.value == value) {
                return index;
            }
            current = current.next;
            index++;
        }
        return -1;
    }

    @Override
    public int get(int index) {
        return getNodeAt(index).value;
    }

    @Override
    public void replace(int index, int value) {
        getNodeAt(index).value = value;
    }

    private Node getNodeAt(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", size: " + size);
        }
        Node current = head;
        for (int i = 0; i < index; i++) {
            current = current.next;
        }
        return current;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void clear() {
        head = null;
        size = 0;
    }

    @Override
    public long memoryBytes() {
        return LIST_OVERHEAD_BYTES + ((long) size * NODE_BYTES);
    }
}
