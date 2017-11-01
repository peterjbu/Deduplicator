import java.util.ArrayList;

public class LCScompression{
    private String compressed;//result string of the compressed file without the longest common substring
    private ArrayList<Integer> indexes;//indexes of where the most common substring is

    //constructor for LCScompression
    LCScompression(){
        this.indexes = new ArrayList<Integer>();
        this.compressed = "";
    }

    public String compress(String lcs, String file){
        int i = 0;
        while( i < file.length()){
            int index=file.indexOf(lcs);//index of the longest common substring
            this.indexes.add(index);//add lcs index into the list of indexes
            file.replaceFirst(lcs, "");//replace first lcs in the string with "" empty string
            if (!file.contains(lcs)){
                i = file.length() + 1;
            }
        }
        this.compressed = file;
        return this.compressed;
    }

    public static void main(String args[]) {


    }

}