package project;

public class BoardState {
    private String desc;
    private String status;
    private String turn;
    private int[] squares;
    private int[][] options;
    private int wMat, bMat, moveCount;
    public String getDesc() {
        return desc;
    }
    public void setDesc(String desc) {
        this.desc = desc;
    }
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public int[] getSquares() {
        return squares;
    }
    public void setSquares(int[] squares) {
        this.squares = squares;
    }
    public int[][] getOptions() {
        return options;
    }
    public void setOptions(int[][] options) {
        this.options = options;
    }
    public int getwMat() {
        return wMat;
    }
    public void setwMat(int wMat) {
        this.wMat = wMat;
    }
    public int getbMat() {
        return bMat;
    }
    public void setbMat(int bMat) {
        this.bMat = bMat;
    }
    public String getTurn() {
        return turn;
    }
    public void setTurn(String turn) {
        this.turn = turn;
    }
    public int getMoveCount() {
        return moveCount;
    }
    public void setMoveCount(int moveCount) {
        this.moveCount = moveCount;
    }
}
