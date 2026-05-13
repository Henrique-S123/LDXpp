package proj.ast;

import proj.values.*;
import proj.types.*;
import proj.env.*;
import proj.env.EnvSet.ENV;
import proj.errors.*;

public class ASTRefl extends ASTNode  {

    public ASTRefl() {}

    public IValue eval(Env<IValue> e) throws InterpreterError {
        return new VRefl();                
    }

    public ASTType typecheck(EnvSet e) throws TypeCheckError, EnvironmentError {
        throw new TypeCheckError("refl: expected type to check against");
	}

    public ASTType typecheck(EnvSet e, ASTType t) throws TypeCheckError, EnvironmentError {
        if (!(t instanceof ASTTEq tt))
            throw new TypeCheckError("refl: expected equality type");

        Env<ASTType> sigma = e.getEnv(ENV.SIGMA);
        ASTNode left = tt.getTerm1(), right = tt.getTerm2();
        ASTNode ln = left.normalize(sigma, new Env<ASTNode>());
        ASTNode rn = right.normalize(sigma, new Env<ASTNode>());
        if (ln.defequals(rn, sigma, new AlphaEnv())) return t;
        throw new TypeCheckError(String.format("refl: terms %s and %s are not definitionally equal", left, right));
    }

    public boolean defequals(ASTNode o, Env<ASTType> sigma, AlphaEnv alpha) {
        return o instanceof ASTRefl;
    }

    @Override
    public String toString() {
        return "refl";
    }
}
