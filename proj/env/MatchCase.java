package proj.env;

import proj.ast.*;

public class MatchCase {
    private final String id;
    private final ASTNode exp;

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
}
