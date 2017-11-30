import java.util.ArrayList;

public class Decompressor {
    private String compressed;
    private String[] metadata;

    public Decompressor(String compressedFile){
        compressed = compressedFile;
        metadata = compressedFile.split(System.getProperty(""));

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
        String test = "hello" +
                "myname is " +
                "Olaf";
        Decompressor D = new Decompressor(test);
//        for (int i = 0; i < D.metadata.length; i++){
//            System.out.println(D.metadata[i]);
//        }
        System.out.println(D.metadata[0]);
    }
}
