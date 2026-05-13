package proj.ast;

import proj.values.*;
import proj.types.*;
import proj.env.*;
import proj.env.EnvSet.ENV;
import proj.errors.*;

public class ASTCheckTypes extends ASTNode {
    ASTType left, right;

    public ASTCheckTypes(ASTType l, ASTType r) {
        left = l;
        right = r;
    }
    
    public IValue eval(Env<IValue> e) {
        return new VBool(true, false);
    }

    public ASTType typecheck(EnvSet e) throws TypeCheckError, EnvironmentError {
        if (left.defequals(right, e.getEnv(ENV.SIGMA), new AlphaEnv())) return new ASTTUnit();
        throw new TypeCheckError(ErrorMessages.typesNotDefeq(left, right));
    }

    @Override
    public String toString() {
        return String.format("%s ?T %s", left, right);
    }
}
