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

    public ASTNode solve(Env<ASTType> sigma) {
        return null;
    }

    public ASTNode subs(String subsId, ASTNode node) {
        return this;
    }

    public boolean defequals(ASTNode o, Env<ASTType> sigma, AlphaEnv alpha) {
        return false;
    }

    public ASTNode normalize(Env<ASTType> sigma) {
        ASTNode ln = this;
        while (true) {
            Env<ASTType> sig = sigma;
            if (ln instanceof ASTApp a) {
                if (a.getFunc() instanceof ASTFunc f) sig = f.getNormSigma();
                else if (a.getFunc() instanceof ASTLFunc lf) sig = lf.getNormSigma();
            }
            ln = ln.weaknorm(new Env<ASTNode>());
            ASTNode sln = ln.solve(sig);
            if (sln == null) return ln;
            ln = sln;
        }
    }
}

