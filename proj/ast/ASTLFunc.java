package proj.ast;

import proj.values.*;
import proj.types.*;
import proj.env.*;
import proj.env.EnvSet.ENV;
import proj.errors.*;

public class ASTLFunc implements ASTNode  {
    String id;
    ASTNode body;
    ASTType argtype;

    public ASTLFunc(String i, ASTNode b, ASTType t) {
        id = i;
        body = b;
        argtype = t;
    }

    public void setBody(ASTNode b) {
        body = b;
    }

    public IValue eval(Environment<IValue> e) throws InterpreterError {
        return new VClos(e, id, body, true);
    }

    public ASTType typecheck(EnvSet e) throws TypeCheckError, EnvironmentError {
        ASTType targtype = e.unfold(argtype);
        e.assocVar(id, targtype);
        ASTType tb = body.typecheck(e);
        if (!(e.getEnv(ENV.DELTA).isEmpty()))
            throw new TypeCheckError("there are unused linear values: " + e.getEnv(ENV.DELTA).toStr());
        ENV env = (targtype instanceof ASTLinType) ? ENV.DELTA : ENV.GAMMA;
        e.closeEnvScope(env);
        return new ASTTLollipop(targtype, tb);
	}

    public ASTNode normalize(Environment<ASTType> sigma) {
        return this;
    }

    public boolean defequals(ASTNode o) {
        // TODO
        return false;
    }
}
