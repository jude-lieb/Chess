package project;

public class Player {

    Crd king;
	int material;
    int promoteType;
    boolean queenside;
    boolean kingside;
    String title;
    Mod[] ks;
    Mod[] qs;

	public Player(String type) {
        title = type;
        material = 0;
        ks = new Mod[4];
        qs = new Mod[4];
        if(type.equals("White")){
            promoteType = 5;
            //White Qingside
            ks[0] = new Mod(new Crd(7, 4), 6, 0);
            ks[1] = new Mod(new Crd(7, 6), 0, 6);
            ks[2] = new Mod(new Crd(7, 7), 4, 0);
            ks[3] = new Mod(new Crd(7, 5), 0, 4);

            //White Queenside
            qs[0] = new Mod(new Crd(7, 4), 6, 0);
            qs[1] = new Mod(new Crd(7, 2), 0, 6);
            qs[2] = new Mod(new Crd(7, 0), 4, 0);
            qs[3] = new Mod(new Crd(7, 3), 0, 4);
        } else {
            promoteType = 11;
            //Black Kingside
            ks[0] = new Mod(new Crd(0, 4), 12, 0);
            ks[1] = new Mod(new Crd(0, 6), 0, 12);
            ks[2] = new Mod(new Crd(0, 7), 10, 0);
            ks[3] = new Mod(new Crd(0, 5), 0, 10);

            //Black Queenside
            qs[0] = new Mod(new Crd(0, 4), 12, 0);
            qs[1] = new Mod(new Crd(0, 2), 0, 12);
            qs[2] = new Mod(new Crd(0, 0), 10, 0);
            qs[3] = new Mod(new Crd(0, 3), 0, 10);
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