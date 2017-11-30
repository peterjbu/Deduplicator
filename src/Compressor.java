import java.util.ArrayList;
import java.io.FileWriter;
import java.io.File;
import java.io.IOException;
import java.io.*;

public class Compressor{
    private ArrayList<ArrayList<Integer>> indexes;//indexes of where the most common substring is
    private ArrayList<ArrayList<Integer>> lcsList;
    private String compressed;//result string of the compressed file without the longest common substring
    private String uneditedFile;
    private String referenceFile;
    private String fileToCompress;

    //constructor for LCScompression
    Compressor(String reference, String file2){
        this.indexes = new ArrayList<ArrayList<Integer>>();
        this.lcsList = new ArrayList<ArrayList<Integer>>();
        this.compressed = "";
        this.uneditedFile = "";
        referenceFile = reference;
        fileToCompress = file2;
        compress(referenceFile, fileToCompress);
    }

    private String compress(String file1, String file2){
        uneditedFile = file2;

        ArrayList<Integer> subIndex = new ArrayList<Integer>();

        //concatenate two files and build suffix tree to
        //find longest common substring of file1 and file2; hardcoded in for now.
        String currentLcs = findLCS(file1, file2);

        int startingIndex = file1.indexOf(currentLcs);
        int endingIndex = startingIndex + currentLcs.length();

        subIndex.add(startingIndex);
        subIndex.add(endingIndex);

        lcsList.add(subIndex);

        ArrayList<Integer> temp = new ArrayList<Integer>();

        while(file2.contains(currentLcs)) {
            int index = file2.indexOf(currentLcs);//index of the longest common substring
            temp.add(index);//add lcsList index into the list of indexes
            file2 = file2.replaceFirst(currentLcs, "");//replace first lcsList in the string with "" empty string
        }
        indexes.add(temp);

        if (file2.length() > 4) {
            compress(file1, file2);
        }
        /** the data required to reconstruct the file based off of the reference file is appended to the
         compressed txt, making the program volatile.The structure of the compressed file will be:

         compressed txt
         index [starting index and ending index of reference file substring

         **/
        else {
            for (int i = 0; i < indexes.size(); i++) {
                file2 += "\n";
                for (int j = 0; j < indexes.get(i).size(); j++) {
                    file2 += indexes.get(i).get(j);
                    file2 += ":";
                    file2 += lcsList.get(i);
                    file2 += ",";
                }
            }
            this.compressed = file2;
        }

        return this.compressed;
    }

    public String getCompressed(){
        return compressed;
    }

//    public String decompress(String compressedFile){
//        String result = compressed;
//        for (int i = indexes.size() - 1; i >= 0; i--) {
//            for (int j = indexes.get(i).size() - 1; j >= 0; j--) {
//                String comStart = result.substring(0, indexes.get(i).get(j));
//                String comEnd = result.substring(indexes.get(i).get(j));
//                result = comStart + lcsList.get(i) + comEnd;
//            }
//        }
//        return result;
//    }

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
        String file1_test = "helloaynameisesebnhellomynamespeterhellocynameisjosh";
        String file2_test = "hellomynameisesenhellomynameispeterhellomynameisjosh";
//        String file1_test = "hellomynameisConrad";
//        String file2_test = "hellomynameisPeterhellomynameisJosh";

        Compressor L12 = new Compressor(file1_test, file2_test);


        String result = L12.compress(file1_test, file2_test);
        System.out.println(result);

        File file = new File("file2_test.txt");
        // creates the file
        try (Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("file2_test.txt"), "utf-8")))
        {
            writer.write(result);
        }
        catch (IOException e){
            System.out.println("IO exception.");
        }

        File reference = new File("file1_test.txt");
        // creates the file
        try (Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("file1_test.txt"), "utf-8")))
        {
            writer.write(file1_test);
        }
        catch (IOException e){
            System.out.println("IO exception.");
        }
    }

}

