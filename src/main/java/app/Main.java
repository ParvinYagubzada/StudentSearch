package app;

import util.SearchType;
import util.Tree;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import java.io.IOException;
import java.nio.file.Path;
import java.util.*;
import java.util.regex.Pattern;

import static app.Printer.*;

public class Main {
    private static final Scanner scanner = new Scanner(System.in);
    public static SortedMap<Long, Student> students;

    static {
        students = new TreeMap<>();
    }
    //Reads data from JSON.
    //Sets next Student ID for insert operation.
    //Starts application.
    public static void main(String[] args) throws IOException {
        readJSON();
        Generator.setID(101000);
        startApp();
    }

    //Prints main menu. Asks for user selection. Application runs until user wants to exit. (Option 0)
    public static void startApp() throws IOException {
        int userInput = -1;
        while (userInput != 0) {
            printMenu(menu);
            userInput = getSelection(2);
            switch (userInput) {
                case 0 -> {
                    println(exiting);
                    writeJSON();
                }
                case 1 -> operationCRUD();
                case 2 -> operationSearch();
            }
        }
    }

    //Starts crud options. Prints crud menu asks user selection.
    //IF any selection throw Exception (BackToMenu) this try-catch block catches it and returns main menu.
    public static void operationCRUD() {
        printMenu(crud);
        try {
            switch (getSelection(4)) {
                case 0, -1:
                    break;
                case 1:
                    Student newStudent = getNewStudentFromUser();
                    println("Student " + colorString(Color.PURPLE, newStudent.getName()) + " added to DB!");
                    students.put(newStudent.getId(), newStudent);
                    break;
                case 2:
                    printMenu(selectStudent);
                    switch (getSelection(2)) {
                        case 1 -> students.values().forEach(System.out::println);
                        case 2 -> {
                            Student student = getStudentById(getId());
                            if (student != null)
                                System.out.println(student);
                        }
                    }
                    break;
                case 3:
                    Student student = getStudentById(getId());
                    if (student != null) {
                        if (updateStudent(student))
                            println("Student " + colorString(Color.PURPLE, student.getName()) + " updated on DB!");
                        else
                            printError("Update failed!");
                    } else {
                        println(returningBack);
                    }
                    break;
                case 4:
                    deleteStudent(getId());
                    break;
            }
        } catch (BackToMenu ignored) {}
        println(returningMainMenu);
    }
    //Starts search options. Prints search menu asks user selection.
    //IF any selection throw Exception (BackToMenu) this try-catch block catches it and returns main menu.
    public static void operationSearch() {
        print(searchOptions);
        try {
            switch (getSelection(3)) {
                case 0, -1:
                    break;
                case 1:
                    search(getSearchString("name"), Comparator.comparing(Student::getName), SearchType.NAME);
                    break;
                case 2:
                    search(getSearchString("surname"), Comparator.comparing(Student::getSurname), SearchType.SURNAME);
                    break;
                case 3:
                    search(getSearchString("father name"), Comparator.comparing(Student::getFatherName), SearchType.FATHER);
                    break;
            }
        } catch (BackToMenu ignored) {}
        println(returningMainMenu);
    }

    /**
     * Gets selections for switches.
     * Continues until user inserts "-1" or valid selection.
     *
     * @param limit limits selection from 0 (inclusive) to limit (inclusive).
     * @return integer selection which will be used in switches.
     **/
    private static int getSelection(int limit) {
        int selection = Integer.MIN_VALUE;
        do {
            try {
                if (selection != Integer.MIN_VALUE)
                    printError("Please select only number 0 to " + limit);
                print(end);
                selection = scanner.nextInt();
            } catch (InputMismatchException exception) {
                scanner.nextLine();
                selection = -2;
            }
        } while (selection < -1 || selection > limit);
        if (selection == -1)
            printBack();
        return selection;
    }

    /**
     * Gets name, last name or father name for searching.
     * Continues until user inserts "-1" or valid name.
     *
     * @param name limits selection from 0 (inclusive) to limit (inclusive).
     * @return string valid name, last name or father name.
     * @throws BackToMenu if user inserts "-1" as input.
     **/
    private static String getSearchString(String name) throws IllegalArgumentException {
        Pattern pattern = Pattern.compile("[A-Za-z]+");
        String word = null;
        do {
            if (word != null)
                printError("Search word can only have alphabetic characters. Min length is 1 character.");
            print("Please enter the " + colorString(Color.PURPLE, name) + " that you want to search: ");
            word = scanner.next();
            if (word.equals("-1"))
                throw new BackToMenu();
        } while (!pattern.matcher(word).matches());
        return toCapitalize(word);
    }

    /**
     * Gets id for selecting, updating or deleting Student.
     * Continues until user inserts "-1" or valid name.
     *
     * @return integer selection which will be used in switches.
     **/
    private static long getId() {
        long id = 1;
        do {
            try {
                if (id == -2)
                    printError("Please write integer number.");
                print("Please enter student id: ");
                id = scanner.nextLong();
            } catch (InputMismatchException exception) {
                scanner.nextLine();
                id = -2;
            }
        } while (id < -1);
        if (id == -1)
            printBack();
        return id;
    }

    /**
     * Searches and finds Students which name, last name or father name starts with given string.
     *
     * @param search given string by user.
     * @param comparator for inserting Students to tree.
     * @param type search type which indicates how search operated on Students.
     **/
    private static void search(String search, Comparator<Student> comparator, SearchType type) {
        Tree<Student> tree = new Tree<>(comparator);
        for (Student student : students.values())
            tree.insert(student);
        tree.search(search, type);
    }

