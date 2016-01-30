public class Catalan {
	
	/**
	 * The public function for this class. Given n, returns an array of each binary string of length 2n with the property that
	 * in any initial substring, the number of 1's is greater than or equal to the number of 0's.
	 * The function uses the fact that these strings are counted by the Catalan numbers
	 * 
	 * @param n each string will be length 2n
	 * @return the array with each string
	 */
	public static String[] CnStrings(int n) {
		return CnStringsHelp(n, CatalanNumbers(n));
	}
	
	
	/**
	 * The Catalan recurrence tells you that C(n) is equal to the sum of C(i)C(j) over all pairs i,j such that i+j = n-1 (with i >= 0 and j >= 0)
	 * when computing the Catalan numbers, you have a base case which is a number,
	 * and a rule for computing the next value by adding and multiplying previously computed values.
	 * 
	 * This function uses the same structure to generate these strings, since they are counted by the Catalan numbers.
	 * So instead of calculating numbers, we calculate lists of strings. The "add" operation is concatenation of lists,
	 * and the "multiply" operation is the operation described in the "cross" function below.
	 * 
	 * @param n each string will be length 2n
	 * @param cats
	 * @return
	 */
	private static String[] CnStringsHelp(int n, int[] cats) {
		String[] strings = new String[cats[Math.max(1,n)]];
		if (n == 0) {
			strings[0] = "";
		} else if (n == 1) {
			strings[0] = "10";
		} else {
			int counter = 0;
			for (int i = 0; i < n; i++) {
				String[] first = CnStringsHelp(i, cats);
				String[] second = CnStringsHelp(n - 1 - i, cats);
				for (int j = 0; j < first.length; j++) {
					for (int k = 0; k < second.length; k++) {
						strings[counter++] = cross(first[j], second[k]);
					}
				}
			}
		}
		return strings;
	}
	
	
	/**
	 * to "multiply" two strings (read description of above function first), create a new string by
	 * 1) appending the first string (this function's result depends on the order in which the arguments are given) up to
	 * the point where it is "balanced" (equal number of 1's and 0's),
	 * 2) appending a 1
	 * 3) appending the entire second string
	 * 4) appending a 0
	 * 5) appending the rest of the first string
	 * 
	 * @param one first string
	 * @param two second string
	 * @return the "product" of the two strings
	 */
	private static String cross(String one, String two) {
		int i = firstBalanceIndex(one);
		String cross = one.substring(0, i) + "1" + two + "0" + one.substring(i, one.length());
		return cross;
	}
	
	
	/**
	 * C(0) = C(1) = 1
	 * @param n
	 * @return an array of the first n+1 Catalan numbers
	 */
	private static int[] CatalanNumbers(int n) {
		int[] cat = new int[n+1];
		cat[0] = cat[1] = 1;
		for (int i=2; i<=n; i++) {
	        cat[i] = 0;
	        for (int j=0; j<i; j++)
	            cat[i] += cat[j] * cat[i-j-1];
	    }
		return cat;
	}
	
	
	/**
	 * finds the first index (besides 0) at which the number of 0's and 1's up to that point are equal
	 * 
	 * @param str
	 * @return that first index 
	 */
	private static int firstBalanceIndex(String str) {
		if (str.equals("")) {
			return 0;
		}
		
		int balance = 0, j = 0;
		do { // until balance==0 again, check the next character and increment balance if that character is 1; otherwise, decrement
			if (str.substring(j, j+1).equals("1")) {
				balance++;
			} else if (str.substring(j, j+1).equals("0")) {
				balance--;
			} else {
				throw new IllegalArgumentException("that's not a 1 or a 0");
			}
			j++;
		} while (balance != 0);
		
		return j;
	}
	
	
	public static void main(String[] args) {
		
	}
	
}
