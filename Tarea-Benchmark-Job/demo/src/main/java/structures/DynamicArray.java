package structures;

public class DynamicArray implements IntStructure {
    private static final int DEFAULT_CAPACITY = 16;
    private static final int ARRAY_HEADER_BYTES = 16;
    private static final int OBJECT_OVERHEAD_BYTES = 24;

    private int[] data;
    private int size;

    public DynamicArray() {
        this.data = new int[DEFAULT_CAPACITY];
        this.size = 0;
    }

    @Override
    public void insertFirst(int value) {
        insertAt(0, value);
    }

    @Override
    public void insertLast(int value) {
        insertAt(size, value);
    }

    @Override
    public void insertMiddle(int value) {
        insertAt(size / 2, value);
    }

    private void insertAt(int index, int value) {
        if (index < 0 || index > size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", size: " + size);
        }
        ensureCapacity(size + 1);
        if (size - index >= 0) {
            System.arraycopy(data, index, data, index + 1, size - index);
        }
        data[index] = value;
        size++;
    }

    private void ensureCapacity(int minCapacity) {
        if (minCapacity <= data.length) {
            return;
        }
        int newCapacity = data.length;
        while (newCapacity < minCapacity) {
            newCapacity *= 2;
        }
        int[] newData = new int[newCapacity];
        System.arraycopy(data, 0, newData, 0, size);
        data = newData;
    }

    @Override
    public boolean delete(int value) {
        int index = search(value);
        if (index == -1) {
            return false;
        }
        if (size - index - 1 > 0) {
            System.arraycopy(data, index + 1, data, index, size - index - 1);
        }
        size--;
        return true;
    }

    @Override
    public int search(int value) {
        for (int i = 0; i < size; i++) {
            if (data[i] == value) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public int get(int index) {
        validateIndex(index);
        return data[index];
    }

    @Override
    public void replace(int index, int value) {
        validateIndex(index);
        data[index] = value;
    }

    private void validateIndex(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", size: " + size);
        }
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void clear() {
        size = 0;
    }

    @Override
    public long memoryBytes() {
        return OBJECT_OVERHEAD_BYTES + ARRAY_HEADER_BYTES + ((long) data.length * Integer.BYTES);
    }
}
