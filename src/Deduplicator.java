import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Arrays;
import java.util.Map;
import java.util.HashMap;

public class Deduplicator {

    public static void main(String[] args) {

        initialize(args);

        final List argList = Arrays.asList("-addFile", "-locker");
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

        if (optionsMap.get("-addFile") != null) {
            if (optionsMap.get("-locker") != null) {
                String lockerPath = "lockers/" + optionsMap.get("-locker");
                File dirPath = new File(lockerPath);
                if (!dirPath.exists()) {
                    boolean success = dirPath.mkdir();
                    if (!success) {
                        throw new IllegalArgumentException("Unable to create locker " + optionsMap.get("-locker"));
                    }
                }
                String fileContents = "";
                String filePath = optionsMap.get("-addFile");
                String[] directories = filePath.split("/");
                String fileName = directories[directories.length - 1];
                String newFileContent = "";

                fileContents = readFrom(filePath);

                String fileListContents = readFrom(".fileList");

                String[] splitContents = fileListContents.split(System.getProperty("line.separator"));
                String[] temp = splitContents[0].split(",");
                String reference = splitContents[0].split(",")[1];
                String locker = splitContents[0].split(",")[2];


                if (firstFile()) {
                    // store uncompressed
                    String newFile = lockerPath + "/" + fileName;

                    // creates the file to locker
                    writeTo(newFile, fileContents);

                    // Updates fileList metadata
                    try (Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(".fileList"), "utf-8")))
                    {
                        String newContents = "1," + fileName + "," + optionsMap.get("-locker") + "\n" + fileName + ":" + optionsMap.get("-locker");
                        writer.write(newContents);
                    }
                    catch (IOException e){
                        System.out.println("Unable to read input file");
                    }
                }
                else {
                    String referenceFileContent = readFrom("lockers/" + locker + "/" + reference);
                    // compress with reference file
                    Compressor comp = new Compressor(referenceFileContent, fileContents);
                    String compressedText = comp.getCompressed();
                    writeTo("lockers/" + locker + "/" + fileName, compressedText);

                    //update the fileListContents .fileList by incrementing the counter and adding fileName and locker
                    try
                    {
                        newFileContent = "," + fileName + ":" + optionsMap.get("-locker");

                        Files.write(Paths.get(".fileList"), newFileContent.getBytes(), StandardOpenOption.APPEND);
                    }catch (IOException e)
                    {
                        System.out.println("Unable to append file");
                    }

                }



                // locker name already exists, proceed to extract text from file, compress into locker
                // Call compression algorithm with filename optionsMap.get("-addFile")
                // and locker location optionsMap.get("-locker")
            }
            else {
                throw new IllegalArgumentException("Must provide locker name to store file in");
            }
        }
    }

    private static void initialize(String[] args) {
        if (args.length == 0) {
            throw new IllegalArgumentException("Cannot provide empty arguments");
        }


        File fileList = new File(".fileList");
        File lockerDir = new File("lockers");

        if (!fileList.exists()) {
            writeTo(".fileList", "0, , ");
        }

        if (!lockerDir.exists()) {
            boolean success = lockerDir.mkdir();
            if (!success) {
                throw new IllegalArgumentException("Unable to create locker directory");
            }
        }
    }

    private static boolean firstFile() {
        String fileListContents = "";

        fileListContents = readFrom(".fileList");

        String[] splitContents = fileListContents.split(System.getProperty("line.separator"));
        String[] first = splitContents[0].split(",");

        return first[0].equals("0");
    }

    private static void writeTo(String fileName, String contents) {
        try (Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileName), "utf-8")))
        {
            writer.write(contents);
        }
        catch (IOException e){
            System.out.println("unable to store file " + fileName);
        }
    }

    private static String readFrom(String filePath){
        String fileContents = "";
        try {
            fileContents =  new String(Files.readAllBytes(Paths.get(filePath)), StandardCharsets.UTF_8);
        }
        catch (IOException e){
            System.out.println("Unable to read input file");
        }
        return fileContents;
    }

}
