package structures;

public class StackList implements IntStructure {
    private class Node {
        int value;
        Node next;
        Node(int value) { this.value = value; }
    }

    private Node head;
    private Node tail;
    private int count;

    @Override
    public void insertFirst(int val) {
        Node newNode = new Node(val);
        if (head == null) {
            head = tail = newNode;
        } else {
            newNode.next = head;
            head = newNode;
        }
        count++;
    }

    @Override
    public void insertLast(int val) {
        Node newNode = new Node(val);
        if (head == null) {
            head = tail = newNode;
        } else {
            tail.next = newNode;
            tail = newNode;
        }
        count++;
    }

    @Override
    public void insertMiddle(int val) {
        if (count == 0) {
            insertFirst(val);
            return;
        }
        int mid = count / 2;
        Node current = head;
        for (int i = 0; i < mid - 1; i++) {
            current = current.next;
        }
        Node newNode = new Node(val);
        newNode.next = current.next;
        current.next = newNode;
        if (newNode.next == null) tail = newNode;
        count++;
    }

    @Override
    public boolean delete(int index) {
        if (index < 0 || index >= count) return false;
        if (index == 0) {
            head = head.next;
            if (head == null) tail = null;
        } else {
            Node current = head;
            for (int i = 0; i < index - 1; i++) {
                current = current.next;
            }
            current.next = current.next.next;
            if (current.next == null) tail = current;
        }
        count--;
        return true;
    }

    @Override
    public int search(int val) {
        Node current = head;
        int index = 0;
        while (current != null) {
            if (current.value == val) return index;
            current = current.next;
            index++;
        }
        return -1;
    }

    @Override
    public int get(int index) {
        if (index < 0 || index >= count) throw new IndexOutOfBoundsException();
        Node current = head;
        for (int i = 0; i < index; i++) current = current.next;
        return current.value;
    }

    @Override
    public void replace(int index, int val) {
        if (index < 0 || index >= count) return;
        Node current = head;
        for (int i = 0; i < index; i++) current = current.next;
        current.value = val;
    }

    @Override
    public int size() {
        return count;
    }

    @Override
    public void clear() {
        head = tail = null;
        count = 0;
    }

    @Override
    public long memoryBytes() {
        return 24 + (count * 24L);
    }
}