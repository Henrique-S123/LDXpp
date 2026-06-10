package proj.types;

import proj.env.*;
import proj.errors.*;
import proj.ast.ASTNode;

public class ASTType  {
    protected Env<ASTType> sig;

    public Env<ASTType> getSig() {
        return sig;
    }

    public void setSig(Env<ASTType> s) {
        sig = s;
    }

    public boolean isSubtypeOf(ASTType o, Env<ASTType> sigma, Env<ASTType> phi, AlphaEnv alpha) {
        return false;
    }

    public ASTType inst(String instId, ASTNode n) {
        return this;
    }

    public ASTType check(Env<ASTType> sigma, Env<ASTType> phi) throws TypeCheckError, EnvironmentError {
        return this;
    }
}
