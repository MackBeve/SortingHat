package edu.lclark.sortinghat;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

public class GUI extends JFrame {

    private Section section;
    private SortingHat sortingHat;
    private Student student;
    private SortingHatMain sortingHatMain;
    private JLabel labelStudents;
    private JLabel labelSections;
    private JLabel labelRun;
    private JLabel labelInstructions;
    private JTextPane output;
    private JLabel labelProgressBar;
    private JButton instructions;
    private JButton browseStudents;
    private JButton browseSections;
    private JButton run;
    private JTextField filename;
    private int flag; // Requires flag = 2 to be able to click RUN button
    private JProgressBar progressBar;
    private String sectionFilePath;
    private String studentFilePath;
    private Report report;


    // Constructor creates GUI, including buttons and progress bar
    public GUI() throws HeadlessException {

        // Initialize
        labelStudents = new JLabel();
        labelSections = new JLabel();
        labelInstructions = new JLabel();
        labelRun = new JLabel();
        labelProgressBar = new JLabel();
        instructions = new JButton("Instructions");
        browseStudents = new JButton("Browse Students");
        browseSections = new JButton("Browse Sections");
        run = new JButton("Run");
        filename = new JTextField();
        flag = 0;
        progressBar = new JProgressBar(0, 100); // Change 100 to task.getLengthOfTask()

        // Create and initialize grid and tooltips
        GridBagLayout layout = new GridBagLayout();
        setLayout(layout);
        setSize(800, 600);
        ToolTipManager.sharedInstance().setInitialDelay(0);

        // Create buttons, progress bar and panels for input
        output = new JTextPane();
        output.setEnabled(false);
        output.setBackground(Color.white);
        run.setEnabled(false);
        progressBar.setValue(0);
        progressBar.setStringPainted(true);

        // Create tooltips for buttons
        instructions.setToolTipText("Read the instruction");
        browseStudents.setToolTipText("Attach CSV file of students");
        browseSections.setToolTipText("Attach CSV file of sections");
        run.setToolTipText("Run the program");

        // Initialize action listeners
        Instruction instruction = new Instruction();
        BrowseStudents browseStudent = new BrowseStudents();
        BrowseSections browseSection = new BrowseSections();
        RunPrograms runProgram = new RunPrograms();

        // Add action listener
        instructions.addActionListener(instruction);
        browseStudents.addActionListener(browseStudent);
        browseSections.addActionListener(browseSection);
        run.addActionListener(runProgram);

        // Initialize labels
        labelStudents.setText("Attach students CSV file");
        labelSections.setText("Attach sections CSV file");
        labelInstructions.setText("Read Instructions");
        labelRun.setText("Run program");
        labelProgressBar.setText("Status");

        // Add progress bar and buttons to input
        add(labelInstructions, new GBC(0, 0, 1, 1).setFill(GBC.BOTH).setWeight(100, 100));
        add(instructions, new GBC(1, 0, 1, 1).setFill(GBC.BOTH).setWeight(100, 100));
        add(labelStudents, new GBC(0, 1, 1, 1).setFill(GBC.BOTH).setWeight(100, 100));
        add(browseStudents, new GBC(1, 1, 1, 1).setFill(GBC.BOTH).setWeight(100, 100));
        add(labelSections, new GBC(0, 2, 1, 1).setFill(GBC.BOTH).setWeight(100, 100));
        add(browseSections, new GBC(1, 2, 1, 1).setFill(GBC.BOTH).setWeight(100, 100));
        add(labelRun, new GBC(0, 3, 1, 1).setFill(GBC.BOTH).setWeight(100, 100));
        add(run, new GBC(1, 3, 1, 1).setFill(GBC.BOTH).setWeight(100, 100));
        add(labelProgressBar, new GBC(0, 4, 1, 1).setFill(GBC.BOTH).setWeight(100, 100));
        add(output, new GBC(1, 4, 1, 1).setFill(GBC.BOTH).setWeight(100, 100));
        add(progressBar, new GBC(0, 5, 2, 1).setFill(GBC.BOTH).setWeight(100, 100));
    }

