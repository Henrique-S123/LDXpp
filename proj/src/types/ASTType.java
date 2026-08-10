package proj.src.types;

import proj.src.env.*;
import proj.src.errors.*;
import proj.src.ast.ASTNode;

public class ASTType  {
    protected boolean lin;

    public boolean isLinear() {
        return lin;
    }

    public boolean isSubtypeOf(ASTType o, PureEnvSet pe) {
        return false;
    }

    public ASTType inst(String instId, ASTNode n) {
        return this;
    }

    public ASTType check(PureEnvSet pe) throws TypeCheckError {
        return this;
    }
}
