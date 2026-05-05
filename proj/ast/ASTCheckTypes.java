package proj.ast;

import proj.values.*;
import proj.types.*;
import proj.env.*;
import proj.env.EnvSet.ENV;
import proj.errors.*;

public class ASTCheckTypes implements ASTNode {
    ASTType left, right;

    public ASTCheckTypes(ASTType l, ASTType r) {
        left = l;
        right = r;
    }
    
    public IValue eval(Env<IValue> e) throws InterpreterError {
        return new VBool(true, false);
    }

    public ASTType typecheck(EnvSet e) throws TypeCheckError, EnvironmentError {
        if (left.defequals(right, e.getEnv(ENV.SIGMA), new Env<ASTNode>(), new Env<ASTNode>())) return new ASTTUnit();
        throw new TypeCheckError(String.format("types %s and %s are not definitionally equal", left, right));
    }

    public ASTType typecheck(EnvSet e, ASTType t) throws TypeCheckError, EnvironmentError {
        return typecheck(e);
    }

    public ASTNode normalize(Env<ASTType> sigma, Env<ASTNode> sub) {
        return this;
    }

    public boolean defequals(ASTNode o, Env<ASTType> sigma) {
        return false;
    }

    @Override
    public String toString() {
        return String.format("%s ?T %s", left, right);
    }
}
