import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Decompressor {
    private String compressed;
    private String[] metadata;
    private ArrayList<Integer> indexes; //indexes of where the most common substring is
    private ArrayList<ArrayList<Integer>> lcsList;

    public Decompressor(String compressedFile){
        compressed = compressedFile;
        metadata = compressedFile.split(System.getProperty("line.separator"));
        lcsList = new ArrayList<ArrayList<Integer>>();
        indexes = new ArrayList<Integer>();
        //metadata[0] is compressed text.
    }

    public void filter(){
        for (int i = metadata.length - 1; i > 0; i--) {
            Pattern p = Pattern.compile("\\[(.*?)\\]");
            Matcher m = p.matcher(metadata[i]);

            String compressIndex = "";

            for (int j = metadata[i].length() - 1; j >= 0; j--) {
                if (metadata[i].charAt(j) == ':') {

                    while (metadata[i].charAt(j-1) != ',') {
                        compressIndex = metadata[i].charAt(j-1) + compressIndex;
                        j--;
                        if (j == 0)
                            break;
                    }

                    indexes.add(Integer.parseInt(compressIndex));
                    System.out.println(Integer.parseInt(compressIndex));
                    compressIndex = "";
                }
            }

            while (m.find()) {
                ArrayList<Integer> tempLCS = new ArrayList<Integer>(); //holds each substring temporarily
                String[] temp = m.group(1).split(",");
                int start = Integer.parseInt(temp[0].trim());
                int end = Integer.parseInt((temp[1].trim()));
                tempLCS.add(start);
                tempLCS.add(end);
                lcsList.add(tempLCS);
            }
        }
    }

//    public String decompressor(){
//        for (int i = indexes.size() - 1; i >= 0; i--) {
//            for (int j = indexes.get(i).size() - 1; j >= 0; j--) {
//                String comStart = result.substring(0, indexes.get(i).get(j));
//                String comEnd = result.substring(indexes.get(i).get(j));
//                result = comStart + lcsList.get(i) + comEnd;
//            }
//        }
//        return result;
//    }

    public static void main(String args[]) {
        String test = "i\n16:[17, 29],\n0:[18, 29],12:[18, 29],\n6:[29, 35],\n6:[46, 52],\n0:[11, 16],";
        Decompressor D = new Decompressor(test);
        D.filter();
//        for (int i = 0; i < D.metadata.length; i++){
//            System.out.println(D.metadata[i]);
//        }
        for (int i = 0; i < D.lcsList.size(); i++){
            System.out.println(D.lcsList.get(i));
        }
    }
}
