package app;

import util.SearchType;

import java.security.InvalidParameterException;
import java.util.regex.Pattern;

import static app.Printer.*;

public class Student implements Comparable<Student> {
    private final long id;
    private String name;
    private String surname;
    private String fatherName;
    private String email;
    private String phoneNumber;

    public Student() {
        this.id = Generator.getID();
    }

    public Student(String name, String surname, String fatherName, String email, String phoneNumber) {
        this.id = Generator.getID();
        this.name = name;
        this.surname = surname;
        this.fatherName = fatherName;
        this.email = email;
        this.phoneNumber = phoneNumber;
    }

    @Override
    public String toString() {
        return "ID: %s | NAME: %20s | SURNAME: %22s | FATHER: %20s | EMAIL: %s | NUMBER: %s".formatted(
                colorString(Color.BLUE, this.id),
                colorString(Color.GREEN, this.name),
                colorString(Color.GREEN, this.surname),
                colorString(Color.GREEN,this.fatherName),
                this.email,
                this.phoneNumber
        );
    }

    public String get(SearchType type) {
        return switch (type) {
            case NAME -> this.name;
            case SURNAME -> this.surname;
            default -> this.fatherName;
        };
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        Pattern pattern = Pattern.compile("^\\p{Upper}[a-z]{2,}");
        if (pattern.matcher(name).matches()) {
            this.name = name;
        } else {
            throw new InvalidParameterException("Name's first letter has to be Upper case letter and other letters have to be Lower case." +
                    "\nIt can only have alphabetic characters. Min length is 3 characters.");
        }
    }

    public String getSurname() {
        return this.surname;
    }

    public void setSurname(String surname) {
        Pattern pattern = Pattern.compile("^\\p{Upper}[a-z]{2,}");
        if (pattern.matcher(surname).matches()) {
            this.surname = surname;
        } else {
            throw new InvalidParameterException("Surname's first letter has to be Upper case letter and other letters have to be Lower case." +
                    "\nIt can only have alphabetic characters. Min length is 5 characters.");
        }
    }

    public String getFatherName() {
        return fatherName;
    }

    public void setFatherName(String fatherName) {
        Pattern pattern = Pattern.compile("^\\p{Upper}[a-z]{2,}");
        if (pattern.matcher(fatherName).matches()) {
            this.fatherName = fatherName;
        } else {
            throw new InvalidParameterException("Father name's first letter has to be Upper case letter and other letters have to be Lower case." +
                    "\nIt can only have alphabetic characters. Min length is 3 characters.");
        }
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        Pattern pattern = Pattern.compile("^[\\p{Alnum}_.-]+@\\p{Alnum}+(\\.\\p{Alpha}{2,6}){1,4}$");
        if (pattern.matcher(email).matches()) {
            this.email = email;
        } else {
            throw new InvalidParameterException("Email format was invalid!");
        }
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        Pattern pattern = Pattern.compile("^\\+994(50|51|55|70|77|99)[1-9]\\d{6}$");
        if (pattern.matcher(phoneNumber).matches()) {
            this.phoneNumber = phoneNumber;
        } else {
            throw new InvalidParameterException("Phone number was incorrect. Format should be like +994XXXXXXXXX");
        }
    }


    @Override
    public int compareTo(Student second) {
        int result;
        if ((result = this.name.compareTo(second.name)) != 0)
            return result;
        if ((result = this.surname.compareTo(second.name)) != 0)
            return result;
        if ((result = this.fatherName.compareTo(second.name)) != 0)
            return result;
        return 0;
    }
}