    /**
     * Finds Student in Map based on his/her id.
     * If Student does not exists in Map then returns null.
     *
     * @param id given Student id.
     * @return Student if it is exist, null if it is not.
     * @throws BackToMenu if user inserts "-1" for id.
     **/
    private static Student getStudentById(long id) {
        if (students.containsKey(id)) {
            return students.get(id);
        } else if (id != -1) {
            printError("This student doesn't exists.");
            return null;
        }
        throw new BackToMenu();
    }

    /**
     * Updates Student in Map.
     * Continues until user selects exit (0) option.
     * Or user selects -1 for returning main menu.
     * Or until user inserts "no" after updating 1 field of Student object.
     *
     * @param student given string by user.
     * @return boolean true if any field of Student object has been updated, false if update process fails.
     **/
    private static boolean updateStudent(Student student) {
        boolean finished = true;
        boolean updated = false;
        do {
            printMenu(update);
            int selection = getSelection(5);
            switch (selection) {
                case 0, -1:
                    return updated;
                case 1:
                    student.setName(getName("name"));
                    finished = askFinished();
                    updated = true;
                    break;
                case 2:
                    student.setSurname(getName("last name"));
                    finished = askFinished();
                    updated = true;
                    break;
                case 3:
                    student.setFatherName(getName("father name"));
                    finished = askFinished();
                    updated = true;
                    break;
                case 4:
                    student.setEmail(getEmail());
                    finished = askFinished();
                    updated = true;
                    break;
                case 5:
                    student.setPhoneNumber(getNumber());
                    finished = askFinished();
                    updated = true;
                    break;
            }
        } while (!finished);
        return true;
    }

    /**
     * Asks user to insert "yes" or "no" inputs.
     * This indicates that if user wants to continue editing or wants to stop and return main menu.
     * Continues until user inserts "-1" or valid input.
     *
     * @return boolean true if any user inserts valid "yes", false if inserts valid "no".
     * @throws BackToMenu if user inserts "-1" as input.
     **/
    private static boolean askFinished() {
        Pattern pattern = Pattern.compile("(?i)([yn]|(?i)(yes|no))");
        do {
            print("Do you want to continue editing? Yes/No: ");
            String ans = scanner.next();
            if (pattern.matcher(ans).matches()) {
                return !ans.equalsIgnoreCase("yes");
            } else if (ans.equals("-1")) {
                throw new BackToMenu();
            }
            printError("Enter \"Yes\" or \"No\" to continue.");
        } while (true);
    }

    /**
     * Gets name, last name or father name for updating or creating new user.
     * Continues until user inserts "-1" or valid name.
     *
     * @param specification type of field that you want user to insert.
     * @return string valid name, last name or father name.
     * @throws BackToMenu if user inserts "-1" as input.
     **/
    private static String getName(String specification) {
        while (true) {
            print("Please enter " + specification + ": ");
            String str = scanner.next();
            if (str.equals("-1"))
                throw new BackToMenu();
            if (Student.checkName(str))
                return str;
            printError("Your input was incorrect! Please enter again. (Words should be Capitalized)");
        }
    }

    /**
     * Gets Student phone number for updating or creating new user.
     * Continues until user inserts "-1" or valid phone number.
     *
     * @return string valid phone number.
     * @throws BackToMenu if user inserts "-1" as input.
     **/
    private static String getNumber() {
        while (true) {
            print("Please enter number: ");
            String str = scanner.next();
            if (str.equals("-1"))
                throw new BackToMenu();
            if (Student.checkNumber(str))
                return str;
            printError("Your input was incorrect! Number format should be like this: +994YYXXXXXXX. Please enter again.");
        }
    }

    /**
     * Gets Student email for updating or creating new user.
     * Continues until user inserts "-1" or valid email.
     *
     * @return string valid email.
     * @throws BackToMenu if user inserts "-1" as input.
     **/
    private static String getEmail() {
        while (true) {
            print("Please enter email: ");
            String str = scanner.next();
            if (str.equals("-1"))
                throw new BackToMenu();
            if (Student.checkEmail(str))
                return str;
            printError("Your input was incorrect! Please enter again.");
        }
    }

    /**
     * Removes Student if student exists.
     *
     * @param id type of field that you want user to insert.
     **/
    private static void deleteStudent(long id) {
        Student student = getStudentById(id);
        if (student != null) {
            students.remove(student.getId());
            println("Student " + colorString(Color.PURPLE, student.getName()) + " removed!");
        }
    }

    //Asks new user data for creating new Student object.
    private static Student getNewStudentFromUser() {
        String name = getName("name");
        String surname = getName("last name");
        String father = getName("father name");
        String number = getNumber();
        String email = getEmail();
        return new Student(name, surname, father, email, number);
    }

    //Writes data to JSON file.
    public static void writeJSON() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        ObjectWriter writer = mapper.writerWithDefaultPrettyPrinter();
        writer.writeValue(Path.of("data.json").toFile(), students);
    }

    //Reads data from JSON file.
    public static void readJSON() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        TypeReference<SortedMap<Long, Student>> reference = new TypeReference<>() {};
        students = mapper.readValue(Path.of("data.json").toFile(), reference);
    }

    //Exception for escaping input fields to main menu.
    static class BackToMenu extends RuntimeException {
        public BackToMenu() {
            println(returningBack);
        }
    }
    //Capitalizes given string
    public static String toCapitalize(String string) {
        return string.substring(0, 1).toUpperCase() + string.substring(1).toLowerCase();
    }
}

