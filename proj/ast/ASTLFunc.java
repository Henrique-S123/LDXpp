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
            throw new TypeCheckError("there are unused linear values: " + e.getEnv(ENV.DELTA).toString());
        e.closeEnvScope(env);
        return new ASTTLollipop(targtype, tb, id);
	}

    public ASTType typecheck(EnvSet e, ASTType t) throws TypeCheckError, EnvironmentError {
        ASTType tt = e.unfold(t);
        ASTType tdom, tcodom;
        String tid;

        if (tt instanceof ASTTLollipop lolli) { tdom = lolli.getCodom(); tcodom = lolli.getCodom(); tid = lolli.getId(); }
        else throw new TypeCheckError("linear func: expected lollipop type");

        ASTType targtype = e.unfold(argtype);
        ENV env = (targtype instanceof ASTLinType) ? ENV.DELTA : ENV.GAMMA;
        e.openEnvScope(ENV.SIGMA);
        e.openEnvScope(env);

        if (!tdom.isSubtypeOf(targtype, e))
            throw new TypeCheckError(String.format("func: dom type %s is not subtype of arg type %s", tdom.toString(), targtype.toString()));

        e.bindToEnv(env, id, targtype);
        e.bindToEnv(ENV.SIGMA, id, targtype);

        ASTType tb = body.typecheck(e, tcodom);

        e.closeEnvScope(env);
        e.closeEnvScope(ENV.SIGMA);
        return new ASTTLollipop(targtype, tb, id);
    }

    public ASTNode normalize(Environment<ASTType> sigma) {
        return new ASTLFunc(id, body.normalize(sigma), argtype);
    }

    public boolean defequals(ASTNode o, Environment<ASTType> sigma) {
        return o instanceof ASTLFunc olfunc && olfunc.getId().equals(id)
            && olfunc.getBody().defequals(body, sigma) && olfunc.getArgtype().defequals(argtype, sigma);
    }

    @Override
    public String toString() {
        return String.format("lfn %s:%s => {%s}", id, argtype.toString(), body.toString());
	}
}
