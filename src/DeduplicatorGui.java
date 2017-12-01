import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeListener;
import java.nio.charset.StandardCharsets;
import java.beans.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Random;


public class DeduplicatorGui extends JPanel implements ActionListener, PropertyChangeListener {

private JProgressBar progressBar;
private JButton startButton, openButton, deleteButton, decompressButton;
private JTextArea taskOutput;
private Task task;
private JFileChooser fc;
private int start = 0;
private int delete = 0;
private int decompress = 0;
private File[] fileNames;
private File referenceFile;
private File[] deletefileNames;


class Task extends SwingWorker<Void, Void> {
    /*
     * Main task. Executed in background thread.
     */
    @Override
    public Void doInBackground() {

        if(start == 1) {
            referenceFile =  fileNames[0];
            String ref = referenceFile.getName();
            try {
                int progress = 0;
                setProgress(0);
                String referenceContent = new String(Files.readAllBytes(Paths.get(ref)));
                for (int ii = 1; ii<fileNames.length; ii++){
                    File current =fileNames[ii];
                    String currentFile = current.getName();
                    try {
                        String curr = new String(Files.readAllBytes(Paths.get(currentFile)));
                        Compressor compress = new Compressor(referenceContent, curr);
                    }
                    catch (IOException ex){
                        System.out.println("IO exception");
                    }
                }
            }
            catch (IOException ex){
                System.out.println("IO exception");
            }
            start = 0;
        }
        else if (delete == 1){
            //Delete()
            //You can hide the file if you start it off with a . ex =  .filename.txt
            //if it is the first file, then hide it, otherwise delete

                //add a variable to compressor that has the max number for each file and just use this
                delete = 0;
        }
        else if(decompress == 1){
            String reference = "";
            String test = "";
            File path = fileNames[0].getParentFile();
            for (final File fileEntry : path.listFiles()) {
                if (fileEntry.getName().contains(".anchor")) {
                    reference = fileEntry.getName();
                } else {
                    System.out.println("No Anchor File");
                }
            }

            for (int i = 0; i<fileNames.length; i++) {
                try {
                    reference = new String(Files.readAllBytes(Paths.get(reference)), StandardCharsets.UTF_8);
                    test = new String(Files.readAllBytes(Paths.get(fileNames[i].getName())), StandardCharsets.UTF_8);
                } catch (IOException e) {
                    System.out.println("IO exception");
                }
                Decompressor D = new Decompressor(reference, test,fileNames[i].getName().replace(".txt",".meta"));
            }

            //System.out.println(D.getDecompressed());
            decompress = 0;
        }
        return null;
    }

    /*
     * Executed in event dispatching thread
     */
    @Override
    public void done() {
        Toolkit.getDefaultToolkit().beep();
        startButton.setEnabled(true);
        setCursor(null); //turn off the wait cursor
        if (start ==1 ) {
            if (!(fileNames.length == 0)) {
                for (int i = 0; i < fileNames.length; i++) {
                    taskOutput.append(fileNames[i].getName() + "\n");
                }
            }
            start =0;
        }
        else if (delete==1){//delete explanations
            if (!(deletefileNames.length == 0)) {
                for (int i = 0; i < fileNames.length; i++) {
                    taskOutput.append(fileNames[i].getName() + "\n");
                }
                taskOutput.append("Files Deleted!! \n");
            }
            delete =0;
        }
        else if (decompress ==1){
            taskOutput.append("Decompressed Files! \n");
            decompress = 0;
        }
        taskOutput.append("Done!\n");
    }
}

    public DeduplicatorGui() {
        super(new BorderLayout());

        //Create the demo's UI.
        startButton = new JButton("DeDuplicator");
        startButton.setActionCommand("start");
        startButton.addActionListener(this);

        deleteButton = new JButton( "Delete File");
        deleteButton.setActionCommand("delete");
        deleteButton.addActionListener(this);

        decompressButton = new JButton("Decompress Files");
        decompressButton.setActionCommand("decompress");
        decompressButton.addActionListener(this);

        fc = new JFileChooser();
        fc.setMultiSelectionEnabled(true);
        openButton = new JButton("Open a File...");
        openButton.addActionListener(this);

        progressBar = new JProgressBar(0, 100);
        progressBar.setValue(0);
        progressBar.setStringPainted(true);

        taskOutput = new JTextArea(5, 20);
        taskOutput.setMargin(new Insets(5,5,5,5));
        taskOutput.setEditable(false);

        JPanel panel = new JPanel();
        //panel.add(openButton);
        panel.add(startButton);
        panel.add(deleteButton);
        panel.add(decompressButton);
        panel.add(progressBar);

        add(panel, BorderLayout.PAGE_START);
        add(new JScrollPane(taskOutput), BorderLayout.CENTER);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

    }

    /**
     * Invoked when the user presses the start button.
     */
    public void actionPerformed(ActionEvent evt) {
        startButton.setEnabled(false);
        if (evt.getSource() == startButton){
            int returnVal = fc.showOpenDialog(DeduplicatorGui.this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File[] files = fc.getSelectedFiles();
                fileNames = files;
                //This is where a real application would open the file
            }
            start = 1;
        }

        else if (evt.getSource() == deleteButton){
            int returnVal = fc.showOpenDialog(DeduplicatorGui.this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File[] files = fc.getSelectedFiles();
                deletefileNames = files;
                //This is where a real application would open the file
            }
            delete = 1;
        }

        else if(evt.getSource() == decompressButton) {
            int returnVal = fc.showOpenDialog(DeduplicatorGui.this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File[] files = fc.getSelectedFiles();
                deletefileNames = files;
                //This is where a real application would open the file
            }
            decompress = 1;
        }

        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        //Instances of javax.swing.SwingWorker are not reusuable, so
        //we create new instances as needed.
        task = new Task();
        task.addPropertyChangeListener(this);
        task.execute();
    }

    /**
     * Invoked when task's progress property changes.
     */
    public void propertyChange(PropertyChangeEvent evt) {
       if ("progress"==evt.getPropertyName()) {
           int progress = (Integer) evt.getNewValue();
            progressBar.setValue(progress);
            taskOutput.append(String.format(
                    "Completed %d%% of task.\n", progress));
        }
    }


    /**
     * Create the GUI and show it. As with all GUI code, this must run
     * on the event-dispatching thread.
     */
    private static void createAndShowGUI() {
        //Create and set up the window.
        JFrame frame = new JFrame("Deduplicator");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //Create and set up the content pane.
        JComponent newContentPane = new DeduplicatorGui();
        newContentPane.setOpaque(true); //content panes must be opaque
        frame.setContentPane(newContentPane);

        //Display the window.
        frame.pack();
        frame.setVisible(true);
    }



    public static void main(String[] args) {
        //Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }


}