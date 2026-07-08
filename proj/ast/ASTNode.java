package proj.ast;

import proj.values.*;
import proj.types.*;
import proj.env.*;
import proj.errors.*;

public abstract class ASTNode {
    protected Env<ASTType> sig;

    public Env<ASTType> getSig() {
        return sig;
    }

    public void setSig(Env<ASTType> s) {
        sig = s;
    }

    public IValue eval(Env<IValue> e) throws InterpreterError {
        return new VUnit();
    }

    public ASTType typecheck(EnvSet e, ASTType target) throws TypeCheckError {
        throw new TypeCheckError("Undefined method.");
    }

    public ASTType puretypecheck(Env<ASTType> sigma, Env<ASTType> phi, ASTType target) throws TypeCheckError {
        return target;
    }

    public ASTNode weaknorm(Env<ASTNode> sub) {
        return this;
    }

    public ASTNode weaknorm() {
        return this.weaknorm(new Env<ASTNode>());
    }

    public ASTNode solve(Env<ASTType> sigma) {
        return null;
    }

    public ASTNode subs(String subsId, ASTNode node) {
        return this;
    }

    public ASTNode normalize(Env<ASTType> sigma) {
        ASTNode ln = this;
        Env<ASTType> sig = sigma;
        while (true) {
            ln = ln.weaknorm();
            ASTNode sln = ln.solve(sig);
            if (sln == null) return ln;
            ln = sln;
            sig = (sln.getSig() != null) ? sln.getSig() : sig;
        }
    }
}

