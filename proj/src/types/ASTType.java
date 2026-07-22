package proj.src.types;

import proj.src.env.*;
import proj.src.errors.*;
import proj.src.ast.ASTNode;

public class ASTType  {
    protected Env<ASTType> sig;
    protected boolean lin;

    public Env<ASTType> getSig() {
        return sig;
    }

    public boolean isLinear() {
        return lin;
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

    public ASTType check(Env<ASTType> sigma, Env<ASTType> phi, AlphaEnv alpha) throws TypeCheckError {
        return this;
    }
}
