package project;

public class Mod {
    static int[] values = {0,1,3,3,5,9,20,1,3,3,5,9,20};
    Crd square;
    int type;
    int replace;

    public Mod(Crd s, int t, int r) {
        square = s;
        type = t;
        replace = r;
    }

    public void apply(int[][] board) {
        board[square.y][square.x] = replace;
    }   

    public void reverse(int[][] board) {
        board[square.y][square.x] = type;
    }
}