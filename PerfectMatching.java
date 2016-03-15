import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Stack;
import java.util.Arrays;


public class PerfectMatching {

	int order;
	int[][] arcs; // list of the arcs (connections between points) that make up the perfect matching; the first point is zero and the last is 2*order - 1
	int[][] points; // updated when arcs is updated; tells you where a given point is found in arcs array (used in Meander)
	int[] oz; // another representation of the perfect matching; a 0 represents the beginning of an arc, and a 1 represents the end of one
	static int d = 10; // diameter of points
	static int x = 30; // horizontal separation of points
	
	
	public int arcsInCommon(PerfectMatching a, PerfectMatching b) {
		return 0;
	}

	
	/**
	 * construct a Perfect Matching from an array of arcs
	 */
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
	
	
	/**
	 * construct a Perfect Matching from an array of 0's and 1's indicating whether each index of the meander
	 * indicates the start of another arc or the closing of one
	 */
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
	
	
	/**
	 * construct a Perfect Matching from a string of 0's and 1's indicating whether each index of the meander
	 * indicates the start of another arc or the closing of one
	 */
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


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof PerfectMatching))
			return false;
		PerfectMatching other = (PerfectMatching) obj;
		return Arrays.equals(other.oz, oz);
	}



	
	/**
	 * use arcs array to make/update points array (described at top)
	 */
	private void pointsSetUp() {
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


	
	/**
	 * @return number of arcs with odd starting point (used to determine level of the perfect matching)
	 */
	public int oddStarts() {
		int odds = 0;
		for (int[] arc : arcs) {
			odds += (Math.min(arc[0],  arc[1]) % 2) ^ 1;	// because arcs is offset by 1 (biggest arc is {0, 2n-1} instead of {1, 2n})
		}
		return odds;
	}


	
	/**
	 * level of meander - ranges from 1 to n
	 */
	public int level() {
		return order + 1 - oddStarts();
	}



	public int order() {
		return order;
	}
	


	/**
	 * make a new text file where each line gives instructions for drawing an arc to TiKZ,
	 * a package for creating graphics in LaTeX
	 * 
	 * @param scaling vary from .5 to 1.5; scaling beyond those bounds makes picture way too small/big
	 */
	public void TikZfile(String fileName, double scaling) {
		String[] lines = new String[arcs.length];
		for (int i = 0; i < arcs.length; i++) {
			double start = (float) scaling*Math.min(arcs[i][0], arcs[i][1]) / 2;
			double end = (float) scaling*Math.max(arcs[i][0], arcs[i][1]) / 2;
			// controls y coordinates = start - end
			// controls x coordinates are start + (end - start)/4 and end - (end - start)/4
			double y = scaling*(end - start) / 3;
			double xOffset = (end - start) / 4;
			double x1 = start + xOffset;
			double x2 = end - xOffset;
			String s = String.format("\\draw (%.3f, 0) .. controls (%.2f, %.3f) and (%.2f, %.1f) .. (%.3f, 0);",
					start, x1, y, x2, y, end);
			lines[i] = s;
		}
		
		
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(fileName + ".txt"));
			writer.write("\\begin{tikzpicture}");
			writer.newLine();
			for (String line : lines) {
				writer.write(line);
				writer.newLine();
			}
			writer.write("\\end{tikzpicture}");
			writer.newLine();
			writer.newLine();
			writer.close();
		} catch (Exception e) {}
	}
	


	/**
	 * combine a list of text files
	 * @param fileNames array of file names
	 */
	public static void concatFiles(String[] fileNames, String newFileName) {
		OutputStream out;
		try {
			out = new FileOutputStream(newFileName + ".txt");
			byte[] buf = new byte[fileNames.length];
		    for (String file : fileNames) {
		        InputStream in = new FileInputStream(file);
		        int b = 0;
		        while ( (b = in.read(buf)) >= 0) {
		            out.write(buf, 0, b);
		            out.flush();
		        }
		        in.close();
		    }
		    out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	    
	}
	
	
	
	/**
	 * returns the deepest nesting level of arcs
	 */
	public int highestNesting() {
		int max = 0;
		int current = 0;	// current level of nesting
		int index = 0;
		while (index < oz.length) {
			if (oz[index] == 1) {
				current++;
				max = Math.max(max, current);
			} else {
				current--;
			}
			index++;
		}
		return max;
	}
	
	
	
	public PerfectMatching clone() {
		return new PerfectMatching(oz.clone());
	}
	
	
	
	/**
	 * draw this Perfect Matching
	 */
	public void draw() {
		double[][] pointsArray = new double[order*2][4];
		for (int i = 0; i < order*2; i++) {
			pointsArray[i][0] = x*(i+1)-d/2; // x-center of each circle
			pointsArray[i][1] = 250-d/2; // y-center of each circle
			pointsArray[i][2] = pointsArray[i][3] = d; // diameter ([2] is width of ellipse and [3] is height, want a circle so both are equal)
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
	


	/**
	 * @return string representation of perfect matching with 0's and 1's
	 */
	public String ozString() {
		String print = "";
		for (int i = 0; i < oz.length; i++) {
			print += oz[i] + ", ";
		}
		return print.substring(0, print.length() - 2);
	}
	

	
	/**
	 * @return return string representation of list of arcs that make up the perfect matching
	 */
	public String arcsString() {
		String print = "";
		for (int i = 0; i < arcs.length; i++) {
			print += String.format("{%d, %d}, ", arcs[i][0], arcs[i][1]);
		}
		return print.substring(0, print.length() - 2);
	}
	


	/**
	 * @return string representation of list of points (describe above)
	 */
	public String pointsString() {
		String print = "";
		for (int i = 0; i < arcs.length; i++) {
			print += String.format("{%d, %d}, ", points[i][0], points[i][1]);
		}
		return print.substring(0, print.length() - 2); // exclude the final comma
	}
	


	/**
	 * create a file with a list of the number of perfect matchings on each level, for orders 1 - maxLevel inclusive.
	 * 
	 * @param fileName name of file to create
	 * @param maxLevel highest level to include
	 */
	public static void writeLevelNumbers(String fileName, int maxLevel) {
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(fileName + ".txt"));
			for (int i = 1; i <= maxLevel; i++) {
				writer.write("order " + i + ":");
				writer.newLine();
				int[] nums = levelNumbers(i);
				for (int num : nums) {
					writer.write(Integer.toString(num));
					writer.newLine();
				}
				writer.write("~~~~~~~~");
				writer.newLine();
			}
			writer.close();
		} catch (Exception e) {}		
		
	}


	/**
	 * for a given order n, return an array giving the number of perfect matchings on each level in the graph of all perfect matchings of order n
	 * @param n the order
	 * @return array giving the number of perfect matchings on each level in the graph of all perfect matchings of order n
	 */
	public static int[] levelNumbers(int n) {
		int[] ret = new int[n];
		String[] strings = Catalan.CnStrings(n);	// strings representing each pm of order n
		
		for (String string : strings) {
			ret[(new PerfectMatching(string)).oddStarts() - 1]++;	// make a pm out of each string, count the number of odd starts and increment corresponding level
		}
		
		return ret;
	}
	
	public static void main(String[] args) {
		PerfectMatching test = new PerfectMatching("11100010");
		
		test.draw();
		
		writeLevelNumbers("levelNumbers 1 to 7", 7);
		System.out.println("done");
		
//		test.TikZfile("test", 1);
//		
		
		
//		for (int i = 0, twos = 0, threes = 0; i < strings.length; i++) {
//			PerfectMatching match = new PerfectMatching(strings[i]);
//			match.TikZfile(Integer.toString(i), .75);
//			if (match.oddStarts() == 2) {
//				names2odds.add(Integer.toString(i) + ".txt");
//			} else if (match.oddStarts() == 3) {
//				names3odds.add(Integer.toString(i) + ".txt");
//			}
//		}
		
//		System.out.println(names2odds.size());
		
//		concatFiles(names2odds.toArray(new String[names2odds.size()]), "allTwos");
//		concatFiles(names2odds.toArray(new String[names3odds.size()]), "allThrees");
		
	}
	
}
