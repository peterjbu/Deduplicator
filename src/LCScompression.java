import java.util.ArrayList;

public class LCScompression{
    private String compressed;//result string of the compressed file without the longest common substring
    private ArrayList<Integer> indexes;//indexes of where the most common substring is
    private String lcs = "";
    private int counter = 0;

    //constructor for LCScompression
    LCScompression(){
        this.indexes = new ArrayList<Integer>();
        this.compressed = "";
    }

    public String compress(String file1, String file2){


        //find longest common substring of file1 and file2; hardcoded in for now.
        if (counter == 0)
            this.lcs = "myname";
        else if (counter == 1)
            this.lcs = "hello";
        else
            this.lcs = "e";

        while(file2.contains(lcs)){
            int index=file2.indexOf(lcs);//index of the longest common substring
            this.indexes.add(index);//add lcs index into the list of indexes
            file2 = file2.replaceFirst(lcs, "");//replace first lcs in the string with "" empty string
        }

        if (file2.length() > 18){ //the smaller you want your file to be, the more time it will take.
            counter++;
            compress(file1, file2);
        }
        else{
            this.compressed = file2;
        }
        return this.compressed;
    }

    public static void main(String args[]) {
    	
    		LCScompression L = new LCScompression();
    		String file2_test = "hellomynameisesenhellomynameispeterhellomynameisjosh";
    		String file1_test = "this is an unnecessary variable since the lcs is hardcoded in.";
    		
    		System.out.println(L.compress(file1_test, file2_test));

    }

}