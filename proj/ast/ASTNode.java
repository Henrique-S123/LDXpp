package proj.ast;

import proj.values.*;
import proj.types.*;
import proj.env.*;
import proj.errors.*;

public abstract class ASTNode {

    public IValue eval(Env<IValue> e) throws InterpreterError {
        return new VUnit();
    }
	
    public ASTType typecheck(EnvSet e) throws TypeCheckError, EnvironmentError {
        return new ASTTUnit();
    }

    public ASTType typecheck(EnvSet e, ASTType t) throws TypeCheckError, EnvironmentError {
        return typecheck(e);
    }

    public ASTNode weaknorm(Env<ASTNode> sub) {
        return this;
    }

    public ASTNode weaknorm() {
        return this.weaknorm(new Env<ASTNode>());
    }

    public TermClosure solve(Env<ASTType> sigma) {
        return null;
    }

    public ASTNode subs(String subsId, ASTNode node) {
        return this;
    }

    public ASTNode normalize(Env<ASTType> sigma) {
        ASTNode ln = this;
        while (true) {
            Env<ASTType> sig = sigma;
            if (ln instanceof ASTApp a) {
                if (a.getFunc() instanceof ASTFunc f) sig = f.getSig();
                else if (a.getFunc() instanceof ASTLFunc lf) sig = lf.getSig();
            }
            if (ln instanceof ASTChoice c && c.getPair() instanceof ASTPair p) sig = p.getSig();
            if (ln instanceof ASTSplit s && s.getPair() instanceof ASTTensor t) sig = t.getSig();
            ln = ln.weaknorm();
            TermClosure sln = ln.solve(sig);
            if (sln == null) return ln;
            ln = sln.term();
        }
    }
}

