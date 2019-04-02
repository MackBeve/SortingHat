package edu.lclark.sortinghat;

import org.junit.Before;
import org.junit.Test;
import org.junit.Assert;

import java.io.File;
import java.util.ArrayList;


public class SortingHatTest {

    private SortingHat sortingHat;
    private ArrayList<Student> students;
    private ArrayList<Section> sections;
    private ArrayList<Student> assigned;
    private ArrayList<Student> assignedCopy;
    private Student studentA;
    private Section sectionA;
    private Report report;

    @Before
    public void setUp() {
//        sortingHat = new SortingHat(new File("csvparsetestSECT3.csv"), new File("csvparsetestSTUD3.csv"));
//        sortingHat = new SortingHat(new File("csvparsetestSECT2_headers.csv"), new File("csvparsetestSTUD2_headers.csv"));
        sortingHat = new SortingHat(new File("csvparsetestSECT.csv"), new File("csvparsetestSTUD.csv"));
        sections = sortingHat.getSections();
        students = sortingHat.getStudents();
        assigned = sortingHat.getAssigned();
        assignedCopy = new ArrayList<>(assigned); // copy not pointer ?
        report = new Report(sections, students);
        double time1 = System.nanoTime();
        sortingHat.sortHungarian();
        double time2 = System.nanoTime();
        //System.out.println(time2 - time1);
    }

    @Test
    public void studentCreatedFromCSV() {
        studentA = students.get(0);
//        Assert.assertEquals(true, studentA.getGender());
    }

    @Test
    public void sectionCreatedFromCSV() {
        sectionA = sections.get(0);
//        Assert.assertEquals("Beck", sectionA.getProf()); // for csvparsetestSECT and csvparsetestSTUD
//        Assert.assertEquals("DRAYTON", sectionA.getProf()); // for csvparsetestSECT2 and csvparsetestSTUD2
    }

    @Test
    public void preferencesNotEmpty() {
        Assert.assertTrue(students.get(1).getPreferences().size() > 0);
    }

    @Test
    public void numStudentsLessThanNumSeats() {
        int numSeats = sortingHat.getNumSeats();
        int numStudents = students.size();
        System.out.println(numSeats + ", " + numStudents);
        Assert.assertTrue(numSeats >= numStudents);
    }

    @Test
    public void buildCostMatrixIsNotNullValued() {
        double[][] costs = sortingHat.buildCostMatrix();
        for (int i = 0; i < costs.length; i++) {
            for (int j = 0; j < costs.length; j++) {
                if (costs[i][j] < 1) {
                    Assert.fail("Found value less than 1");
                }
                if (Double.isNaN(costs[i][j])) {
                    Assert.fail("Found null value");
                }
                if (Double.isInfinite(costs[i][j])) {
                    Assert.fail("Found infinite value");
                }
            }
        }
        Assert.assertFalse(false);
    }


    @Test
    public void sortSetsSectionsForEveryStudent() {
        // Check that the section field of every student is set.
        for (int i = 0; i < students.size(); i++) {
            Assert.assertNotNull(students.get(i).getAssignedSection());
        }

    }

    @Test
    public void sortSetsAppropriateSizeSection() {
        for (int i = 0; i < sections.size(); i++) {
            Assert.assertTrue(sections.get(i).getNumStudents() < 20);   // 20 is arbitrary max size
        }
    }

    @Test
    public void sortDoesNotDuplicateStudents() {
        // Look at every section and assert we never have two identical students in a class
        ArrayList<Student> seen = new ArrayList<>();
        for (Section sec : sections) {
            for (Student stud : sec.getStudents()) {
                if (seen.contains(stud)) {
                    Assert.fail("Error: " + stud.getStudentNo() + " is assigned more than once.");
                } else {
                    seen.add(stud);
                }
            }
        }
    }


    @Test
    public void sortGivesMostTheirTopChoices() {
        // Our sorting algorithm should place most students into one of their top two preferences
        double[] percentages = report.percentages();

        System.out.println("The percentage of students in their 1st choice is: " + (percentages[0]));
        System.out.println("The percentage of students in their 2nd choice is: " + (percentages[1]));
        System.out.println("The percentage of students in their 3rd choice is: " + (percentages[2]));
        System.out.println("The percentage of students in their 4th choice is: " + (percentages[3]));
        System.out.println("The percentage of students in their 5th choice is: " + (percentages[4]));
        System.out.println("The percentage of students in their 6th choice is: " + (percentages[5]));
        System.out.println("The number of students who did not get any of their preferences is: " + (report.numStudentsGotNoPreferences()));

        int[] reportPref = report.preferences();

        for (int i = 0; i < reportPref.length; i++) {
            System.out.println(reportPref[i] + " students got their choice " + (i + 1));
        }

        Assert.assertTrue(.5 < (0.0 + percentages[0] + percentages[1]));
        Assert.assertEquals(0.0, percentages[5], 0.001);
        Assert.assertEquals(0, report.numStudentsGotNoPreferences());
    }

