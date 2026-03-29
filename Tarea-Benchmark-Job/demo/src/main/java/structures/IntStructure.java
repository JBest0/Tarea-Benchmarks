package structures;

public interface IntStructure {
    void insertFirst(int value);

    void insertLast(int value);

    void insertMiddle(int value);

    boolean delete(int value);

    int search(int value);

    int get(int index);

    void replace(int index, int value);

    int size();

    void clear();

    long memoryBytes();
}
