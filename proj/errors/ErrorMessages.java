package proj.errors;

import proj.values.*;

public final class ErrorMessages {
    private ErrorMessages() {}

    // Evaluation errors
    public static String wrongValueToUnary(String op, IValue v) {
        return String.format("Illegal value to %s: %s", op, v);
    }

    public static String wrongValueToBinary(String op, IValue v, IValue v2) {
        return String.format("Illegal value to %s: %s and %s", op, v, v2);
    }

    public static String missingMatchCase(String label) {
        return String.format("Missing match case for label %s", label);
    }

    public static String unexpectedOperation(String op) {
        return String.format("Unexpected operation %s", op);
    }

    public static String illegalTypeTo(String op, Object type) {
        return "Illegal type to " + op + ": " + type;
    }

    public static String undefinedVariable(String name) {
        return "Undefined variable: " + name;
    }

    public static String typeMismatch(Object expected, Object actual) {
        return "Type mismatch: expected " + expected + " but got " + actual;
    }

    public static String divisionByZero() {
        return "Division by zero";
    }
}
