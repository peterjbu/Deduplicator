import java.util.ArrayList;

public class Compressor{
    private String compressed;//result string of the compressed file without the longest common substring
    private ArrayList<ArrayList<Integer>> indexes;//indexes of where the most common substring is
    private ArrayList<String> lcsList;
    private ArrayList<String> zipFiles;
    private String uneditedFile = "";

    //constructor for LCScompression
    Compressor(){
        this.indexes = new ArrayList<ArrayList<Integer>>();
        lcsList = new ArrayList<String>();
        this.compressed = "";
    }

    public String compress(String file1, String file2){

        uneditedFile = file2;
        //concatenate two files and build suffix tree to
        //find longest common substring of file1 and file2; hardcoded in for now.
        String currentLcs = findLCS(file1, file2);
        lcsList.add(currentLcs);

        ArrayList<Integer> temp = new ArrayList<Integer>();

        while(file2.contains(currentLcs)) {
            int index = file2.indexOf(currentLcs);//index of the longest common substring
            temp.add(index);//add lcsList index into the list of indexes
            file2 = file2.replaceFirst(currentLcs, "");//replace first lcsList in the string with "" empty string
        }

        indexes.add(temp);

        if (file2.length() > 10) {
            //System.out.println(currentLcs);
            compress(file1, file2);
        }
        else
            this.compressed = file2;

        return this.compressed;
    }

    public String decompress(String compressedFile){
        String result = compressed;
        for (int i = indexes.size() - 1; i >= 0; i--) {
            for (int j = indexes.get(i).size() - 1; j >= 0; j--) {
                String comStart = result.substring(0, indexes.get(i).get(j));
                String comEnd = result.substring(indexes.get(i).get(j));
                result = comStart + lcsList.get(i) + comEnd;
            }
        }
        return result;
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

        String file1_test = "helloaynameisesebnhellomynamespeterhellocynameisjosh";
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

        //System.out.println(L.compress(file1_test, file2_test));
        String result = L.compress(file1_test, file2_test);
        System.out.println(result);
        System.out.println(L.decompress((result)));

        //System.out.println(L.findLCS(file1_test, file2_test));

    }

}

