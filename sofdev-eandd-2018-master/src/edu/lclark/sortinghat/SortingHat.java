package edu.lclark.sortinghat;

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.Array;
import java.util.*;

public class SortingHat {


    private ArrayList<Section> sections;
    private ArrayList<Student> students;
    private ArrayList<Student> unassigned;
    private ArrayList<Student> assigned;

    private int numSeats;
    private SeatIndexMap seatMap;

    private double[][] cost;

    /**
     * The proper constructor which takes files as parameters
     *
     * @param sectionsFile
     * @param studentsFile
     */
    public SortingHat(File sectionsFile, File studentsFile) {
        sections = new ArrayList<>();
        students = new ArrayList<>();
        assigned = new ArrayList<>();
        unassigned = new ArrayList<>();

        parseSectionCSV(sectionsFile);
        parseStudentCSV(studentsFile);

        numSeats = 0;
        for (Section s : this.sections) {
            numSeats += s.getNumAvailableSeats();
        }
        seatMap = new SeatIndexMap(sections, numSeats);
    }

    /**
     * Puts section information from CSV file into section objects
     *
     * @param file
     */
    public void parseSectionCSV(File file) {
        int index = 0;
        Scanner scanner = null;
        try {
            scanner = new Scanner(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        int sectionNoIndex = 0;
        int professorIndex = 1;
        int capIndex = 2;

        ArrayList<String> possibleCourseSectionNames = new ArrayList<>();
        possibleCourseSectionNames.add("course section #");
        possibleCourseSectionNames.add("course number");
        possibleCourseSectionNames.add("course no");
        possibleCourseSectionNames.add("course no.");

        ArrayList<String> possibleCapNames = new ArrayList<>();
        possibleCapNames.add("cap");
        possibleCapNames.add("size");
        possibleCapNames.add("number students");

        List<String> row = CSVReader.parseLine(scanner.nextLine());

        boolean header = possibleCourseSectionNames.contains(row.get(0)) || row.get(0).toLowerCase().equals("professor")
                || row.get(0).toLowerCase().equals("size");
        // if there is a header
        if (header) {
            if (row.size() > 2) {
                for (int i = 0; i < 3; i++) {
                    if (possibleCourseSectionNames.contains(row.get(i).toLowerCase().trim())) {
                        sectionNoIndex = i;
                    } else if (row.get(i).trim().toLowerCase().equals("professor")) {
                        professorIndex = i;
                    } else if (possibleCapNames.contains(row.get(i).toLowerCase().trim())) {
                        capIndex = i;
                    }
                }
            }
        }

        // look for "Course Section #", or "Course Number"
        if (header) {
            while (scanner.hasNext()) {

                row = CSVReader.parseLine(scanner.nextLine());

                String sectionNo = row.get(sectionNoIndex);
                String prof = row.get(professorIndex);

                if (prof.charAt(0) == '\"') {
                    prof = prof.substring(1, prof.length());
                }
                if (prof.charAt(prof.length() - 1) == ' ') {
                    prof = prof.substring(0, prof.length() - 1);
                }

                if (row.size() <= 2) {
                    sections.add(new Section(sectionNo, prof, ++index));
                } else {
                    sections.add(new Section(sectionNo, prof, ++index, Integer.parseInt(row.get(capIndex))));
                }
            }
        } else {
            try {
                scanner = new Scanner(file);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            while (scanner.hasNext()) {

                row = CSVReader.parseLine(scanner.nextLine());

                String sectionNo = row.get(sectionNoIndex);
                String prof = row.get(professorIndex);

                if (prof.charAt(0) == '\"') {
                    prof = prof.substring(1, prof.length());
                }
                if (prof.charAt(prof.length() - 1) == ' ') {
                    prof = prof.substring(0, prof.length() - 1);
                }

                if (row.size() <= 2) {
                    sections.add(new Section(sectionNo, prof, ++index));
                } else {
                    sections.add(new Section(sectionNo, prof, ++index, Integer.parseInt(row.get(capIndex))));
                }
            }
        }

        scanner.close();
    }

    /**
     * Puts student information from CSV file into student objects
     *
     * @param file
     */
    public void parseStudentCSV(File file) {

        String[] headersArray = { "Placement", "Identifying Number", "M / F", "Athlete", "Sport", "SSS", "AES", "Transfer",
                "Continuing", "Prev. E&D Instructor", "Section #'s can't be placed into (due to prev professor)",
                "Choice 1", "Choice 2", "Choice 3", "Choice 4", "Choice 5", "Choice 6" };

        ArrayList<String> headers = new ArrayList<>();
        for (String s : headersArray) {
            headers.add(s.toLowerCase().trim()); // TODO: why doesn't toLowerCase() do anything???
        }

        HashMap<String, Integer> headerMap = new HashMap<>();

        Scanner scanner = null;
        try {
            scanner = new Scanner(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        List<String> row = CSVReader.parseLine(scanner.nextLine());
        boolean header = headers.contains(row.get(0).toLowerCase().trim()); // why untrue//?

        if (header) {
            for (int i = 0; i < row.size(); i++) {
                headerMap.put(row.get(i), i);
            }
//            System.out.println(headerMap.toString());
            while (scanner.hasNext()) {
                row = CSVReader.parseLine(scanner.nextLine());
//                System.out.println(headerMap.toString());
                String id = row.get(headerMap.get("identifying number"));

                boolean male = row.get(headerMap.get("M / F")).equals("M");

                boolean athlete = row.get(headerMap.get("Athlete")).equals("y");

                // Adds pre-assigned students to assigned<>
                if (!row.get(headerMap.get("Placement")).isEmpty()) { //TODO: MAke sure this works
                    for (Section s : sections) {
                        if (s.getSectionNo().equals(row.get(headerMap.get("Placement")))) {
                            Student peter = new Student(id, male, athlete, s); // peter is an preassigned student
                            if (!s.addStudent(peter)) {
                                System.out.println("ERROR! Could not pre-assign student to section.");
                            }
                            assigned.add(peter);
                            break;
                        }
                    }
                    continue;
                }

                ArrayList<Section> preferences = new ArrayList<>(); // TODO: reference already parsed sections arraylist

                for (int i = 0; i < 6; i++)
                    for (Section s : sections) {
                        if (s.getSectionNo().equals(row.get(headerMap.get("Choice 1") + i))) {
                            preferences.add(sections.get(s.getIndex() - 1)); // Goes out of bounds (removed a -1 and it works!)
                        }
                    }

//                ArrayList<String> prevProfs = new ArrayList<>(); // TODO: this should add the entire list..
//                prevProfs.add(row.get(headerMap.get("Prev. E&D Instructor")));

                // illegal sections
                String illegal = row.get(headerMap.get("Section #'s can't be placed into (due to prev professor)"));

                //parse illegal sections
                ArrayList<String> illegalSections = parseIllegalSections(illegal);

                //remove previous profs from preferences
                preferences.removeAll(illegalSections); // TODO: does this work???

                students.add(new Student(id, preferences, male, athlete, illegalSections));
            }
        } else {
            try {
                scanner = new Scanner(file);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            while (scanner.hasNext()) {
                row = CSVReader.parseLine(scanner.nextLine());
                String id = row.get(1);

                boolean male = row.get(2).equals("M");

                boolean athlete = row.get(3).equals("y");

                // Adds pre-assigned students to assigned<>
                if (!row.get(0).isEmpty()) { //TODO: MAke sure this works
                    for (Section s : sections) {
                        if (s.getSectionNo().equals(row.get(0))) {
                            Student peter = new Student(id, male, athlete, s);
                            if (!s.addStudent(peter)) {
                                System.out.println("ERROR! Could not pre-assign student to section.");
                            }
                            ;
                            assigned.add(peter);
                            break;
                        }
                    }
                    continue;
                }

                ArrayList<Section> preferences = new ArrayList<>(); // TODO: reference already parsed sections arraylist

                for (int i = 0; i < 6; i++)
                    for (Section s : sections) {
                        if (s.getSectionNo().equals(row.get(11 + i))) {
                            preferences.add(sections.get(s.getIndex() - 1)); // Goes out of bounds (removed a -1 and it works!)
                        }
                    }

//                ArrayList<String> prevProfs = new ArrayList<>(); // TODO: this should add the entire list..
//                prevProfs.add(row.get(9));

                // illegal sections
                String illegal = row.get(10);
                //parse illegal sections
                ArrayList<String> illegalSections = parseIllegalSections(illegal);

                //remove previous profs from preferences
                preferences.removeAll(illegalSections); // TODO: does this work???
                students.add(new Student(id, preferences, male, athlete, illegalSections));
            }

        }

        scanner.close();

    }

    /**
     *
     * Parses illegal sections... duh
     */
    public ArrayList<String> parseIllegalSections(String illegal) {
        ArrayList<String> illegalSections = new ArrayList<>();
        if (illegal.contains(",")) {
            String[] illegalArray = illegal.split(",");
            for (String s : illegalArray) {
                s = s.replaceAll("\\s+", "");
                if (s.charAt(0) == '\"') {
                    s = s.substring(1, s.length());
                }
                if (s.charAt(s.length() - 1) == '\"') {
                    s = s.substring(0, s.length() - 1);
                }
                illegalSections.add(s);
            }
        } else {
            illegalSections.add(illegal);
        }
        return illegalSections;
    }

//    public ArrayList<String> removeIllegalPreferences(ArrayList<String> illegalSections, ArrayList<Section> preferences) {
//
//        for (String p : illegalSections) {
//            //copy preferences
//            ArrayList<Section> prefcopy = new ArrayList<>(preferences);
//            for (Section s : preferences) {
//                if (s.equals(p)) {
//                    prefcopy.remove(s);
//                }
//            }
//            preferences = prefcopy;
//        }
//        return prefcopy;
//    }

    /**
     * Non-static run boy
     */
    public void run() {
        sortHungarian();
        new Output(sections, students, assigned);
    }

    /**
     * Sorts the students into sections using the hungarian algorithm
     */
    public void sortHungarian() {   //TODO: Refactor to use copies of sections & students

        // Convert ArrayLists to arrays as prep for hungarian algorithm
        Section[] sectionsArray = new Section[sections.size()];
        Student[] studentArray = new Student[students.size()];
        sectionsArray = sections.toArray(sectionsArray);
        studentArray = students.toArray(studentArray);

        // Build the cost matrix and execute the hungarian algorithm
        buildCostMatrix();
        HungarianAlgorithm hungary = new HungarianAlgorithm(cost);
        int[] result = hungary.execute();


        for (int i = 0; i < studentArray.length; i++) {
            // Handle cases where people[i] is -1 (unassigned student)
            if (result[i] < 0) {
                unassigned.add(studentArray[i]);
                continue;
            }
            // Assign students to sections, and update section field of students
            Section currentSection = seatMap.getSection(result[i]); // people[i] might be negative
            currentSection.addStudent(studentArray[i]);
            studentArray[i].setAssignedSection(currentSection);
        }

    }

    /**
     * Builds a the cost matrix for use in the Hungarian algorithm.
     *
     * @return the cost matrix, a 2D array of doubles.  All entries should be finite and positive.
     */
    public double[][] buildCostMatrix() { // sectionsArray will be used later to hard encode male/female ratios, etc

        double alpha = 3.5;
        double defaultWeight = Math.pow(alpha, 7);
        double illegalWeight = Math.pow(alpha, 9);
        double ratioMin = .3; //minimum ratio for a gender
        int numFemale;
        int numMale;

        Section currentSection;

        Section[] sectionsArray = new Section[sections.size()];
        Student[] studentsArray = new Student[students.size()];
        sectionsArray = sections.toArray(sectionsArray);
        studentsArray = students.toArray(studentsArray);
        // Initialize cost array
        cost = new double[numSeats][numSeats];

        // Sets everything to nullWeight
        for (int i = 0; i < numSeats; i++) {
            for (int j = 0; j < numSeats; j++) {
                cost[i][j] = defaultWeight;
            }
        }

        // Loop through students, grab their preferences, and update the cost matrix for each preference
        // Currently accounts for gender by setting the first 'n' seats to female, next 'n' to male.
        for (int i = 0; i < studentsArray.length; i++) {

            // Loop through the preferences
            ArrayList<Section> preferences = studentsArray[i].getPreferences();
            for (int j = 0; j < preferences.size(); j++) {
                currentSection = preferences.get(j);
                // Get the indices in the cost matrix corresponding to the section of current preference
                int[] indices = seatMap.getIndices(currentSection);
                //set numMale and numFemale
                numFemale = (int) (currentSection.getCap() * ratioMin + .5) - currentSection.getNumFemaleStudents();
                numMale = (int) (currentSection.getCap() * ratioMin + .5) - currentSection.getNumMaleStudents();
                for (int k = 0; k < indices.length; k++) {
                    // Set the first 'n' seats to be female
                    if (k < numFemale) {
                        if (!studentsArray[i].getGender()) {
                            cost[i][indices[k]] = weightFunction(alpha, j);
                        }
                    }
                    // Set the next 'n' seats to be male
                    else if (k < numMale + numFemale) {
                        if (studentsArray[i].getGender()) {
                            cost[i][indices[k]] = weightFunction(alpha, j);
                        }
                    }
                    // The remaining seats should be biased only by the preference level
                    else {
                        cost[i][indices[k]] = weightFunction(alpha, j);
                    }
                }
            }

            // Loop through remaining non-preferred sections and account for gender balance
            for (int j = 0; j < sections.size(); j++) {
                currentSection = sections.get(j);
                if (preferences.contains(currentSection)) {
                    continue;
                } // skip if we've already done it in preferences

                // Get the indices in the cost matrix corresponding to the section of current preference
                int[] indices = seatMap.getIndices(currentSection);

                //set numMale and numFemale
                numFemale = (int) (currentSection.getCap() * ratioMin + .5) - currentSection.getNumFemaleStudents();
                numMale = (int) (currentSection.getCap() * ratioMin + .5) - currentSection.getNumMaleStudents();
                for (int k = 0; k < indices.length; k++) {
                    // Set the first 'n' seats to be biased against males
                    if (k < numFemale) {
                        if (studentsArray[i].getGender()) {
                            cost[i][indices[k]] = illegalWeight;
                        }
                    }
                    // Set the next 'n' seats to be biased against females
                    else if (k < numMale + numFemale) {
                        if (!studentsArray[i].getGender()) {
                            cost[i][indices[k]] = illegalWeight;
                        }
                    }
                }
            }

        }

        // make illegal sections have illegalWeight
        for (int i = 0; i < studentsArray.length; i++) {
            ArrayList<String> illegalSections = studentsArray[i].getIllegalSections();
            for (int j = 0; j < illegalSections.size(); j++) {
                if (!illegalSections.get(j).equals("")) { // max is suspicious
                    int[] indices = seatMap.getIndices(illegalSections.get(j));
                    for (int k = 0; k < indices.length; k++) {
                        cost[i][indices[k]] = illegalWeight;
                    }
                }
            }
        }

        // Go through the dummy students and set the gendered seats to illegalWeight
        for (int i = studentsArray.length; i < numSeats; i++) {
            for (int j = 0; j < sections.size(); j++) {
                currentSection = sections.get(j);
                // Get the indices in the cost matrix corresponding to the section of current preference
                int[] indices = seatMap.getIndices(currentSection);
                //set numMale and numFemale
                numFemale = (int) (currentSection.getCap() * ratioMin + .5) - currentSection.getNumFemaleStudents();
                numMale = (int) (currentSection.getCap() * ratioMin + .5) - currentSection.getNumMaleStudents();
                for (int k = 0; k < indices.length; k++) {
                    if (k < numMale + numFemale) {
                        cost[i][indices[k]] = illegalWeight;
                    }
                }
            }
        }

        return cost;
    }

    /**
     * Weight function takes alpha constant and j variable. Likely will have to change later in order to optimize
     * preferences, gender, and other factors.
     */
    private double weightFunction(double alpha, int j) {
        return Math.pow(alpha, j + 1);
    }

    public String prettifyMatrix(double[][] mat) {
        String s = "";
        for (double[] row : mat) {
            for (double element : row) {
                s += (int) element + "\t\t";
            }
            s += "\n";
        }
        return s;
    }

    public String prettifyMatrix(int[] mat) {
        String s = "";
        for (double element : mat) {
            s += element + "\t\t";
        }
        s += "\n";
        return s;
    }

    public double[][] getCost() {
        return cost;
    }

    public ArrayList<Section> getSections() {
        return sections;
    }

    public ArrayList<Student> getStudents() {
        return students;
    }

    public ArrayList<Student> getAssigned() {
        return assigned;
    }

    public int getNumSeats() {
        return numSeats;
    }

    /**
     * Main static boy
     *
     * @param args
     */
    public static void main(String[] args) {
        SortingHat sortingHat = new SortingHat(new File("csvparsetestSECT.csv"), new File("csvparsetestSTUD.csv"));
        System.out.println("The Cost Array is:");
        System.out.println(sortingHat.prettifyMatrix(sortingHat.buildCostMatrix()));
        System.out.println("The assignment array is:");
        System.out.println("The Cost Array is:");
    }

}

