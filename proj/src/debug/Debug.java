package proj.src.debug;

public class Debug {
    static int depth = 0;

    public static boolean ENABLED = Boolean.getBoolean("debug");

    public static boolean state = ENABLED;

    public static void off() { if (ENABLED) state = false; }

    public static void on() { if (ENABLED) state = true; }

    public static void open() { depth += 1; }

    public static void close() { depth -= 1; }

    public static void log(String message) { if (state) System.out.println("  ".repeat(depth) + "[DEBUG] " + message); }

    public static void nl() { if (state) System.out.println(); }
}