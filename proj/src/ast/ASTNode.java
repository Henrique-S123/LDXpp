package proj.src.ast;

import proj.src.values.*;
import proj.src.types.*;
import proj.src.env.*;
import proj.src.errors.*;

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

    public ASTType puretypecheck(PureEnvSet pe, ASTType target) throws TypeCheckError {
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
}

