import java.io.IOException;
import java.lang.reflect.Array;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Decompressor {
    private String reference;
    private String[] metadata;
    private String decompressed;
    private String compressed;
    private ArrayList<Integer> indexes; //indexes of where the most common substring is
    private ArrayList<ArrayList<Integer>> lcsList;

    public Decompressor(String referenceFile, String compressedFile, String metaContents){
        reference = referenceFile;
        metadata = metaContents.split(System.getProperty("line.separator"));
        decompressed = "";
        compressed = compressedFile;
        lcsList = new ArrayList<ArrayList<Integer>>();
        indexes = new ArrayList<Integer>();
        decompressor();
        //metadata[0] is compressed text.
    }

    private void filter(){
        for (int i = metadata.length - 1; i >= 0; i--) {
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

    private void decompressor(){
        filter();
        String temp = compressed;
        for (int i = 0; i < indexes.size(); i++) {
            String comStart = temp.substring(0, indexes.get(i));
            String comEnd = temp.substring(indexes.get(i));
            temp = comStart + reference.substring(lcsList.get(i).get(0), lcsList.get(i).get(1)) + comEnd;
        }
        decompressed = temp;
    }

    public String getDecompressed() {
        return decompressed;
    }

    public static void main(String args[]) {
//        String test = "i\n16:[17, 29],\n0:[18, 29],12:[18, 29],\n6:[29, 35],\n6:[46, 52],\n0:[11, 16],";
//        String reference = "helloaynameisesebnhellomynamespeterhellocynameisjosh";
        String reference = "";
        String test = "";

        try {
            reference = new String(Files.readAllBytes(Paths.get("file1_test.txt")), StandardCharsets.UTF_8);
            test = new String(Files.readAllBytes(Paths.get("file2_test.txt")), StandardCharsets.UTF_8);
        }
        catch (IOException e){
            System.out.println("IO exception");
        }

//        Decompressor D = new Decompressor(reference, test);
//        System.out.println(D.getDecompressed());
//        for (int i = 0; i < D.metadata.length; i++){
//            System.out.println(D.metadata[i]);
//        }

    }
}
