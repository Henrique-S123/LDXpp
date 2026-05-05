package proj.env;

import proj.ast.*;

import java.util.*;

public class AlphaEnv {
    Env<ASTNode> left, right;

    public AlphaEnv() {
        left = new Env<ASTNode>();
        right = new Env<ASTNode>();
    }

    /* Getters */
    public Env<ASTNode> getLeft() { return left; }
    public Env<ASTNode> getRight() { return right; }

    /* Adding new equivalences */
    public AlphaEnv extend(String id1, String id2) {
        left = left.beginScope();
        right = right.beginScope();
        ASTId newid = new ASTId(UUID.randomUUID().toString());
        left.assoc(id1, newid);
        right.assoc(id2, newid);
        return this;
    }

    public String toString() {
        return "left: " + left + "; right: " + right;
    }
}
