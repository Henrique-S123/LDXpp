package proj.env;

import proj.ast.*;
import proj.types.*;

public class Bind {
    private final String id;
    private final ASTType type;
    private final ASTNode exp;

    public Bind(String _id, ASTType _type, ASTNode _exp) {
        id = _id;
        type = _type;
        exp = _exp;
    }

    public String getId() {
        return id;
    }

    public ASTType getType() {
        return type;
    }

    public ASTNode getExp() {
        return exp;
    }
}
