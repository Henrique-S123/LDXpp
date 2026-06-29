package proj.debug;

public class Debug {
    static int depth = 0;

    public static final boolean ENABLED = Boolean.getBoolean("debug");

    public static void open() { depth += 1; }

    public static void close() { depth -= 1; }

    public static void log(String message) { if (ENABLED) System.out.println("\t".repeat(depth) + "[DEBUG] " + message); }

    public static void nl() { if (ENABLED) System.out.println(); }
}