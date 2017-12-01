import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Arrays;
import java.util.Map;
import java.util.HashMap;

/**
 * This class serves as the entry point to our Deduplicator app. It supports two arguments: -addFile, -locker
 * The -addFile command adds the file in question to the locker location as the argument to the -locker flag.
 * A user cannot add a file without supplying a locker location. However, a user CAN supply a locker location with no
 * -addFile flag, akin to initializing an empty locker.
 */

public class Deduplicator {

    public static void main(String[] args) {

        /**
         * All flags that can be added as args to the command line
         */

        final List argList = Arrays.asList("-addFile", "-locker", "-retrieve", "-dest");
        Map<String, String> optionsMap = new HashMap<String, String>();

        for (int i = 0; i < args.length; i++) {
            if (args[i].charAt(0) == '-') {
                if (args[i].length() < 2) {
                    throw new IllegalArgumentException("Not a valid argument: " + args[i]);
                }
                if (argList.contains(args[i])) {
                    optionsMap.put(args[i], args[i + 1]);
                    i++;
                }
                else {
                    throw new IllegalArgumentException("Not a valid argument: " + args[i]);
                }
            }
        }

        if (optionsMap.get("-retrieve") != null && optionsMap.get("-dest") != null) {
            retrieve(optionsMap.get("-retrieve"), optionsMap.get("-dest"));
        }

        /**
         * Initialize a locker with a .fileList file.
         */

        if (optionsMap.get("-locker") != null) {
            String lockerPath = optionsMap.get("-locker");

            File dirPath = new File(lockerPath);
            if (!dirPath.exists()) {
                boolean success = dirPath.mkdir();
                if (!success) {
                    throw new IllegalArgumentException("Unable to create locker at" + lockerPath);
                }
                initialize(lockerPath);
            }
        }

        /**
         * Add a file to the specified locker location
         */

        if (optionsMap.get("-addFile") != null) {
            if (optionsMap.get("-locker") != null) {
                String lockerPath = optionsMap.get("-locker");

                File dirPath = new File(lockerPath);
                if (!dirPath.exists()) {
                    boolean success = dirPath.mkdir();
                    if (!success) {
                        throw new IllegalArgumentException("Unable to create locker at" + lockerPath);
                    }
                    initialize(lockerPath);
                }
                String fileContents = "";
                String filePath = optionsMap.get("-addFile");
                String[] directories = filePath.split("/");
                String fileName = directories[directories.length - 1];
                fileContents = readFrom(filePath);
                String fileListContents = readFrom(lockerPath + "/.fileList");
                String reference = fileListContents.split(",")[0];

                /**
                 * If this is the first file added to the locker, use it as the reference file for compression
                 * Otherwise, obtain contents in reference file and use it to compress file
                 */

                if (firstFile(lockerPath)) {
                    // store uncompressed
                    String newFilePath = lockerPath + "/" + fileName + ".dedup";

                    // creates the file to locker
                    writeTo(newFilePath, fileContents);
                    writeTo(lockerPath + "/" + ".fileList", fileName + ".dedup");
                }
                else {
                    String referenceFileContent = readFrom(lockerPath + "/" + reference );

                    // compress with reference file
                    Compressor comp = new Compressor(referenceFileContent, fileContents);
                    String compressedText = comp.getCompressed();
                    writeTo(lockerPath + "/" + fileName + ".dedup", compressedText);
                    String updatedFileListContent = "," + fileName + ".dedup";

                    try {
                        Files.write(Paths.get(lockerPath + "/.fileList"), updatedFileListContent.getBytes(), StandardOpenOption.APPEND);
                    }
                    catch (IOException e) {
                        System.out.println("Unable to append file");
                    }
                }
            }
            else {
                throw new IllegalArgumentException("Must provide locker name to store file in");
            }
        }
    }

    /**
     * Initializes a locker location with an empty .fileList file
     * @param folderPath: the folder path to add the new .fileList into
     */
    private static void initialize(String folderPath) {

        File fileList = new File(folderPath + "/.fileList");

        if (!fileList.exists()) {
            writeTo(folderPath + "/.fileList", "");
        }
    }

    /**
     * Checks whether the file to be added to a locker location is the first such file
     * @param folderPath: the folder path to use
     * @return True if it is the first file being added, false otherwise
     */
    private static boolean firstFile(String folderPath) {
        String fileListContents = "";

        fileListContents = readFrom(folderPath + "/.fileList");

        return fileListContents.equals("");
    }


    /**
     * Writes contents to a file
     * @param filePath: The filepath to write to
     * @param contents: The contents to write into fileName
     */
    private static void writeTo(String filePath, String contents) {
        try (Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filePath), "utf-8")))
        {
            writer.write(contents);
        }
        catch (IOException e){
            System.out.println("Unable to store file " + filePath);
        }
    }

    /**
     * Reads contents from a file
     * @param filePath: The file to read from
     * @return A String object consisting of the contents in filePath
     */
    private static String readFrom(String filePath){
        String fileContents = "";
        try {
            fileContents =  new String(Files.readAllBytes(Paths.get(filePath)), StandardCharsets.UTF_8);
        }
        catch (IOException e){
            e.printStackTrace();
        }
        return fileContents;
    }

    private static void retrieve(String filePath, String dest) {
        String lockerPath = filePath.substring(0, filePath.lastIndexOf('/'));
        String fileName = filePath.substring(filePath.lastIndexOf('/'));
        String fileListContents = readFrom(lockerPath + "/.fileList");
        String referenceContents = readFrom(lockerPath + "/" + fileListContents.split(",")[0]);
        String compressedContents = readFrom(filePath);
        String decompressed = new Decompressor(referenceContents, compressedContents).getDecompressed();
        fileName.substring(0, fileName.lastIndexOf("."));
        writeTo(dest + "/" + fileName.substring(0, fileName.lastIndexOf(".")), decompressed);
    }

//    private static long getFileSize(File file){
//        try {
//            return file.length();
//        } catch(Exception e) {
//            System.out.println("Unable to get file size");
//        }
//    }

}
