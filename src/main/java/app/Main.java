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

    public static void main(String[] args) throws IOException {
        readJSON();
        Generator.setID(101000);
        startApp();
    }

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

    public static void operationCRUD() {
        printMenu(crud);
        try {
            switch (getSelection(4)) {
                case 0, -1:
                    break;
                case 1:
                    Student newStudent = getNewStudentFromUser();
                    if (newStudent != null) {
                        println("Student " + colorString(Color.PURPLE, newStudent.getName()) + " added to DB!");
                        students.put(newStudent.getId(), newStudent);
                    } else {
                        println(returningBack);
                    }
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
        } catch (BackToMenu ignored) {
        }
        println(returningMainMenu);
    }

    public static void operationSearch() {
        print(searchOptions);
        try {
            switch (getSelection(3)) {
                case 0, -1:
                    break;
                case 1:
                    search(getString("name"), Comparator.comparing(Student::getName), SearchType.NAME);
                    break;
                case 2:
                    search(getString("surname"), Comparator.comparing(Student::getSurname), SearchType.SURNAME);
                    break;
                case 3:
                    search(getString("father name"), Comparator.comparing(Student::getFatherName), SearchType.FATHER);
                    break;
            }
        } catch (BackToMenu ignored) {
        }
        println(returningMainMenu);
    }

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

    private static String getString(String name) throws IllegalArgumentException {
        Pattern pattern = Pattern.compile("^\\p{Upper}[a-z]*");
        String word = null;
        do {
            if (word != null)
                printError("Search word's first letter has to be Upper case character and others needs to be Lower case!" +
                        " It can have min 1 character. Example: Parvin");
            print("Please enter the " + colorString(Color.PURPLE, name) + " that you want to search: ");
            word = scanner.next();
            if (word.equals("-1"))
                throw new BackToMenu();
        } while (!pattern.matcher(word).matches());
        return word;
    }

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

    private static void search(String search, Comparator<Student> comparator, SearchType type) {
        Tree<Student> tree = new Tree<>(comparator);
        for (Student student : students.values())
            tree.insert(student);
        tree.search(search, type);
    }

    private static Student getStudentById(long id) {
        if (students.containsKey(id)) {
            return students.get(id);
        } else if (id != -1) {
            printError("This student doesn't exists.");
            return null;
        }
        throw new BackToMenu();
    }

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

    private static void deleteStudent(long id) {
        Student student = getStudentById(id);
        if (student != null) {
            students.remove(student.getId());
            println("Student " + colorString(Color.PURPLE, student.getName()) + " removed!");
        }
    }

    private static Student getNewStudentFromUser() {
        String name = getName("name");
        String surname = getName("last name");
        String father = getName("father name");
        String number = getNumber();
        String email = getEmail();
        return new Student(name, surname, father, email, number);
    }

    private static boolean checkExit(String str) {
        return str.equals("-1");
    }

    public static void writeJSON() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        ObjectWriter writer = mapper.writerWithDefaultPrettyPrinter();
        writer.writeValue(Path.of("data.json").toFile(), students);
    }

    public static void readJSON() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        TypeReference<SortedMap<Long, Student>> reference = new TypeReference<>() {
        };
        students = mapper.readValue(Path.of("data.json").toFile(), reference);
    }

    static class BackToMenu extends RuntimeException {
        public BackToMenu() {
            println(returningBack);
        }
    }
}