    private class BrowseStudents implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            JFileChooser jfc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());

            jfc.setDialogTitle("Select a CSV file");
            jfc.setAcceptAllFileFilterUsed(false);
            FileNameExtensionFilter filter = new FileNameExtensionFilter("CSV files", "csv");
            jfc.addChoosableFileFilter(filter);

            int returnValue = jfc.showOpenDialog(GUI.this);
            // int returnValue = jfc.showSaveDialog(null);

            if (returnValue == JFileChooser.APPROVE_OPTION) {
                File selectedFile = jfc.getSelectedFile();
                if (filter.accept(selectedFile)) {
                    String text = selectedFile.getAbsolutePath().toString();
                    File f = new File(text);
                    flag++;
                    studentFilePath = text;
                    String previous = output.getText();
                    output.setText(previous + "\n" + "Student File found at " + f.getName() + '\n');
                    // Activate Run
                    if (flag == 2) {
                        run.setEnabled(true);
                    }

                    revalidate();
                }
            }
        }
    }

    private class BrowseSections implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            JFileChooser jfc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());

            jfc.setDialogTitle("Select a CSV file");
            jfc.setAcceptAllFileFilterUsed(false);
            FileNameExtensionFilter filter = new FileNameExtensionFilter("CSV files", "csv");
            jfc.setFileFilter(filter);

            int returnValue = jfc.showOpenDialog(GUI.this);
            // int returnValue = jfc.showSaveDialog(null);

            if (returnValue == JFileChooser.APPROVE_OPTION) {
                File selectedFile = jfc.getSelectedFile();
                if (filter.accept(selectedFile)) {
                    String text = selectedFile.getAbsolutePath().toString();
                    File f = new File(text);
                    flag++;
                    sectionFilePath = text;
                    String previous = output.getText();
                    output.setText(previous + "\n" + "Section File found at " + f.getName() + '\n');
                    // Activate Run
                    if (flag == 2) {
                        run.setEnabled(true);
                    }

                    revalidate();
                }
            }
        }
    }

    private class Instruction implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            JOptionPane.showMessageDialog(null, "Welcome to Sorting Hat, where we sort students into E&D sections.\n" +
                    "1. Please click the \"Browse Students\" button and select a CSV file.\n" +
                    "2. Please click the \"Browse Sections\" button and select a CSV file.\n" +
                    "3. Please click the  \"Run\" button.", "Instructions", 1);
        }
    }

    private class RunPrograms implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            sortingHatMain = new SortingHatMain(sectionFilePath, studentFilePath);
//            sortingHatMain.printParse();
            // Run Sorting Hat
            report = new Report(sortingHatMain.getSortingHat().getSections(), sortingHatMain.getSortingHat().getStudents());
            run.setEnabled(false);
            ProgressWorker pw = new ProgressWorker();
            pw.addPropertyChangeListener(new PropertyChangeListener() {
                @Override
                public void propertyChange(PropertyChangeEvent evt) {
                    String name = evt.getPropertyName();
                    if (name.equals("progress")) {
                        int progress = (int) evt.getNewValue();
                        progressBar.setValue(progress);
                        repaint();
                    } else if (name.equals("state")) {
                        SwingWorker.StateValue state = (SwingWorker.StateValue) evt.getNewValue();
                        switch (state) {
                            case DONE:
                                run.setEnabled(true);
                                break;
                        }
                    }

                }

            });
            pw.execute();
        }


    }


    public class ProgressWorker extends SwingWorker<Object, Object> {
        @Override
        protected Object doInBackground() throws Exception {
            for (int i = 0; i <= 100; i++) {
                setProgress(i);
//                double progress = sortingHat.getGreedyAlgorithm().getSections().size() /
//                setProgress();
                if (i == 100) {
                    double[] percentages = report.percentages();

                    output.setText("Output file created at User folder");
                    for (int j = 0; j < 6; j++) {
                        String previous = output.getText();
                        output.setText(previous + "\n The percentage of students in their number " + (j + 1) + " choice is: " + (((int) (10000 * percentages[j])) / 100.0) + "%");
                    }
                    String previous = output.getText();
                    output.setText(previous + "\nThe number of students who did not get any of their preferences is: " + (report.numStudentsGotNoPreferences()));
                    ArrayList<String> badStudents = report.studentsWithIllegalPreferences();
                    if (badStudents.size() != 0) {
                        previous = output.getText() + "\nThese students listed a previous professor in their preferences:";
                        for (String s : badStudents) {
                            previous = previous + " " + s;
                        }
                        output.setText(previous);
                    }

                }
                try {
                    Thread.sleep(10);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return null;
        }
    }
}
