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
        return new ASTTEq(new ASTUnit(), new ASTUnit(), new ASTTUnit());
	}

    public ASTType typecheck(EnvSet e, ASTType t) throws TypeCheckError, EnvironmentError {
        if (!(t instanceof ASTTEq tt))
            throw new TypeCheckError("refl: expected equality type");

        Environment<ASTType> sigma = e.getEnv(ENV.SIGMA);
        if (!(tt.getTerm1().normalize(sigma).defequals(tt.getTerm2().normalize(sigma), sigma)))
            throw new TypeCheckError(String.format("refl: terms %s and %s are not definitionally equal", tt.getTerm1(), tt.getTerm2()));

        return t;
    }

    public ASTNode normalize(Environment<ASTType> sigma) {
        return this;
    }

    public boolean defequals(ASTNode o, Environment<ASTType> sigma) {
        return o instanceof ASTRefl;
    }

    @Override
    public String toString() {
        return "refl";
    }
}