    @Test
    public void sortUpdatesSectionsAndStudents() {
        // Check that sortHungarian updates students' section and a section's students, and that they match.
        for (Student stud : students) {
            Assert.assertTrue(stud.getAssignedSection().hasStudent(stud));
        }
    }

    @Test
    public void noStudentRecievesIllegalSection() {
//        for(Section s: sections){
//            System.out.println(s.getProf());
//        }
        for (Student s : students) {
            for (String illegalSection : s.getIllegalSections()) {
                //System.out.println(s.getStudentNo() + " " + p + " " + s.getSection().getProf());
                //students without a previous professor will have the the empty string in the prevprof field
                if (!illegalSection.equals("")) {
                    Assert.assertFalse(s.getIllegalSections().contains(s.getAssignedSection()));
                }
            }
        }
    }

    @Test
    public void hungarianAlgorithmReturnsCorrectAnswerForSmallProblem() {
        double[][] cost = {{82.0, 83, 69, 92}, {77, 37, 49, 92}, {11, 69, 5, 86}, {8, 9, 98, 23}};
        int[] actual = {2, 1, 0, 3};
        HungarianAlgorithm hungary = new HungarianAlgorithm(cost);
        int[] result = hungary.execute();
        for (int i = 0; i < 4; i++) {
            Assert.assertEquals(actual[i], result[i]);
        }
    }

    @Test
    public void hungarianAlgorithmReturnsCorrectAnswerForMediumProblem() {
        // This is for a medium-size problem
        double[][] cost = {
                {1, 1, 2, 2, 3, 3, 4, 4, 5, 5},
                {1, 1, 2, 2, 3, 3, 5, 5, 4, 4},
                {2, 2, 3, 3, 4, 4, 100, 100, 1, 1},
                {1, 1, 5, 5, 3, 3, 2, 2, 4, 4},
                {3, 3, 2, 2, 4, 4, 1, 1, 5, 5},
                {5, 5, 1, 1, 2, 2, 3, 3, 4, 4},
                {100, 100, 100, 100, 1, 1, 2, 2, 3, 3},
                {4, 4, 100, 100, 3, 3, 2, 2, 1, 1},
                {3, 3, 2, 2, 1, 1, 4, 4, 5, 5},
                {100, 100, 100, 100, 100, 100, 100, 100, 100, 100}
        };
        int[] actual = {0, 1, 8, 6, 7, 2, 4, 9, 5, 3};
        int[] actual2 = {1, 0, 9, 7, 6, 3, 5, 8, 2};
        HungarianAlgorithm hungary = new HungarianAlgorithm(cost);
        int[] result = hungary.execute();
        for (int i = 0; i < 10; i++) {
            boolean statement = result[i] == actual[i] || result[i] == actual2[i];
            Assert.assertTrue(statement);
        }
    }

    @Test
    public void hungarianAlgorithmSortsTwoStudentsInSurplusArray() {
        // This is for a medium-size problem
        double[][] cost = {
                {1, 1, 2, 2, 100, 100, 100, 100, 100, 100},
                {2, 2, 100, 100, 1, 1, 100, 100, 100, 100},
                {100, 100, 100, 100, 100, 100, 100, 100, 100, 100},
                {100, 100, 100, 100, 100, 100, 100, 100, 100, 100},
                {100, 100, 100, 100, 100, 100, 100, 100, 100, 100},
                {100, 100, 100, 100, 100, 100, 100, 100, 100, 100},
                {100, 100, 100, 100, 100, 100, 100, 100, 100, 100},
                {100, 100, 100, 100, 100, 100, 100, 100, 100, 100},
                {100, 100, 100, 100, 100, 100, 100, 100, 100, 100},
                {100, 100, 100, 100, 100, 100, 100, 100, 100, 100}
        };
        HungarianAlgorithm hungary = new HungarianAlgorithm(cost);
        int[] result = hungary.execute();
        Assert.assertTrue(result[0] == 0 || result[0] == 1);
        Assert.assertTrue(result[1] == 4 || result[1] == 5);
    }

    @Test
    public void sortMakesGenderBalancedClasses() {
        for (Section sec : sections) {
            int numMales = 0;
            for (Student stud : sec.getStudents()) {
                if (stud.getGender()) {
                    numMales++;
                }
            }
            System.out.println((numMales + 0.0) / sec.getNumStudents());
            System.out.println(sec.getSectionNo());
            System.out.println(sec.getNumStudents());

            if (sec.getNumStudents() < 10) {
                Assert.assertTrue((numMales + 0.0) / sec.getNumStudents() >= 0.20 && (numMales + 0.0) / sec.getNumStudents() <= 0.80);
                continue;
            }
            Assert.assertTrue((numMales + 0.0) / sec.getNumStudents() >= 0.30 && (numMales + 0.0) / sec.getNumStudents() <= 0.70);
        }
    }

    @Test
    public void sortDoesNotChangeAssignedStudents() {
//        Assert.assertTrue(false);
        for (Student s : assigned) {
            Assert.assertTrue(assignedCopy.contains(s));
        }
    }

}