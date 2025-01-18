package project;
import javafx.fxml.FXML;
import java.io.*;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import java.util.Scanner;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
/**
 * Controller Class 
 * JavaFX component controls and user input
 * Initialization of game
 */
public class Controller {
	@FXML
	GridPane grid;
	@FXML
	Button changeBtn;
	@FXML
	ImageView background;
    @FXML
    Button exitBtn;
    @FXML
    Button winLabel;
	
    //File names for images
    String[] names = {"blank.jpg", "wp.png", "wn.png","wb.png","wr.png","wq.png",
    		"wk.png", "bp.png","bn.png","bb.png","br.png","bq.png", "bk.png"};
    
    //FXML Image grid
    static ImageView iViews[][] = new ImageView[8][8];
    ImageView selectedImage = new ImageView();
    
    int[] promoteTypes = {2,3,4,5};
    
    //Number of potential moves for each piece
    int[] moveAmount = {0, 4, 8, 28, 28, 56, 8, 4, 8, 28, 28, 56, 8}; 
    //Relative material values of pieces (king excluded)
	int[] values = {0,1,3,3,5,9,20,1,3,3,5,9,20};
	
	//Images for each of the piece types
    Image[] images = new Image[13];
    
    //Stores potential move coordinate shifts for each type
    Piece[] pieces = new Piece[13];
    
    //User piece selection mode (start square or end square)
    boolean mode = true;
    
    //Start and end square coordinates
    Crd init = new Crd(0,0);
    Crd dest = new Crd(0,0);
    
    Grid gameGrid;
    CrdPair[] moves;
	
	//Sets up game and event handlers
	public void start() throws IOException {
		Image bak = new Image("board.png");

		//Default board state
		int[] set = {10,8,9,11,12,9,8,10,7,7,7,7,7,7,7,7,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
				0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,1,1,1,1,1,1,4,2,3,5,6,3,2,4};
		
		//Alternate starting position configuration
//		int[] set = new int[64];
//		set[15] = 1;
//		set[16] = 1;
//		set[54] = 12;
		
		//Setting background
		background.setImage(bak); 
		winLabel.setVisible(false);
		
		//User game created
		gameGrid = new Grid(set, pieces, 39, 39, 6, 5);
		
		//Preparing to read x and y shifts for each pieces' moves
		File file = new File("newMoves.txt");
		Scanner scan = new Scanner(file);
		int readInt1, readInt2;
		
		//Setting up list of images (one for each piece type)
		//Creating each piece type
		//Reading all potential piece coordinate shifts from text file
		for(int i = 0; i < 13; i++) { 
			images[i] = new Image(names[i]);
			Crd[] temp = new Crd[moveAmount[i]];
			for(int j = 0; j < moveAmount[i]; j++) {
				readInt1 = Integer.parseInt(scan.next());
				readInt2 = Integer.parseInt(scan.next());
				temp[j] = new Crd(readInt1, readInt2);
			}
			pieces[i] = new Piece(i, temp);
		}
		scan.close();
		
		//Finding legal moves in starting position
		updateLegalMoves();
		
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
 	    
	    //Event handling for user move selections
	    grid.setOnMouseClicked(e -> { 
	    	//A square has been clicked.
	    	int y = ((int) Math.round(e.getY()))/80; //Getting x coord of mouse
			int x = ((int) Math.round(e.getX()))/80; //Getting y coord of mouse
			
			//Changing image size to show focus
	    	selectedImage.setFitHeight(80);
	    	selectedImage = iViews[y][x];
	    	
			if(mode) { //Selecting starting square
				init = new Crd(y, x); 
				if(!Grid.colorCompare(gameGrid.board[init.y][init.x], gameGrid.color)) {
					selectedImage.setFitHeight(90);
					mode = false;
				}
			} else { //Selecting destination square
				dest = new Crd(y, x);
				CrdPair chosenMove = new CrdPair(init.y, init.x, y, x);
				
				//If legal move choice, move and run computer response move
				for(int i = 0; i < 100 && moves[i] != null; i++) {
					if(moves[i].equals(chosenMove)) {
						enterMove(moves[i]);
						//computerPlay();
						updateLegalMoves();
						gameGrid.print();
						break;
					}
				}
				
				mode = true;
			}
		});
	}
	
	public void updateLegalMoves() {
		moves = new CrdPair[100];
	    gameGrid.getLegalMoves(moves, gameGrid.color);
	}
	
	//Entering player move choices (updating board contents)
	public void enterMove(CrdPair move) {
		gameGrid.move(new Move(gameGrid, move));	
		updateImages(gameGrid);
	}
	
	//Entering computer move choices (updating board contents)
	public void computerPlay() {
		gameGrid.compMove();
		updateImages(gameGrid);
	}
	
	//Refreshing images on board using board array
	public void updateImages(Grid gameGrid) {
		for(int i = 0; i < 8; i++) {
			for(int j = 0; j < 8; j++) {
				iViews[i][j].setImage(images[gameGrid.board[i][j]]);
			}
		}	
	}

	public void undo() {
		gameGrid.undoMove();
		updateImages(gameGrid);
		updateLegalMoves();
		gameGrid.print();
	}
	
	public void changePromotion() {
		if(gameGrid.promote < 5) {
			gameGrid.promote++;
		} else {
			gameGrid.promote = 2;
		}
		changeBtn.setText("Promote " + gameGrid.promote);
	}
	
	//Setting up everything
	public void initialize() throws IOException {
		start();
	}
	
	public void exit() {
		System.exit(0);
	}
}