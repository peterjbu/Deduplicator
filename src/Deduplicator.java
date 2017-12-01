import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

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

        final List argList = Arrays.asList("-addFile", "-locker", "-retrieve", "-dest", "-delete", "-getStats");
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
            createLocker(lockerPath);
        }

        /**
         * Add a file to the specified locker location
         */

        if (optionsMap.get("-addFile") != null) {
            if (optionsMap.get("-locker") != null) {
                String filePath = optionsMap.get("-addFile");
                String lockerPath = optionsMap.get("-locker");
                addFile(filePath, lockerPath);
            }
            else {
                throw new IllegalArgumentException("Must provide locker name to store file in");
            }
        }

        if (optionsMap.get("-delete") != null){
            String filePath = optionsMap.get("-delete");
            delete(filePath);
        }

        if (optionsMap.get("-getStats") != null){
            String lockerPath = optionsMap.get("-getStats");
            getStats(lockerPath);
        }
    }

    /**
     * Checks whether the file to be added to a locker location is the first such file
     * @param folderPath: the folder path to use
     * @return True if it is the first file being added, false otherwise
     */
    private static boolean firstFile(String folderPath) {
        File anchorFile = new File(folderPath + "/.anchor");
        return !anchorFile.exists();
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
            //e.printStackTrace();
        }
        return fileContents;
    }

    /**
     * Creates locker
     * @param lockerPath: creates locker at this location
     */
    public static void createLocker(String lockerPath){
        File dirPath = new File(lockerPath);
        if (!dirPath.exists()) {
            boolean success = dirPath.mkdir();
            if (!success) {
                throw new IllegalArgumentException("Unable to create locker at" + lockerPath);
            }
        }
    }

    /**
     * Adds file to locker
     * @param filePath: The path of the file to add
     * @param lockerPath: The path of the locker to add file to
     */
    public static void addFile(String filePath, String lockerPath){
        File dirPath = new File(lockerPath);
        if (!dirPath.exists()) {
            boolean success = dirPath.mkdir();
            if (!success) {
                throw new IllegalArgumentException("Unable to create locker at" + lockerPath);
            }
        }
        String fileContents = "";
        String[] directories = filePath.split("/");
        String fileName = directories[directories.length - 1];
        fileContents = readFrom(filePath);

        /**
         * If this is the first file added to the locker, use it as the reference file for compression
         * Otherwise, obtain contents in reference file and use it to compress file
         */

        if (firstFile(lockerPath)) {
            writeTo(lockerPath + "/" + ".anchor", fileContents);
            writeTo(lockerPath + "/" + fileName + ".dedup", "");
            writeTo(lockerPath + "/" + fileName + ".meta", "0:[0, " + fileContents.length() + "],");
        }
        else {
            String referenceFileContent = readFrom(lockerPath + "/" + ".anchor");

            // compress with reference file
            Compressor comp = new Compressor(referenceFileContent, fileContents);
            String compressedText = comp.getCompressed();
            String metaContents = comp.getMeta();
            writeTo(lockerPath + "/" + fileName + ".meta", metaContents);
            writeTo(lockerPath + "/" + fileName + ".dedup", compressedText);
        }
    }


    /**
     * retrieves compressed file from locker and saves decompressed version to destination
     * @param filePath: Source of target compressed file
     * @param dest: Destination path of decompressed file
     */

    public static void retrieve(String filePath, String dest) {
        String lockerPath = filePath.substring(0, filePath.lastIndexOf('/'));
        String fileName = filePath.substring(filePath.lastIndexOf('/'));
        String metaPath = filePath.substring(0, filePath.lastIndexOf(".")) + ".meta";
        String anchorContents = readFrom(lockerPath + "/.anchor");
        String compressedContents = readFrom(filePath);
        String metaContents = readFrom(metaPath);
        String decompressed = new Decompressor(anchorContents, compressedContents, metaContents).getDecompressed();
        writeTo(dest + "/" + fileName.substring(0, fileName.lastIndexOf(".")), decompressed);
    }

    /**
     * deletes files specified in filepath. To delete compressed file, delete .dedup extension.
     * @param filePath: Path of file to be deleted
     */
    public static void delete(String filePath){
        File dedupFile = new File(filePath);
        File metaFile = new File(filePath.substring(0, filePath.lastIndexOf(".")) + ".meta");
        if(!dedupFile.delete()){
            throw new IllegalArgumentException("Cannot delete dedup file");
        }
        if (!metaFile.delete()) {
            throw new IllegalArgumentException("Cannot delete meta file");
        }
    }


    /**
     * prints out the size of the a directory
     * @param lockerPath: the path of the directory
     */
    private static void getStats(String lockerPath){
        long length = 0;
        ArrayList<Long> fileSize = new ArrayList<Long>();
        ArrayList<String> fileName = new ArrayList<String>();
        File locker = new File(lockerPath);
        try {
            for (File file : locker.listFiles()) {
                if (file.isFile()) {
                    length += file.length();
                    fileSize.add(file.length());
                    fileName.add(file.getAbsolutePath());
                }
//                else
//                    length += locker(file);
            }
        } catch(NullPointerException e){
            e.printStackTrace();
        }

        System.out.println("Size of directory: " + length);
        for (int i = 0; i < fileSize.size(); i++){
            System.out.println("Size of " + fileName.get(i) + " : " + fileSize.get(i));
        }
    }

}
