import java.util.ArrayList;

public class Meander {

	PerfectMatching top;
	PerfectMatching bottom;
	int order;
	static int d = PerfectMatching.d; // diameter of points
	static int x = PerfectMatching.x; // horizontal separation of points



	public static void main(String[] args) {
		// System.out.println(howMany(3));
		// System.out.println(allMeanders(3).length);
		Meander[][] mbo = allMeandersSeparatedByOrder(5);
		for (int i = 0; i < mbo.length; i++) {
			System.out.println(mbo[i].length);
		}
	}
	


	/**
	 * returns a list containing each meander of the specified order (doesn't check if any meander is a flipped version of a meander already in the list)
	 */
	public static Meander[] allMeanders(int order) { 
		ArrayList<Meander> meanders = new ArrayList<Meander>();
		String[] perfectMatchings = Catalan.CnStrings(order);
		for (int i = 0; i < perfectMatchings.length; i++) {
			for (int j = 0; j < perfectMatchings.length; j++) {
				Meander meander = new Meander(new PerfectMatching(perfectMatchings[i]), new PerfectMatching(perfectMatchings[j]));
				if (meander.isMeander()) {
					meanders.add(meander);
					// meander.draw();
				}
			}
		}
		return meanders.toArray(new Meander[meanders.size()]);
	}

	/**
	 * @int order order of meanders
	 * @return array where array[k] is an array containing each meander that has its top on level k of G(n)
	 */
	public static Meander[][] allMeandersSeparatedByOrder(int order) {
		Meander[][] meanders = new Meander[order][]; // to be returned
		Meander[] allMeanders = allMeanders(order);
		for (int i = 0; i < order; i++) { // determine how many meanders are on each level (level of a meander is determined by the level of its top)
			int count = 0;
			for (Meander meander : allMeanders) {
				if (meander.top.level() == i+1) {
					count++;
				}
			}
			meanders[i] = new Meander[count];
		}

		int[] counts = new int[order]; // to keep track of how many meanders have been added to each array in meanders[][]
		for (Meander meander : allMeanders) {
			int lev = meander.top.level() - 1;
			meanders[lev][counts[lev]] = meander;
			counts[lev]++;
		}

		return meanders;
	}



	/**
	 * counts how many meanders there are of a given order
	 * @param order
	 */
	public static int howMany(int order) {
		int count = 0;
		String[] perfectMatchings = Catalan.CnStrings(order);
		boolean[][] matches = new boolean[perfectMatchings.length][perfectMatchings.length];
		for (int i = 0; i < perfectMatchings.length; i++) {
			for (int j = 0; j < perfectMatchings.length; j++) {
				if (!matches[i][j]) {
					Meander meander = new Meander(new PerfectMatching(perfectMatchings[i]), new PerfectMatching(perfectMatchings[j]));
					if (meander.isMeander()) {
						count++;
						matches[j][i] = false; // switch to true to stop counting upside-down versions of already counted meanders
					}
				}
			}
		}
		return count;
	}
	


	/**
	 * given a size (order), generates each possible pair of perfect matchings of that size (there are C(mOrder)^2 such pairs, where C(n) is the nth Catalan number),
	 * checks that a pair creates a meander (unless it's the same perfect matching on top and on bottom, in which case it is not a meander),
	 * and then makes every possible 01 move on top, and checks whether a compensating 	move on the bottom (to fix the meander) was found;
	 * if not, then draw that meander and return
	 *
	 * long story short, look for a meander where some 01 move on top has no compensating 01 move on the bottom
	 * 
	 * @param mOrder the desired size of each perfect matching
	 */
	public static void testMatchings(int mOrder) {
		String[] matchingInts = Catalan.CnStrings(mOrder);
		boolean done = false;
		for (int i = 0; i < matchingInts.length; i++) {
			for (int j = 0; j < matchingInts.length; j++) {
				//System.out.println(matchingInts[i]+", "+matchingInts[j]);
				if (j != i) {
					Meander meander = new Meander(new PerfectMatching(matchingInts[i]), new PerfectMatching(matchingInts[j]));
					if (meander.isMeander()) {
						for (int k = 0; k < 2*mOrder - 1; k++) {
							try {
								Meander second = new Meander(new PerfectMatching(matchingInts[i]), new PerfectMatching(matchingInts[j]));
								done = !(second.zeroOneMove(k, k+1, "t")); // 
							} catch (Exception e) {
								// there will be a lot of invalid moves, ie in a string starting with 11, since
								// trying to switch a 1 with another 1 will throw an exception
							}
							if (done) {
								meander.draw();
								return;
							}
							
						}
					}
				}
			}
		}
		
	}
	


