package project;
import java.util.Arrays;

public class MoveStack {
    private Move[] stackArray;
    private int index;
    private int capacity;

    public MoveStack() {
        stackArray = new Move[100];
        capacity = 100;
        index = -1;
    }

    public void push(Move move) {
        //resizing stack when full
        if (index == capacity - 1) {
            capacity += 50;
            stackArray = Arrays.copyOf(stackArray, capacity);
        }

        stackArray[++index] = move;
    }

    public Move pop() {
        if (index == -1) {
            return null;
        }
        return stackArray[index--];
    }

    public Move peek() {
        if (index == -1) {
            return null;
        }
        return stackArray[index];
    }

    public int size() {
        return index + 1;
    }
}