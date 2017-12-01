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
private JButton startButton, openButton, deleteButton, decompressButton, newLocker;
private JDialog jd;
private JTextArea taskOutput;
private Task task;
private JFileChooser fc;
private int start = 0;
private int delete = 0;
private int decompress = 0;
private int newlocker = 0;
private File[] fileNames;
private File referenceFile;
private File[] deletefileNames;
private File dirPath;



class Task extends SwingWorker<Void, Void> {
    /*
     * Main task. Executed in background thread.
     */
    @Override
    public Void doInBackground() {
        Deduplicator deduplicator = new Deduplicator();
        if(start == 1) {
            for (int ii = 0; ii<fileNames.length; ii++){
                File current =fileNames[ii];
                String currentFile = current.getAbsolutePath();
                deduplicator.addFile(currentFile, dirPath.getAbsolutePath());
            }
        }
        else if (delete == 1){
            //Delete()
            System.out.println("print");
            for(int ii = 0; ii<fileNames.length; ii++){
                deduplicator.delete(fileNames[ii].getAbsolutePath());
            }
        }
        else if(decompress == 1){
            for (int i = 0; i<fileNames.length; i++) {
                System.out.println(fileNames[i].getAbsolutePath());
                deduplicator.retrieve(fileNames[i].getAbsolutePath(), fileNames[i].getParent());
            }
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
        if (start == 1 ) {
            taskOutput.append("Compressed Files:");
            for (int i = 0; i < fileNames.length; i++) {
                taskOutput.append(fileNames[i].getName() + "\n");
            }
            taskOutput.append("In Locker "+ dirPath + "\n");
            start =0;
        }
       if (delete==1){//delete explanations

           for (int i = 0; i < fileNames.length; i++) {
               taskOutput.append(fileNames[i].getName() + "\n");
               taskOutput.append("Files Deleted!! \n");
           }
            delete =0;
        }
      if (decompress ==1){
            System.out.println("hell0");
            taskOutput.append("Decompressed Files in Locker with path " +fileNames[0].getParent() +"\n");
            for (int i = 0; i<fileNames.length; i++) {
                taskOutput.append(fileNames[i].getAbsolutePath()+"\n");
            }
            decompress = 0;
        }
      if (newlocker==1){
            taskOutput.append("New locker at path " + dirPath.getAbsolutePath() + "\n");
            newlocker = 0;
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

        newLocker = new JButton("Set/New Locker");
        newLocker.setActionCommand("newLocker");
        newLocker.addActionListener(this);


        fc = new JFileChooser();
        fc.setMultiSelectionEnabled(true);
        openButton = new JButton("Open a File...");
        openButton.addActionListener(this);

        progressBar = new JProgressBar(0, 100);
        progressBar.setValue(0);
        progressBar.setStringPainted(true);

        taskOutput = new JTextArea(5, 20);
        taskOutput.setBackground(Color.WHITE);
        taskOutput.setMargin(new Insets(5,5,5,5));
        taskOutput.setEditable(false);

        JPanel panel = new JPanel();
        panel.setBackground(Color.DARK_GRAY);

        panel.add(newLocker);
        panel.add(startButton);
        panel.add(deleteButton);
        panel.add(decompressButton);
       // panel.add(progressBar);
        add(panel, BorderLayout.PAGE_START);
        add(new JScrollPane(taskOutput), BorderLayout.CENTER);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        setMinimumSize(new Dimension(500,200));
        setBackground(Color.PINK);

    }

    /**
     * Invoked when the user presses the start button.
     */
    public void actionPerformed(ActionEvent evt) {
        startButton.setEnabled(false);
        if (evt.getSource() == startButton){
            fc.setDialogTitle("Select Files to be Deduplicated");
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
                fileNames = files;
                //This is where a real application would open the file
            }
            delete = 1;
        }

        else if(evt.getSource() == decompressButton) {
            fc.setDialogTitle("Select the '.depup' to decompress them");
            int returnVal = fc.showOpenDialog(DeduplicatorGui.this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File[] files = fc.getSelectedFiles();
                fileNames = files;
                //This is where a real application would open the file
            }
            decompress = 1;
        }
        else if(evt.getSource() == newLocker){
            String locker = JOptionPane.showInputDialog("ENTER NEW LOCKER NAME\n Or SET ");
            if((dirPath==null)) {
                locker = locker + ".locker";
                dirPath = new File(locker);
                System.out.println(dirPath.getAbsolutePath());
                if (!dirPath.exists()) {
                    boolean success = dirPath.mkdir();
                    if (!success) {
                        throw new IllegalArgumentException("Unable to create locker at" + locker);
                    }
                }
            }
            newlocker=1;
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