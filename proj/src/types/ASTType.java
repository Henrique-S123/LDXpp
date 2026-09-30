package proj.src.types;

import proj.src.env.*;
import proj.src.errors.*;
import proj.src.ast.ASTNode;
import proj.src.defeq.IdPair;

import java.util.HashSet;
import java.util.Set;

public class ASTType  {
    protected boolean lin;

    public boolean isLinear() {
        return lin;
    }

    public boolean isSubtypeOf(ASTType o, PureEnvSet pe) {
        return isSubtypeOf(o, pe, new AlphaEnv(), new HashSet<>());
    }

    public boolean isSubtypeOf(ASTType o, PureEnvSet pe, AlphaEnv alpha, Set<IdPair> seen) {
        return false;
    }

    public ASTType inst(String instId, ASTNode n) {
        return this;
    }

    public ASTType check(PureEnvSet pe) throws TypeCheckError {
        return this;
    }

    public boolean structEq(ASTType o) {
        return false;
    }
}
