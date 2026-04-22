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

    public String getId() {
        return id;
    }

    public ASTNode getBody() {
        return body;
    }

    public ASTType getArgtype() {
        return argtype;
    }

    public void setBody(ASTNode b) {
        body = b;
    }

    public IValue eval(Environment<IValue> e) throws InterpreterError {
        return new VClos(e, id, body, true);
    }

    public ASTType typecheck(EnvSet e) throws TypeCheckError, EnvironmentError {
        ASTType targtype = e.unfold(argtype);
        ENV env = (targtype instanceof ASTLinType) ? ENV.DELTA : ENV.GAMMA;
        e.openEnvScope(env);
        e.bindToEnv(env, id, targtype);
        ASTType tb = body.typecheck(e);
        if (!(e.getEnv(ENV.DELTA).isEmpty()))
            throw new TypeCheckError("there are unused linear values: " + e.getEnv(ENV.DELTA).toStr());
        e.closeEnvScope(env);
        return new ASTTLollipop(targtype, tb);
	}

    public ASTType typecheck(EnvSet e, ASTType t) throws TypeCheckError, EnvironmentError {
        // TODO
        return typecheck(e);
    }

    public ASTNode normalize(Environment<ASTType> sigma) {
        return new ASTLFunc(id, body.normalize(sigma), argtype);
    }

    public boolean defequals(ASTNode o, Environment<ASTType> sigma) {
        return o instanceof ASTLFunc && ((ASTLFunc) o).getId().equals(id)
            && ((ASTLFunc) o).getBody().defequals(body, sigma) && ((ASTLFunc) o).getArgtype().defequals(argtype, sigma);
    }
}
