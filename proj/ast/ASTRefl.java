package proj.ast;

import proj.values.*;
import proj.types.*;
import proj.env.*;
import proj.env.EnvSet.ENV;
import proj.errors.*;

public class ASTRefl extends ASTNode  {

    public ASTRefl() {}

    public IValue eval(Env<IValue> e) {
        return new VRefl();                
    }

    public ASTType typecheck(EnvSet e) throws TypeCheckError, EnvironmentError {
        throw new TypeCheckError(ErrorMessages.missingExpectedType(this));
	}

    public ASTType typecheck(EnvSet e, ASTType t) throws TypeCheckError, EnvironmentError {
        if (!(t instanceof ASTTEq tt))
            throw new TypeCheckError(ErrorMessages.illegalTypeToUnary("refl", t));

        Env<ASTType> sigma = e.getEnv(ENV.SIGMA);
        ASTNode left = tt.getTerm1(), right = tt.getTerm2();
        ASTNode ln = left.normalize(sigma);
        ASTNode rn = right.normalize(sigma);
        if (ln.defequals(rn, sigma, new AlphaEnv())) return t;
        throw new TypeCheckError(ErrorMessages.termsNotDefeq(left, right));
    }

    public boolean defequals(ASTNode o, Env<ASTType> sigma, AlphaEnv alpha) {
        return o instanceof ASTRefl;
    }

    @Override
    public String toString() {
        return "refl";
    }
}
