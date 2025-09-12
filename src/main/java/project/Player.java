package project;

public class Player {
    Crd king;
	int material;
    int promoteType;
    boolean canCastle;
    boolean queenside;
    boolean kingside;
    boolean isTurn;
    String title;

	public Player(String type) {
		title = type;
        material = 0;
        if(type.equals("white")){
            promoteType = 5;
        } else {
            promoteType = 11;
        }
	}
}