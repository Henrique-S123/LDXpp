package proj.debug;

public class Debug {
    public static final boolean ENABLED =
            Boolean.getBoolean("debug");

    public static void log(String message) {
        if (ENABLED) {
            System.out.println("[DEBUG] " + message);
        }
    }
}