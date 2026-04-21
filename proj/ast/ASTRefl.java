package proj.ast;

import proj.values.*;
import proj.types.*;
import proj.env.*;
import proj.env.EnvSet.ENV;
import proj.errors.*;

public class ASTRefl implements ASTNode  {

    public ASTRefl() {}

    public IValue eval(Environment<IValue> e) throws InterpreterError {
        return new VUnit();                
    }

    public ASTType typecheck(EnvSet e) throws TypeCheckError, EnvironmentError {
        // TODO: refl should behave like nil in LP course lists
        return new ASTTUnit();
	}

    public ASTType typecheck(EnvSet e, ASTType t) throws TypeCheckError, EnvironmentError {
        if (!(t instanceof ASTTEq))
            throw new TypeCheckError("refl: expected equality type");
        ASTTEq tt = ((ASTTEq) t);

        if (!(tt.getTerm1().defequals(tt.getTerm2(), e.getEnv(ENV.SIGMA))))
            throw new TypeCheckError(String.format("refl: terms %s and %s are not definitionally equal", tt.getTerm1(), tt.getTerm2()));

        return t;
    }

    public ASTNode normalize(Environment<ASTType> sigma) {
        return this;
    }

    public boolean defequals(ASTNode o, Environment<ASTType> sigma) {
        // TODO: think
        return false;
    }

    @Override
    public String toString() {
        return "refl";
    }
}
