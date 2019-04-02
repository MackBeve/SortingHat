package edu.lclark.sortinghat;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class Report {

    ArrayList<Student> students;
    ArrayList<Section> sections;

    /**
     * Construct a Report
     *
     * @param sections the sections you want to report on
     * @param students the students you want to report on
     */
    public Report(ArrayList<Section> sections, ArrayList<Student> students) {
        this.sections = sections;
        this.students = students;
    }

    /**
     * The number of students who recieve each of their preferences
     *
     * @return int[] the ith entry is the number of students who go their ith choice
     */
    public int[] preferences() {
        int[] preferenceResults = new int[6];
        for (Student s : students) {
            ArrayList<Section> pref = s.getPreferences();
            for (int j = 0; j < pref.size(); j++) {
                if (pref.get(j).equals(s.getAssignedSection())) {
                    preferenceResults[j]++;
                    break;
                }
            }
        }
        return preferenceResults;
    }

    /**
     * The percent of students who got each of their preferences
     *
     * @return double[] the ith entry in the percent of students who got their ith choice
     */
    public double[] percentages() {
        int numStudents = numStudentsWithPreferences();
        int[] preferences = preferences();
        double[] percentages = new double[6];
        for (int i = 0; i < 6; i++) {
            percentages[i] = (0.0 + preferences[i]) / numStudents;
        }
        return percentages;
    }

    /**
     * @return The number of students who list at least 4 preferences
     */
    public int numStudentsWithPreferences() {
        int n = 0;
        for (Student s : students) {
            //TODO how many preferences is too few?
            if (s.getPreferences().size() > 3) {
                n++;
            }
        }
        return n;
    }

    /**
     * Gets the number of students not assigned to any of their preferences, does not consider students who list 3 or fewer preferences
     *
     * @return the number of students who were not assigned to one of their preferences
     */
    public int numStudentsGotNoPreferences() {
        int n = 0;
        for (Student s : students) {
            if ((!s.getPreferences().contains(s.getAssignedSection())) && (s.getPreferences().size() > 3)) {
                n++;
            }
        }
        return n;
    }

    /**
     * @return the number of male students
     */
    public int numMales() {
        int n = 0;
        for (Student s : students) {
            if (s.getGender()) {
                n++;
            }
        }
        return n;
    }

    /**
     * @return the number of female students
     */
    public int numFemales() {
        return students.size() - numMales();
    }

    /**
     * How popular a given section is
     *
     * @param sectionNo the section number
     * @return int[] the ith entry corresponds to the nubmer of students that listed the section as their ith preference
     */
    public int[] sectionPopularity(String sectionNo) {
        int[] num = new int[6];
        for (Student s : students) {
            for (int i = 0; i < s.getPreferences().size(); i++) {
                if (s.getPreferences().get(i).equals(sectionNo)) {
                    num[i]++;
                }
            }
        }
        return num;
    }

    /**
     * Gets all the students who have illegal preferences
     *
     * @return An arrayList of Strings of the studentNo of the students with illegal preferences
     */
    public ArrayList<String> studentsWithIllegalPreferences() {
        ArrayList<String> badStudents = new ArrayList<>();
        for (Student s : students) {
            for (Section p : s.getPreferences()) {
                if (s.getIllegalSections().contains(p.getSectionNo())) {
                    badStudents.add(s.getStudentNo());
                }
            }
        }
        return badStudents;
    }

}
