package project;
import java.util.*;
/**
 * Eval Class
 * Scans legal move tree for high scoring moves.
 * Sorts moves by score and randomly breaks ties
 */
public class Eval {
	int[] values = {0,1,3,3,5,9,20,1,3,3,5,9,20};
	int totalCount = 0;

	//Tree search alpha-beta pruning algorithm (roughly based on wikihow code example)
	public int getScore(int depth, Grid current, int color, int alpha, int beta){
		//Terminating condition (currently depth 1)
		if (depth == 2) {
			totalCount++;
			return current.positionEval();
		}

		//Setting up move array and count
		current.findLegalMoves();
		int moveCount = current.legalMoveCount;
		
		//maximizing black score
		if (color > 6){
			int best = -100;
			
			//Recursively searching each move in array
			for (int i = 0; i < moveCount; i++){
				
				if(current.moves[i] == null) {
					System.out.println("Index " + i);
				}
				//Moving to new position for analysis
				Move stat = new Move(current, current.moves[i]);
				current.move(stat);
				
				//Recursive call to next position (then undo move)
				int val = getScore(depth + 1, current, otherColor(color), alpha, beta);
				current.undoMove();
				
				//Compare with previous scores
				best = Math.max(best, val);
				alpha = Math.max(alpha, best);
				
				//Trimming branches to reduce total calculation
				if (beta <= alpha) {
					break;
				}
			}
			return best;
		} else {
			int best = 100;
			
			//Recursively searching each move in array
			for (int i = 0; i < moveCount; i++){
				
				
				if(current.moves[i] == null) {
					System.out.println("Index " + i);
				}
				Move stat = new Move(current, current.moves[i]);
				current.move(stat);
				
				//Recursive call to next position (then undo move)
				int val = getScore(depth + 1, current, otherColor(color), alpha, beta);
				current.undoMove();
				
				//Compare with previous scores
				best = Math.min(best, val);
				beta = Math.min(beta, best);
				
				//Trimming branches to reduce total calculation
				if (beta <= alpha) {
					break;
				}
			}
			return best;
		}
	}

	//Gets all legal moves and generates scores for each
	//Sorts scores to find highest and breaks ties when necessary
	public CrdPair getBestMove(Grid grid, int color) {
		int count = grid.legalMoveCount;
		
		if(count == 0) {
			System.out.println("No Legal Computer Moves.");
			return null;
		}
		
		double[] scores = new double[count];
		
		//Evaluating the position after each legal move
		for(int i = 0; i < count; i++) {
			Move stat = new Move(grid, grid.moves[i]);
			grid.move(stat);
			scores[i] = getScore(0, grid, otherColor(color), -100, 100);
			grid.undoMove();
		}
		
		//Insertion sorting of moves and scores
		double temp;
		CrdPair tempCrdPair;
		for (int i = 1; i < count; i++){
		    for (int j = i; j > 0 && scores[j-1] < scores[j]; j--){
		        temp = scores[j];
		        tempCrdPair = grid.moves[j];
		        scores[j] = scores[j-1];
		        grid.moves[j] = grid.moves[j-j];
		        scores[j-1] = temp;
		        grid.moves[j-1] = tempCrdPair;
		    }
		}

		//Displaying results
		System.out.println();
		for(int i = 0; i < count; i++) {
			System.out.printf("%.2f ", scores[i]);
		}
		System.out.println();
		System.out.println("Total visited positions: " + totalCount);

		//Finding how many moves are tied in score
		double topScore = scores[0];
		int tieCount = 0;
		for(int i = 0; i < count; i++) {
			tieCount = i;
			if(scores[i] < topScore) {
				break;
			}
		}
		
		//Handling two or more tied move scores with random break
		if(tieCount > 1) {
			Random rand = new Random();
			int index = rand.nextInt(tieCount);
			CrdPair temp1 = grid.moves[0];
			grid.moves[0] = grid.moves[index];
			grid.moves[index] = temp1;
		}
	}
	
	//Gets opposite color
	public static int otherColor(int color) {
		if(color > 6) {
			return 6;
		} else {
			return 12;
		}
	}
}