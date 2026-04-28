package proj.env;

import proj.ast.*;

public class MatchCase {
    String id;
    ASTNode exp;

    public MatchCase(String _id, ASTNode _exp) {
        id = _id;
        exp = _exp;
    }

    public String getId() {
        return id;
    }

    public ASTNode getExp() {
        return exp;
    }

    @Override
    public String toString() {
        return String.format("%s -> %s", id, exp.toString());
    }
}
