package proj.errors;

import proj.values.*;
import proj.types.*;
import proj.env.*;
import proj.ast.*;

public final class ErrorMessages {
    private ErrorMessages() {}

    // Environment errors
    public static String undefinedVariable(String name) {
        return "Undefined variable: " + name;
    }

    // Evaluation errors
    public static String wrongValueToUnary(String op, IValue v) {
        return String.format("Illegal value to %s: %s", op, v);
    }

    public static String wrongValueToBinary(String op, IValue v, IValue v2) {
        return String.format("Illegal values to %s: %s and %s", op, v, v2);
    }

    public static String unexpectedOperation(String op) {
        return String.format("Unexpected operation %s", op);
    }

    public static String missingMatchCase(String label) {
        return String.format("Missing match case for label %s", label);
    }

    // Typechecking errors
    public static String illegalTypeToUnary(String op, ASTType t) {
        return String.format("Illegal type to %s: %s", op, t);
    }

    public static String illegalTypeToBinary(String op, ASTType t1, ASTType t2) {
        return String.format("Illegal types to %s: %s and %s", op, t1, t2);
    }

    public static String typeMismatch(String t1, ASTType t2) {
        return String.format("Type mismatch: expected %s type but got %s", t1, t2);
    }

    public static String differentTypes(ASTNode n1, ASTNode n2, ASTType t1, ASTType t2) {
        return String.format("Terms %s and %s have different types: %s and %s", n1, n2, t1, t2);
    }

    public static String termsNotDefeq(ASTNode n1, ASTNode n2) {
        return String.format("Terms %s and %s are not definitionally equal", n1, n2);
    }

    public static String typesNotDefeq(ASTType t1, ASTType t2) {
        return String.format("Types %s and %s are not definitionally equal", t1, t2);
    }

    public static String notSubtypeApp(ASTType arg, ASTType param) {
        return String.format("Argument type %s is not a subtype of parameter type %s", arg, param);
    }

    public static String notSubtypeFunc(ASTType dom, ASTType param) {
        return String.format("Domain type %s is not a subtype of parameter type %s", dom, param);
    }

    public static String notSubtype(ASTType t1, ASTType t2) {
        return String.format("Type %s is not a subtype of %s", t1, t2);
    }

    public static String unusedLinearValues(Env<ASTType> env) {
        return String.format("There are unused linear values: %s", env);
    }

    public static String missingExpectedType(ASTNode n) {
        return String.format("%s expects a type to be typed against", n);
    }
}
