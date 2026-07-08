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

    /* Adding new equivalences */
    public AlphaEnv extend(String id1, String id2) {
        left = left.beginScope();
        right = right.beginScope();
        String newid = UUID.randomUUID().toString();
        left.assoc(id1, newid);
        right.assoc(id2, newid);
        return this;
    }

    /* Testing equivalences */
    public boolean equiv(String id1, String id2) {
        String s1 = left.find(id1);
        String s2 = right.find(id2);
        return (s1 != null && s1.equals(s2));
    }

    public String toString() {
        return "left: " + left + "; right: " + right;
    }
}
