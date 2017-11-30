import java.util.ArrayList;

public class Decompressor extends Compressor {
    private String compressed;
    private String metadata;

    public Decompressor(String compressedFile){
        compressed = compressedFile;
        if (compressedFile.contains("M*D")){
        int index = compressedFile.indexOf("M*D");
        metadata = compressedFile.substring(index+3,compressedFile.length()-1);
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
        Compressor L12 = new Compressor();

        String file1_test = "helloaynameisesebnhellomynamespeterhellocynameisjosh";
        String file2_test = "hellomynameisesenhellomynameispeterhellomynameisjosh";

        String result = L12.compress(file1_test, file2_test);
        System.out.println(result);
        Decompressor D = new Decompressor(result);
//        for (int i = 0; i < D.metadata.length; i++){
//            System.out.println(D.metadata[i]);
//        }
        System.out.println(D.metadata);
    }
}
