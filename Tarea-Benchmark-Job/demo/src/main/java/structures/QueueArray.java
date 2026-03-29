package structures;

public class QueueArray implements IntStructure {
    private int[] data;
    private int count;

    public QueueArray() {
        this.data = new int[10];
        this.count = 0;
    }

    private void resize() {
        int[] newData = new int[data.length * 2];
        System.arraycopy(data, 0, newData, 0, count);
        data = newData;
    }

    @Override
    public void insertFirst(int val) {
        insertAt(0, val);
    }

    @Override
    public void insertLast(int val) {
        if (count == data.length) resize();
        data[count++] = val;
    }

    @Override
    public void insertMiddle(int val) {
        insertAt(count / 2, val);
    }

    private void insertAt(int index, int val) {
        if (index < 0 || index > count) return;
        if (count == data.length) resize();
        for (int i = count; i > index; i--) {
            data[i] = data[i - 1];
        }
        data[index] = val;
        count++;
    }

    @Override
    public boolean delete(int index) {
        if (index < 0 || index >= count) return false;
        for (int i = index; i < count - 1; i++) {
            data[i] = data[i + 1];
        }
        count--;
        return true;
    }

    @Override
    public int search(int val) {
        for (int i = 0; i < count; i++) {
            if (data[i] == val) return i;
        }
        return -1;
    }

    @Override
    public int get(int index) {
        if (index < 0 || index >= count) throw new IndexOutOfBoundsException();
        return data[index];
    }

    @Override
    public void replace(int index, int val) {
        if (index >= 0 && index < count) {
            data[index] = val;
        }
    }

    @Override
    public int size() {
        return count;
    }

    @Override
    public void clear() {
        count = 0;
    }

    @Override
    public long memoryBytes() {
        return 16 + (data.length * 4L);
    }
}