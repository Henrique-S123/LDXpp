package proj.ast;

import proj.values.*;
import proj.types.*;
import proj.defeq.TermClosure;
import proj.env.*;
import proj.errors.*;

public abstract class ASTNode {

    public IValue eval(Env<IValue> e) throws InterpreterError {
        return new VUnit();
    }
	
    public ASTType typecheck(EnvSet e) throws TypeCheckError, EnvironmentError {
        throw new TypeCheckError("Undefined method.");
    }

    public ASTType typecheck(EnvSet e, ASTType target) throws TypeCheckError, EnvironmentError {
        return typecheck(e);
    }

    public ASTType puretypecheck(Env<ASTType> sigma, Env<ASTType> phi, ASTType target) throws TypeCheckError, EnvironmentError {
        return target;
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
        Env<ASTType> sig = sigma;
        while (true) {
            ln = ln.weaknorm();
            TermClosure sln = ln.solve(sig);
            if (sln == null) return ln;
            ln = sln.term();
            sig = sln.env();
        }
    }
}

