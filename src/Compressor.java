import java.util.ArrayList;

public class Compressor{
    private String compressed;//result string of the compressed file without the longest common substring
    private ArrayList<Integer> indexes;//indexes of where the most common substring is
    private String lcs = "";
    private ArrayList<String> zipFiles;

    //constructor for LCScompression
    Compressor(){
        this.indexes = new ArrayList<Integer>();
        this.compressed = "";
    }

    public String compress(String file1, String file2){

        //concatenate two files and build suffix tree to
        //find longest common substring of file1 and file2; hardcoded in for now.
        this.lcs = findLCS(file1, file2);

        int index=file2.indexOf(lcs);//index of the longest common substring
        this.indexes.add(index);//add lcs index into the list of indexes
        file2 = file2.replaceAll(lcs, "");//replace first lcs in the string with "" empty string
        System.out.println(file2);

        this.compressed = file2;
        return this.compressed;
    }

    static String findLCS(String X, String Y)
    {
        // Create a table to store lengths of longest common
        // suffixes of substrings.   Note that LCSuff[i][j]
        // contains length of longest common suffix of X[0..i-1]
        // and Y[0..j-1]. The first row and first column entries
        // have no logical meaning, they are used only for
        // simplicity of program
        int m = X.length();
        int n = Y.length();

        int[][] LCSuff = new int[m + 1][n + 1];

        // To store length of the longest common substring
        int len = 0;

        // To store the index of the cell which contains the
        // maximum value. This cell's index helps in building
        // up the longest common substring from right to left.
        int row = 0, col = 0;

        /* Following steps build LCSuff[m+1][n+1] in bottom
           up fashion. */
        for (int i = 0; i <= m; i++) {
            for (int j = 0; j <= n; j++) {
                if (i == 0 || j == 0)
                    LCSuff[i][j] = 0;

                else if (X.charAt(i - 1) == Y.charAt(j - 1)) {
                    LCSuff[i][j] = LCSuff[i - 1][j - 1] + 1;
                    if (len < LCSuff[i][j]) {
                        len = LCSuff[i][j];
                        row = i;
                        col = j;
                    }
                } else
                    LCSuff[i][j] = 0;
            }
        }

        // if true, then no common substring exists

        // allocate space for the longest common substring
        String resultStr = "";

        // traverse up diagonally form the (row, col) cell
        // until LCSuff[row][col] != 0
        while (LCSuff[row][col] != 0) {
            resultStr  = X.charAt(row - 1) + resultStr; // or Y[col-1]
            --len;

            // move diagonally up to previous cell
            row--;
            col--;
        }

        // required longest common substring
        return resultStr;
    }



    public static void main(String args[]) {

        Compressor L = new Compressor();

        String file1_test = "hellomyname";
//            L.zipFiles.add(file1_test);
        String file2_test = "hellomynameisesenhellomynameispeterhellomynameisjosh";
//            L.zipFiles.add(file2_test);
//    		String file3_test = "hellomynameisdan";
//    		L.zipFiles.add(file3_test);
//    		String file4_test = "ellomynameisda";
//    		L.zipFiles.add(file4_test);

//    		for (int i = 1; i < L.zipFiles.size(); i++){
//    		    L.compress(L.zipFiles.get(0), L.zipFiles.get(i));
//            }

        System.out.println(L.compress(file1_test, file2_test));

        //System.out.println(L.findLCS(file1_test, file2_test));

    }

}

