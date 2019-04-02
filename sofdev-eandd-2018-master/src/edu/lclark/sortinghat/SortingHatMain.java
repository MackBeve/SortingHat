package edu.lclark.sortinghat;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileReader;

public class SortingHatMain {

    private SortingHat sortingHat;

    /**
     * This launches the GUI and also contains the sorting hat rules
     *
     * @param args
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            JFrame frame = new GUI();
            frame.setTitle("Sorting Hat GUI");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setVisible(true);
        });
    }

    public SortingHatMain(String sectionSelectedFile, String studentSelectedFile) {
        sortingHat = new SortingHat(new File(sectionSelectedFile), new File(studentSelectedFile));
        sortingHat.run();
    }

    /**
     * Test method to make sure CSV is parsed. May be deprecated when run() changes from just printing stuff
     */
//    public void printParse() {
//        sortingHat.run();
//    }
    public SortingHat getSortingHat() {
        return sortingHat;
    }
}
