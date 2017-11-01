public class LongestCommonSubstring {
	
	static char[] find_LCS(String X, String Y) {
		int x_length = X.length();
		int y_length = Y.length();
		
		//initialize a table where dimensions are the longest common substring possible to ensure
		//every character gets compared to one another.
		int[][] OverlapTable = new int [x_length][y_length];
		
		for (int i = 0; i < x_length; i++) {
			for (int j = 0; j < y_length; j++) {
				//the first column and row of the table are just fillers to make iterating simpler.
				if (i == 0 || j == 0)
					OverlapTable[i][j] = 0;
				//there is an overlap in the strings at i - 1 and j - 1 index.
				else if(X.charAt(i - 1) == Y.charAt(j - 1))
					OverlapTable[i][j] = OverlapTable[i - 1][j - 1] + 1;
				//there is an overlap in a shift of the characters.
				else
					OverlapTable[i][j] = Math.max(OverlapTable[i - 1][j],OverlapTable[i][j - 1]);
			}
		}
		
		int index = OverlapTable[x_length][y_length];
		char[] LCS = new char[index + 1];
		LCS[index] = '\0';
		
		int x = x_length;
		int y = y_length;
		
		while (x > 0 && y > 0) {
			if (X.charAt(x - 1) == Y.charAt(y)) {
				LCS[index - 1] = X.charAt(x - 1);
				x--;
				y--;
				index--;
			}
			else if(OverlapTable[x - 1][y] > OverlapTable[x][y - 1])
				x--;
			else
				y--;
		}
	return LCS;	
	}
	
	public static void main(String[] args) {
		String str1 = "peterjang";
		String str2 = "petrenjan";
		
	}
}
