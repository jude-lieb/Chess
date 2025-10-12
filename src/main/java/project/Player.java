package project;

public class Player {
    Crd king;
	int material;
    int promoteType;
    boolean queenside;
    boolean kingside;

	public Player(String type) {
        material = 0;
        if(type.equals("white")){
            promoteType = 5;
        } else {
            promoteType = 11;
        }
	}

    public void changePromotion() {
		if(promoteType < 5) {
			promoteType++;
		} else {
			promoteType = 2;
		}
	}
}