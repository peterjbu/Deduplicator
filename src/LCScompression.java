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
        while(file.contains(lcs)){
            int index=file.indexOf(lcs);//index of the longest common substring
            this.indexes.add(index);//add lcs index into the list of indexes
            file = file.replaceFirst(lcs, "");//replace first lcs in the string with "" empty string
        }
        for (int i = 0; i < indexes.size(); i++) {
        		System.out.println(indexes.get(i));
        }
        this.compressed = file;
        return this.compressed;
    }

    public static void main(String args[]) {
    	
    		LCScompression L = new LCScompression();
    		String file_test = "hellomynameisesenhellomynameispeterhellomynameisjosh";
    		String lcs_test = "myname";
    		
    		System.out.println(L.compress(lcs_test, file_test));

    }

}