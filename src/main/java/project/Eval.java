package project;
import java.util.Random;
/**
 * Eval Class
 * Scans legal move tree for high scoring moves.
 * Sorts moves by score and randomly breaks ties
 */
public class Eval {
	int[] values = {0,1,3,3,5,9,20,1,3,3,5,9,20};
	int totalCount = 0;
    Game g;

    public Eval(Game g) {
        this.g = g;
    }

	//Tree search alpha-beta pruning algorithm (roughly based on wikihow code example)
	public int getScore(int depth, int alpha, int beta){
		//Terminating condition (currently depth 1)
		if (depth == 2) {
			totalCount++;
			return g.materialDiff();
		}

		//Setting up move array and count
		int moveCount = g.list.size();
		
		//maximizing black score
		if (g.color > 6){
			int best = -100;
			
			//Recursively searching each move in array
			for (int i = 0; i < moveCount; i++){
				//Moving to new position for analysis
                Move stat = g.list.get(i);
				g.move(stat);
				
				//Recursive call to next position (then undo move)
				int val = getScore(depth + 1, alpha, beta);
				g.undoMove();
				
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
				Move stat = g.list.get(i);
				g.move(stat);
				
				//Recursive call to next position (then undo move)
				int val = getScore(depth + 1, alpha, beta);
				g.undoMove();
				
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
	public void pickBestMove() {
		int count = g.list.size();

		if(count == 0) {
			System.out.println("No Legal Computer Moves.");
			return;
		}
		
		double[] scores = new double[count];
		
		//Evaluating the position after each legal move
		for(int i = 0; i < count; i++) {
			g.move(g.list.get(i));
			scores[i] = getScore(0, -100, 100);
			g.undoMove();
		}
		
		//Insertion sorting of moves and scores
		double temp;
		Move tempMove;
		for (int i = 1; i < count; i++){
		    for (int j = i; j > 0 && scores[j-1] < scores[j]; j--){
		        temp = scores[j];
		        tempMove = g.list.get(j);
		        scores[j] = scores[j-1];
		        g.list.set(j, g.list.get(j-1));
		        scores[j-1] = temp;
		        g.list.set(j-1, tempMove);
		    }
		}

		//Displaying results
		// System.out.println();
		// for(int i = 0; i < count; i++) {
		// 	System.out.printf("%.2f ", scores[i]);
		// }
		// System.out.println();
		// System.out.println("Total visited positions: " + totalCount);

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
			Move temp1 = g.list.get(0);
			g.list.set(0, g.list.get(index));
			g.list.set(index, temp1);
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