package project;
import javafx.fxml.FXML;
import java.io.*;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import java.util.Scanner;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
/**
 * Jude Lieb
 * Chess game interface and engine
 * Start Date: 06/2022
 * End Date: Ongoing
 * Version 10
 */

public class Controller {
	@FXML
	GridPane grid;
	@FXML
	ImageView background;
    @FXML
    Button exitBtn;
    @FXML
    Button simulateBtn;
    @FXML
    Button winLabel;
	
    //File names of images
    String[] names = {"blank.jpg", "wp.png", "wn.png","wb.png","wr.png","wq.png",
    		"wk.png", "bp.png","bn.png","bb.png","br.png","bq.png", "bk.png"};
    
    static ImageView iViews[][] = new ImageView[8][8];
    ImageView selectedImage = new ImageView();
    int[] moveAmount = {0, 4, 8, 28, 28, 56, 8, 4, 8, 28, 28, 56, 8}; 
	int[] values = {0,1,3,3,5,9,20,1,3,3,5,9,20};
    Image[] images = new Image[13];
	Piece[] pieces = new Piece[13];
    Game primary;
    Crd init = new Crd(0,0);
    Crd dest = new Crd(0,0);
	boolean mode = true;
	
	//Main function sets up game and event handlers
	public void start() throws IOException {
		Image bak = new Image("board.png");

		//Default board state
		int[] set = {10,8,9,11,12,9,8,10,7,7,7,7,7,7,7,7,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
				0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,1,1,1,1,1,1,4,2,3,5,6,3,2,4};
		
		//int[] set = new int[64];
		background.setImage(bak); //Setting background
		winLabel.setVisible(false);
		
		//Change board start set here
		
		//User game created
		primary = new Game(set, pieces, values);
		
		//Preparing to read x and y shifts for each pieces' moves
		File file = new File("newMoves.txt");
		Scanner scan = new Scanner(file);
		int readInt1;
		int readInt2;
		//Setting up list of piece images
		//Creating each piece type and reading shifts
		for(int i = 0; i < 13; i++) { 
			images[i] = new Image(names[i]);
			Crd[] temp = new Crd[moveAmount[i]];
			for(int j = 0; j < moveAmount[i]; j++) {
				//Reading move coordinates from file
				readInt1 = Integer.parseInt(scan.next());
				readInt2 = Integer.parseInt(scan.next());
				temp[j] = new Crd(readInt2, readInt1);
			}
			pieces[i] = new Piece(i, temp);
		}
		scan.close();
		
		//Setting image views for visual board
	    int count = 0;
	    for(int y = 0; y < 8; y++) {
	    	for(int x = 0; x < 8; x++, count++) {
	    		ImageView temp = new ImageView(images[set[count]]);
	    		grid.getChildren().removeAll();
	    		iViews[y][x] = temp;
	    		temp.setFitHeight(80);
	    		temp.setPreserveRatio(true);
	    		grid.add(temp, x, y);
	    	}
	    }
	    
	    grid.setOnMouseClicked(e -> { 
	    	//A square has been clicked. 
	    	//Mode determines whether destination or source square is being selected
	    	int y = ((int) Math.round(e.getY()))/80; //x coord of mouse
			int x = ((int) Math.round(e.getX()))/80; //y coord of mouse
	    	selectedImage.setFitHeight(80);
	    	selectedImage = iViews[y][x];
	    	
			if(mode) {//Piece Selection
				init = new Crd(x, y); 
				if(primary.startSelect(init)) {
					selectedImage.setFitHeight(90);
					mode = false;
				}
			} else { //Destination square selection
				dest = new Crd(x, y);
				Crd target = new Crd((dest.x - init.x), (dest.y - init.y));
				
				if(primary.destSelect(init, dest, target)) {
					//Player's move entered
					primary.move(init, dest);
					primary.colorSwap();
					//primary.printBoard();
					moveImage(init, dest);
					if(!updateStatus(primary, primary.getStatus())) {
						//Computer makes a move
						Mv moveChoice = primary.compMove();
						updateStatus(primary, primary.getStatus());
						moveImage(moveChoice);
			    		primary.colorSwap();
					}
				} else {
					System.out.println("Invalid Move");
				}
				mode = true;
			}
		});
	    
	    //Allows automatic moves to be made
	    simulateBtn.setOnMouseClicked(e -> {
	    	Mv selection;
	    	for(int k = 0; k < 50; k++) {
	    		if(updateStatus(primary, primary.getStatus())) {
	    			break;
	    		}
	    		selection = primary.compMove();
	    		moveImage(selection);
	    		primary.colorSwap();
	    	}
	    });
	}
	
	//Computer makes one move
	public void simulateOne() {
		Mv selection;
		selection = primary.compMove();
		moveImage(selection);
		primary.colorSwap();
    	updateStatus(primary, primary.getStatus());
	}
	
	//Check mate notification
	public boolean updateStatus(Game game, int status ) {
		if(status > 1) {
			//grid.setVisible(false);
			winLabel.setVisible(true);
			//simulateBtn.setVisible(false);
			//exitBtn.setVisible(false);
			return true;
		} 
		return false;
	}
	
	//Moving coordinates
	public void moveImage(Crd coord, Crd dest) {
		iViews[dest.y][dest.x].setImage(images[primary.grid.board[dest.y][dest.x]]);
		iViews[coord.y][coord.x].setImage(images[0]);	
	}
	
	//Shifts piece images in grid
	public void moveImage(Mv m) {
		iViews[m.endY][m.endX].setImage(images[primary.grid.board[m.endY][m.endX]]);
		iViews[m.startY][m.startX].setImage(images[0]);	
	}

	public void initialize() throws IOException {
		start();
	}

	public void exit() {
		System.exit(0);
	}
}