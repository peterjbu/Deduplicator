
import javafx.concurrent.Task;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeListener;
import java.util.Random;
import java.beans.*;
import java.io.*;

public class DeduplicatorGui extends JPanel implements ActionListener, PropertyChangeListener {

private JProgressBar progressBar;
private JButton startButton, openButton, deleteButton;
private JTextArea taskOutput;
private Task task;
private JFileChooser fc;
private int start = 0;
private int delete = 0;
private String fileNames ="";

class Task extends SwingWorker<Void, Void> {
    /*
     * Main task. Executed in background thread.
     */
    @Override
    public Void doInBackground() {

        if(start == 1) {
            Random random = new Random();
            String file1_test = "helloaynameisesebnhellomynamespeterhellocynameisjosh";
            String file2_test = "hellomynameisesenhellomynameispeterhellomynameisjosh";
            int progress = 0;
            //Initialize progress property.
            setProgress(0);
            while (progress < 100) {
                //Sleep for up to one second.
                try {
                    Thread.sleep(random.nextInt(100));
                } catch (InterruptedException ignore) {}
                //Make random progress.
                progress += random.nextInt(10);
                setProgress(Math.min(progress, 100));
            }

            start = 0;
        }
        else if (delete == 1){
            //Delete()
                //Sleep for up to one second.
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ignore) {
                }
                //add a variable to compressor that has the max number for each file and just use this
                delete = 0;
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
        if(!fileNames.equals("")){
            taskOutput.append(fileNames + "\n");
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
        panel.add(openButton);
        panel.add(startButton);
        panel.add(deleteButton);
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
            start = 1;
            System.out.println("start");
        }

        if (evt.getSource() == deleteButton){
            delete = 1;
            System.out.println("delete");
        }

        if (evt.getSource() == openButton) {
            int returnVal = fc.showOpenDialog(DeduplicatorGui.this);

            if (returnVal == JFileChooser.APPROVE_OPTION) {
               File[] file = fc.getSelectedFiles();
               for (int ii = 0; ii<file.length; ii++){
                   File current =file[ii];
                   fileNames += current.getName() + "\n";
               }
                //This is where a real application would open the file
                System.out.println(fileNames);
            }
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