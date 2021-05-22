package app;

class Generator {
    private static long id = 100000;

    public static long getID() {
        return id++;
    }

    public static void setID(long id) {
        Generator.id = id;
    }
}
