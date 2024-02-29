package project;
import java.util.Random;
/**
 * Game Class 
 * Determines automatic move choice
 * Manages game status
 */
public class Game {
	Grid grid;
	int color;
	int bMat;
	int wMat;
	int bCount;
	int wCount;
	int[] values;
	
	public Mv compMove() {
		Mv[] moves = new Mv[200];
		Mv selection;
		Mv tempMv;
		
		int count = grid.getLegalMoves(moves, color);
		//No legal moves
		if(count == 0) {
			return null;
		}
		
		int[] scores = getEvals(this, count, color, moves);
		int temp;
		//Insertion sorting of moves and scores
		for (int i = 1; i < count; i++){
		    for (int j = i; j > 0 && scores[j-1] < scores[j]; j--){
		        temp = scores[j];
		        tempMv = moves[j];
		        scores[j] = scores[j-1];
		        moves[j] = moves[j-j];
		        scores[j-1] = temp;
		        moves[j-1] = tempMv;
		    }
		}
		
//		System.out.println("Move scores");
//		for(int i = 0; i < count; i++) {
//			System.out.print(scores[i] + " ");
//		}
//		System.out.println();

		//Finding if there are any score ties
		int topScore = scores[0];
		int tieCount = 0;
		for(int i = 0; i < count; i++) {
			tieCount = i;
			if(scores[i] < topScore) {
				break;
			}
		}
		
		if(tieCount > 1) {
			//System.out.println("Tie count = " + tieCount);
			Random rand = new Random();
			int num = rand.nextInt(tieCount);
			//System.out.println("Random Selection: " + num);
			selection = moves[num];
		} else {
			selection = moves[0];
		}
	
		//System.out.println("Legal Move Count: " + count);		
		move(selection.getInit(), selection.getDest());
		return selection;
	}	
	
	//Returns int score for all moves
	public int[] getEvals(Game game, int count, int color, Mv[] moves) {
		int[] answer = new int[count];
		for(int i = 0; i < count; i++) {
			answer[i] = evaluate(game, moves[i], color);
		}
		return answer;
	}
	
	//Returns int score based on positional situation
	public int evaluate(Game game, Mv move, int color) {
		int answer = 0;
		Grid g1 = new Grid(game.grid.cloneArray(), game.grid.pieces);
		Crd start = move.getInit();
		Crd dest = move.getDest();
		//Starting and desting square material values
		int v1 = values[g1.board[start.y][start.x]];
		int v2 = values[g1.board[dest.y][dest.x]];
		
		if(grid.board[dest.y][dest.x] != 0) {
			//System.out.println("V1 " + v1 + " V2 " + v2);
			answer++;
			if(v1 < v2) {
				answer = answer + 1;
			}
		}
		
		g1.move(start, dest);
		int bTargets = grid.squareTargets(color, start, dest);
		int gTargets = grid.squareTargets(otherColor(color),start, dest);
		//System.out.println("Targets: " + move.destY + " " + move.destX + " "+ gTargets + " " + bTargets);
		
		if(gTargets >= bTargets) {
			answer++;
		} else {
			if(v1 <= v2) {
				answer++;
			} else {
				answer = answer - 2;
			}
		}
	
		if(g1.inCheck(otherColor(color))) {
			answer++;
			if(g1.checkMate(otherColor(color))) {
				answer += 200;
			}
		}
		return answer;
	}

	//For when a piece is selected by user
	public boolean startSelect(Crd init) {
		return !Grid.colorCompare(grid.board[init.y][init.x], color);
	}
	
	//Destination square selected by user
	public boolean destSelect(Crd init, Crd mv, Crd target) {
		if(target.x == 0 && target.y == 0) { //unselecting piece, no move made
			System.out.println("Move Cancelled");
			return false;
		} else {
			if(isLegal(init, mv, target)) {
				return true;
			} else {
				return false;
			}
		}
	}
	
	public boolean isLegal(Crd init, Crd dest, Crd shift) {
		if(Grid.colorCompare(grid.board[init.y][init.x],grid.board[dest.y][dest.x])){
			return grid.isMoveLegal(init, dest, shift, color);
		}
		return false;
	}
	
	public boolean isCheckMate() {
		return grid.checkMate(color) || grid.checkMate(otherColor(color));
	}
	
	//Determines whether game is stalemate or checkmate
	public int getStatus() {
		if(grid.getLegalMoves(color) == 0) {
			if(grid.inCheck(color)) {
				if(color == 6) {
					return 2; //checkmate white
				} else {
					return 3; //checkmate black
				}
			} else {
				return 1; //stalemate
			}
		} else {
			return 0; //not checkmate or stalemate
		} 
	}

	public void move(Crd coord, Crd mv) { 
		//Subtracting material value and piece count if there is a capture
		if(grid.board[mv.y][mv.x] != 0) {
			if(grid.board[coord.y][coord.x] > 6) {
				wMat = wMat - values[grid.board[mv.y][mv.x]];
				//System.out.println(values[grid.board[mv.y][mv.x]]);
				wCount--;
			} else {
				bMat = bMat - values[grid.board[mv.y][mv.x]];
				//System.out.println(values[grid.board[mv.y][mv.x]]);
				bCount--;
			}
		}
		//white pawn promotes to queen
		if(grid.board[coord.y][coord.x] == 1 && mv.y == 0) {
			grid.board[coord.y][coord.x] = 5;
			wMat += 8;
		}
		//If black pawn reaches other side, promotes to queen
		if(grid.board[coord.y][coord.x] == 7 && mv.y == 7) {
			grid.board[coord.y][coord.x] = 11;
			bMat += 8;
		}
		grid.move(coord, mv);		
	}
	
	public Game(int[] set, Piece[] pieces, int[] values) {
		this.values = values;
		int[][] temp = new int[8][8];
		int count = 0; 
		for(int i = 0; i < 8; i++) {
			for(int j = 0; j < 8; j++) {
				temp[i][j] = set[count];	
				count++;
			}
		}
		grid = new Grid(temp, pieces);
		color = 6;
		bMat = 39;
		wMat = 39;
		bCount = 16;
		wCount = 16;
	}
	
	public void colorSwap() { 
		if(color == 12) {
			color = 6;
		} else {
			color = 12;
		}
	}		
	
	public int otherColor(int color) {
		if(color < 7) {
			return 12;
		} else {
			return 6;
		}
	}
	
	public void printBoard() {
		for(int i = 0; i < 8; i++) {
			System.out.println();
			for(int j = 0; j < 8; j++) {
				System.out.printf("%-2s ", grid.board[i][j]);
			}
		}
		System.out.println("\nWMat: " + wMat);
		System.out.println("BMat: " + bMat);
		System.out.println();
	}
}