	public Meander (PerfectMatching t, PerfectMatching b) {
		if (t == null) {
			throw new IllegalArgumentException("top is null");
		}
		if (b == null) {
			throw new IllegalArgumentException("bottom is null");
		}
		if (t.order != b.order) {
			throw new IllegalArgumentException("top and bottom are different sizes");
		}
		top = t;
		bottom = b;
		order = t.order;
	}



	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Meander))
			return false;
		Meander other = (Meander) obj;
		return (other.top.equals(top) && other.bottom.equals(bottom));
	}


	
	/**
	 * check whether it's actually a meander
	 * 
	 * @return true if you visited every point (so everything is on the same loop)
	 * 		   false otherwise
	 */
	public boolean isMeander() {
		int start = top.arcs[0][0]; // start at some point in the meander (can't predict where)
		int current = start;
		int counter = 0;
		do {
			int[] xy = {top.points[current][0], top.points[current][1]};
			xy[1] ^= 1;
			current = top.arcs[xy[0]][xy[1]];
			int[] wz = {bottom.points[current][0], bottom.points[current][1]};
			wz[1] ^= 1;
			current = bottom.arcs[wz[0]][wz[1]];
			counter++;
		} while (current != start); // follow the meander until you get back to where you started
		return (counter == order);
	}
	


	/**
	 * tries to make the 01 move on the top or bottom (whichever is specified),
	 * then tests 01 moves on the other half to try to find one that "repairs" the meander (makes it into one loop again)
	 * 
	 * @param s one index
	 * @param t the other index (s = t +/- 1)
	 * @param tb top or bottom
	 * @return true if a 01 move (on the other half of the meander) was found to "repair" the meander
	 * 	  	   false otherwise
	 */
	public boolean zeroOneMove(int s, int t, String tb) { // indices
		DrawFrame.yOffset = DrawFrame.height;
		if (tb != "t" && tb != "b") {
			throw new IllegalArgumentException("specify t or b (for top or bottom)");
		}
		PerfectMatching move, other;
		if (tb == "t") {
			move = top.clone();
			other = bottom.clone();
			move.zeroOneMove(s, t);
		} else {
			move = bottom.clone();
			other = top.clone();
			move.zeroOneMove(s, t);
		}
		
		for (int i = 0; i < order*2 - 1; i++) {
			Meander test;
			if (tb == "t") {
				test = new Meander(move.clone(), other.clone());
				try {
					test.bottom.zeroOneMove(i, i+1);
					if (test.isMeander()) {
						// found corresponding move
						return true;
					}
				} catch (Exception e) {	}
				
			} else {
				test = new Meander(other.clone(), move.clone());
				try {
					test.top.zeroOneMove(i, i+1);
					if (test.isMeander()) {
						// found corresponding move
						return true;
					}
				} catch (Exception e) {	}
			}
		}
		draw();
		System.out.println("couldn't find a move; indices " + s + ", " + t);
		System.out.println(top.ozString() + "\n" + bottom.ozString());
		return false; // got to the end without finding a move
	}
	
	

	public void draw() {
		double[][][] pointsArrays = new double[2][order*2][4];
		int[][][] arcsArrays = new int[2][order][4];
		PerfectMatching[] matchings = {top, bottom};
		
		for (int m = 0; m < 2; m++) {
			for (int i = 0; i < order*2; i++) {
				pointsArrays[m][i][0] = x*(i+1)-d/2;
				pointsArrays[m][i][1] = 250-d/2; //y-center of each circle
				pointsArrays[m][i][2] = pointsArrays[m][i][3] = d;
			}
			
			for (int i = 0; i < order; i++) {
				arcsArrays[m][i][0] = (matchings[m].arcs[i][0]+1)*x; // top-left x of the arc's framing rectangle
				arcsArrays[m][i][2] = (Math.abs(matchings[m].arcs[i][1]-matchings[m].arcs[i][0]))*x; // width of arc
				arcsArrays[m][i][3] = arcsArrays[m][i][2]/2; // height
				arcsArrays[m][i][1] = DrawFrame.height/2 - arcsArrays[m][i][3]/2 - d/2; // top-left y of the arc's framing rectangle
			}
		}
		DrawFrame.drawMeander(pointsArrays[0], arcsArrays[0], pointsArrays[1], arcsArrays[1]);
	}
}