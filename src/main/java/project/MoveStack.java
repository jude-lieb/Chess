package project;

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
        if (index == capacity - 1) {
            System.out.println("Stack full.");
            return;
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