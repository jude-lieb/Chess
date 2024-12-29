package project;
/**
 * Leftovers Class
 * Holding currently unused but potentially useful code
 */

public class Leftovers {
//	//Returns number of times the other color is currently attacking a given square
//		public int squareTargets(int color, Crd start, Crd square) {
//			int type, x, y;
//			int totalAttacks = 0;
//			for(int i = 0; i < 8; i++) {
//				for(int q = 0; q < 8; q++) {
//					type = board[i][q];
//					if(type != 0 && colorCompare(type, color)) {
//						if(q == start.x && i == start.y) {
//							continue;
//						}
//						Crd[] moves = pieces[type].getMoves();
//						for(int j = 0; j < moves.length; j++) {
//							x = q + moves[j].x;
//							y = i + moves[j].y;
//							if(y < 8 && y > -1 && x < 8 && x > -1) {
//								if(y == square.y && x == square.x) {
//									//Fixing pawns attacking forward
//									if(type == 1 || type == 7) {
//										if(moves[j].x != 0) {
//											totalAttacks++;
//											//System.out.println("Attack confirmed: " + type);
//											//System.out.println(type + " " + moves[j].x);
//										}
//									} else {
//										if(systemChecks(new Crd(q,i),new Crd(x, y))) {
//											totalAttacks++;
//											//System.out.println("Attack confirmed: " + type);
//										} else {
//											//System.out.println("Check Failed. " + type);
//										}
//									}
//								}
//							}
//						}
//					}
//				}
//			}
//			return totalAttacks;
//		}
}
