import java.util.Stack;


public class PerfectMatching {

	int order;
	int[][] arcs; // list of the arcs (connections between points) that make up the perfect matching; the first point is zero and the last is 2*order - 1
	int[][] points; // updated when arcs is updated; tells you where a given point is found in arcs array
	int[] oz; // another representation of the perfect matching; a 0 represents the beginning of an arc, and a 1 represents the end of one
	static int d = 20; // diameter of points
	static int x = 60; // horizontal separation of points
	
	private void pointsSetUp() {
		//use arcs array to make points array
		points = new int[order*2][2];
		for (int i = 0; i < arcs.length; i++) {
			for (int j = 0; j < 2; j++) {
				points[arcs[i][j]][0] = i;
					points[arcs[i][j]][1] = j;
			}
		}	
	}
	
	/**
	 * 
	 * @param s left or right index
	 * @param t left or right index ( abs(t-s)=1 )
	 */
	public void zeroOneMove(int s, int t) {
		if (s-t != 1 && s-t != -1) {
			throw new IllegalArgumentException("left and right index should be 1 apart from each other");
		}
		if (oz[s] == oz[t]) {
			throw new IllegalArgumentException("two 1's or 2 0's");
		}
		int left = Math.min(s, t);
		int right = Math.max(s, t);
		if (oz[left] == 1) { // start counting 1's and 0's from the left
			int ones = 0; // ones should be greater than 0's
			int zeros = 0;
			for (int i = 0; i < left; i++) {
				if (oz[i] == 0) {
					zeros++;
				} else {
					ones++;
				}
			}
			if (ones <= zeros) {
				throw new IllegalArgumentException("when switching 10 to 01, there must "
						+ "be more 1's than 0's prior to first index of zero-one move");
			} else {
				oz[left] = 0;
				oz[right] = 1;
				// set up arcs
			}
		} else {
			oz[left] = 1;
			oz[right] = 0;
		}
		// set up arcs
		Stack<Integer> stack = new Stack<Integer>();
		int counter = 0;
		for (int i = 0; i < oz.length; i++) {
			if (oz[i] == 1) {
				stack.push(i);
			} else {
				arcs[counter][0] = stack.pop();
				arcs[counter][1] = i;
				counter++;
			}
		}
		
		for (int i = 0; i < arcs.length; i++) {
			for (int j = 0; j < 2; j++) {
				points[arcs[i][j]][0] = i;
					points[arcs[i][j]][1] = j;
			}
		}
		
	}
	
	public PerfectMatching(int[][] arcs) {
		order = arcs.length;		
		this.arcs = arcs;
		oz = new int[order*2];
		for (int i = 0; i < arcs.length; i++) {
			oz[arcs[i][0]] = 1;
			oz[arcs[i][1]] = 0;
		}
		pointsSetUp();
	}
	
	public PerfectMatching clone() {
		return new PerfectMatching(oz.clone());
	}
	
	public PerfectMatching(int[] oz) {
		if (oz.length % 2 != 0) {
			throw new IllegalArgumentException("ones and zeros array is of odd length");
		}
		order = oz.length/2;
		this.oz = oz;
		
		arcs = new int[order][2];
		
		Stack<Integer> stack = new Stack<Integer>();
		int counter = 0;
		for (int i = 0; i < oz.length; i++) {
			if (oz[i] == 1) {
				stack.push(i);
			} else {
				arcs[counter][0] = stack.pop();
				arcs[counter][1] = i;
				counter++;
			}
		}
		pointsSetUp();
	}
	
	public PerfectMatching(String zeroOnes) {
		int[] nums = new int[zeroOnes.length()];
		for (int i = 0; i < nums.length; i++) {
			nums[i] = Integer.parseInt(String.valueOf(zeroOnes.charAt(i)));
		}
		
		order = nums.length/2;
		oz = nums;
		
		arcs = new int[order][2];
		
		Stack<Integer> stack = new Stack<Integer>();
		int counter = 0;
		for (int i = 0; i < oz.length; i++) {
			if (oz[i] == 1) {
				stack.push(i);
			} else {
				arcs[counter][0] = stack.pop();
				arcs[counter][1] = i;
				counter++;
			}
		}
		pointsSetUp();
	}
	
	public void draw() {
		double[][] pointsArray = new double[order*2][4];
		for (int i = 0; i < order*2; i++) {
			pointsArray[i][0] = x*(i+1)-d/2;
			pointsArray[i][1] = 250-d/2; // y-center of each circle
			pointsArray[i][2] = pointsArray[i][3] = d;
		}
		
		int[][] arcsArray = new int[order][4];
		for (int i = 0; i < order; i++) {
			arcsArray[i][0] = (arcs[i][0]+1)*x; //top-left x
			arcsArray[i][2] = (Math.abs(arcs[i][1]-arcs[i][0]))*x; //width
			arcsArray[i][3] = arcsArray[i][2]/2; // height
			arcsArray[i][1] = DrawFrame.height/2 - arcsArray[i][3]/2 - d/2; // top-left y
		}
		DrawFrame.drawPerfectMatching(pointsArray, arcsArray);
	}
	
	public String ozString() { // print representation of perfect matching with 0's and 1's
		String print = "";
		for (int i = 0; i < oz.length; i++) {
			print += oz[i] + ", ";
		}
		return print.substring(0, print.length() - 2);
	}
	
	public String arcsString() { // print list of the arcs (connections between points) that make up the perfect matching
		String print = "";
		for (int i = 0; i < arcs.length; i++) {
			print += "{" + arcs[i][0] + ", " + arcs[i][1] + "}, ";
		}
		return print.substring(0, print.length() - 2);
	}
	
	public static void main(String[] args) {
//		int[][] arcs = {{0,7},{1,4},{2,3},{5,6}};
//		PerfectMatching test = new PerfectMatching(arcs);
//		System.out.println(test.arcsString());
		PerfectMatching test = new PerfectMatching("11100010");
	    test.draw();
	}
	
}
