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
        } catch (BackToMenu ignored) {}
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
        } catch (BackToMenu ignored) {}
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
        Student newUser = getNewStudentFromUser();
        if (newUser != null) {
            copyData(student, newUser);
            return true;
        }
        return false;
    }

    private static void copyData(Student student, Student dummy) {
        if (dummy != null) {
            student.setName(dummy.getName());
            student.setSurname(dummy.getSurname());
            student.setFatherName(dummy.getFatherName());
            student.setEmail(dummy.getEmail());
            student.setPhoneNumber(dummy.getPhoneNumber());
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
        Student student = new Student();
        do {
            try {
                print("Name: ");
                String name = scanner.next();
                if (checkExit(name))
                    break;
                student.setName(name);
                print("Surname: ");
                String surname = scanner.next();
                if (checkExit(surname))
                    break;
                student.setSurname(surname);
                print("Father name: ");
                String father = scanner.next();
                if (checkExit(father))
                    break;
                student.setFatherName(father);
                print("Email: ");
                String email = scanner.next();
                if (checkExit(email))
                    break;
                student.setEmail(email);
                print("Phone number: +994");
                String phone = scanner.next();
                if (checkExit(phone))
                    break;
                student.setPhoneNumber("+994" + phone);
                return student;
            } catch (Exception exception) {
                printError(exception.getMessage());
            }
        } while (true);
        return null;
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
        TypeReference<SortedMap<Long, Student>> reference = new TypeReference<>() {};
        students = mapper.readValue(Path.of("data.json").toFile(), reference);
    }

    static class BackToMenu extends RuntimeException {
        public BackToMenu() {
            println(returningBack);
        }
    }
}

