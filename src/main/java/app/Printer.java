package app;

public final class Printer {
    public static final String start = "Please select one of selections:\n";
    public static final String end = "Your selection:\t";
    public static final String menu = Color.CYAN.asString + """
            If you want to go back write "-1" in text fields.""" + Color.PURPLE.asString + """
            
            You need to exit form main menu if you want to Save unsaved data.""" + Color.BLUE.asString + """
            
            \t1. CRUD Operations.
            \t2. Search operations.""" + Color.YELLOW.asString + """
                        
            \t0. Exit.
            """ + Color.RESET.asString;
    public static final String crud = Color.BLUE.asString + """
            \t1. Create new student.
            \t2. Select a student or students.
            \t3. Update existing student.
            \t4. Delete a student.""" + Color.YELLOW.asString + """
                        
            \t0. Back.
            """ + Color.RESET.asString;

    public static final String selectStudent = Color.BLUE.asString + """
            \t1. Select all students.
            \t2. Select a student.""" + Color.YELLOW.asString + """
                        
            \t0. Back.
            """ + Color.RESET.asString;
    public static final String returningBack = Color.CYAN.asString + "Returning back..." + Color.RESET.asString;
    public static final String returningMainMenu = Color.CYAN.asString + "Returning main menu..." + Color.RESET.asString;
    public static final String exiting = Color.CYAN.asString + "Exiting..." + Color.RESET.asString;
    public static final String searchOptions = Color.BLUE.asString + """
            \t\t SEARCH BY:
            \t1. Name.
            \t2. Last name.
            \t3. Father name.""" + Color.YELLOW.asString + """
                        
            \t0. Back.
            """ + Color.RESET.asString;

    public static <T> String colorString(Color color, T word) {
        return color.asString + word + Color.RESET.asString;
    }

    public static void print(String string) {
        System.out.print(string);
    }

    public static void println() {
        System.out.println();
    }

    public static <T> void println(T value) {
        System.out.println(value);
    }

    public static void printMenu(String menu) {
        System.out.print(start + menu);
    }

    public static void printError(String error) {
        System.out.println(Color.RED.asString + error + Color.RESET.asString);
    }


    public static void printBack() {
        println(returningBack);
    }
}