package proj.ast;

import proj.values.*;
import proj.types.*;
import proj.env.*;
import proj.env.EnvSet.ENV;
import proj.errors.*;

public class ASTRefl implements ASTNode  {

    public ASTRefl() {}

    public IValue eval(Env<IValue> e) throws InterpreterError {
        return new VUnit();                
    }

    public ASTType typecheck(EnvSet e) throws TypeCheckError, EnvironmentError {
        return new ASTTEq(new ASTUnit(), new ASTUnit(), new ASTTUnit());
	}

    public ASTType typecheck(EnvSet e, ASTType t) throws TypeCheckError, EnvironmentError {
        if (!(t instanceof ASTTEq tt))
            throw new TypeCheckError("refl: expected equality type");

        Env<ASTType> sigma = e.getEnv(ENV.SIGMA);
        ASTNode ln = tt.getTerm1().normalize(sigma, new Env<ASTNode>());
        ASTNode rn = tt.getTerm2().normalize(sigma, new Env<ASTNode>());
        if (!(ln.defequals(rn, sigma)))
            throw new TypeCheckError(String.format("refl: terms %s and %s are not definitionally equal", tt.getTerm1(), tt.getTerm2()));

        return t;
    }

    public ASTNode normalize(Env<ASTType> sigma, Env<ASTNode> sub) {
        return this;
    }

    public boolean defequals(ASTNode o, Env<ASTType> sigma) {
        return o instanceof ASTRefl;
    }

    @Override
    public String toString() {
        return "refl";
    }
}
