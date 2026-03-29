package structures;

public class DoublyLinkedList implements IntStructure {
    private static final int LIST_OVERHEAD_BYTES = 32;
    private static final int NODE_BYTES = 32;

    private Node head;
    private Node tail;
    private int size;

    private static final class Node {
        private int value;
        private Node prev;
        private Node next;

        private Node(int value) {
            this.value = value;
        }
    }

    @Override
    public void insertFirst(int value) {
        Node node = new Node(value);
        if (head == null) {
            head = node;
            tail = node;
        } else {
            node.next = head;
            head.prev = node;
            head = node;
        }
        size++;
    }

    @Override
    public void insertLast(int value) {
        Node node = new Node(value);
        if (tail == null) {
            head = node;
            tail = node;
        } else {
            tail.next = node;
            node.prev = tail;
            tail = node;
        }
        size++;
    }

    @Override
    public void insertMiddle(int value) {
        int index = size / 2;
        if (index == 0) {
            insertFirst(value);
            return;
        }
        if (index == size) {
            insertLast(value);
            return;
        }

        Node current = getNodeAt(index);
        Node previous = current.prev;
        Node node = new Node(value);

        previous.next = node;
        node.prev = previous;
        node.next = current;
        current.prev = node;
        size++;
    }

    @Override
    public boolean delete(int value) {
        Node current = head;
        while (current != null) {
            if (current.value == value) {
                if (current.prev != null) {
                    current.prev.next = current.next;
                } else {
                    head = current.next;
                }

                if (current.next != null) {
                    current.next.prev = current.prev;
                } else {
                    tail = current.prev;
                }

                size--;
                return true;
            }
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

        if (index < size / 2) {
            Node current = head;
            for (int i = 0; i < index; i++) {
                current = current.next;
            }
            return current;
        }

        Node current = tail;
        for (int i = size - 1; i > index; i--) {
            current = current.prev;
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
        tail = null;
        size = 0;
    }

    @Override
    public long memoryBytes() {
        return LIST_OVERHEAD_BYTES + ((long) size * NODE_BYTES);
    }
}
