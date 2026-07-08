package proj.env;

import java.util.*;

public class AlphaEnv {
    Env<String> left, right;

    public AlphaEnv() {
        left = new Env<String>();
        right = new Env<String>();
    }

    public AlphaEnv copy() {
        AlphaEnv newalpha = new AlphaEnv();
        newalpha.left = left.copy();
        newalpha.right = right.copy();
        return newalpha;
    }

    /* Getters */
    public Env<String> getLeft() { return left; }
    public Env<String> getRight() { return right; }

    /* Adding new equivalences */
    public AlphaEnv extend(String id1, String id2) {
        left = left.beginScope();
        right = right.beginScope();
        String newid = UUID.randomUUID().toString();
        left.assoc(id1, newid);
        right.assoc(id2, newid);
        return this;
    }

    public String toString() {
        return "left: " + left + "; right: " + right;
    }
}
