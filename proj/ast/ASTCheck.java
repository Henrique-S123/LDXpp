package proj.ast;

import proj.values.*;
import proj.types.*;
import proj.env.*;
import proj.env.EnvSet.ENV;
import proj.errors.*;

public class ASTCheck extends ASTNode {
    ASTNode left, right;

    public ASTCheck(ASTNode l, ASTNode r) {
        left = l;
        right = r;
    }
    
    public IValue eval(Env<IValue> e) throws InterpreterError {
        return new VBool(true, false);
    }

    public ASTType typecheck(EnvSet e) throws TypeCheckError, EnvironmentError {
        ASTType t = left.typecheck(e);
        ASTType t2 = right.typecheck(e);
        if (!t.isSubtypeOf(t2, e) || !t2.isSubtypeOf(t, e))
            throw new TypeCheckError(String.format("terms %s and %s do not have the same type", left, right));

        Env<ASTType> sigma = e.getEnv(ENV.SIGMA);
        ASTNode ln = left.normalize(sigma, new Env<ASTNode>());
        ASTNode rn = right.normalize(sigma, new Env<ASTNode>());
        if (ln.defequals(rn, sigma, new AlphaEnv())) return new ASTTEq(left, right, t);
        throw new TypeCheckError(String.format("terms %s and %s are not definitionally equal", left, right));
    }

    @Override
    public String toString() {
        return String.format("%s ? %s", left, right);
    }
}
