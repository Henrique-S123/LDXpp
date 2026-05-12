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

    public ASTNode weaknorm(Env<ASTType> sigma, Env<ASTNode> sub) {
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
}

