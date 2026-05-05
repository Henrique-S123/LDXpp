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
    Env<ASTNode> normEnv;
    Env<ASTType> normSigma;

    public ASTLFunc(String i, ASTNode b, ASTType t) {
        id = i;
        body = b;
        argtype = t;
        normEnv = null;
        normSigma = null;
    }

    public ASTLFunc(String i, ASTNode b, ASTType t, Env<ASTNode> sub, Env<ASTType> sigma) {
        id = i;
        body = b;
        argtype = t;
        normEnv = sub;
        normSigma = sigma;
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

    public Env<ASTNode> getNormEnv() {
        return normEnv;
    }

    public Env<ASTType> getNormSigma() {
        return normSigma;
    }

    public void setBody(ASTNode b) {
        body = b;
    }

    public void setNormSigma(Env<ASTType> s) {
        normSigma = s;
    }

    public IValue eval(Env<IValue> e) throws InterpreterError {
        return new VClos(e, id, body, true);
    }

    public ASTType typecheck(EnvSet e) throws TypeCheckError, EnvironmentError {
        ASTType targtype = e.unfold(argtype);
        ENV env = (targtype instanceof ASTLinType) ? ENV.DELTA : ENV.GAMMA;
        e.openEnvScope(env);
        e.openEnvScope(ENV.SIGMA);
        e.bindToEnv(env, id, targtype);
        e.bindToEnv(ENV.SIGMA, id, targtype);
        setNormSigma(e.getEnv(ENV.SIGMA));

        ASTType tb = body.typecheck(e);

        if (!(e.getEnv(ENV.DELTA).isEmpty()))
            throw new TypeCheckError("there are unused linear values: " + e.getEnv(ENV.DELTA));
        e.closeEnvScope(env);
        e.closeEnvScope(ENV.SIGMA);
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
            throw new TypeCheckError(String.format("func: dom type %s is not subtype of arg type %s", tdom, targtype));

        e.bindToEnv(env, id, targtype);
        e.bindToEnv(ENV.SIGMA, id, targtype);
        setNormSigma(e.getEnv(ENV.SIGMA));

        ASTType tb = body.typecheck(e, tcodom);

        e.closeEnvScope(env);
        e.closeEnvScope(ENV.SIGMA);
        return new ASTTLollipop(targtype, tb, id);
    }

    public ASTNode normalize(Env<ASTType> sigma, Env<ASTNode> sub) {
        return new ASTLFunc(id, body.normalize(getNormSigma(), sub), argtype, sub, getNormSigma());
    }

    public boolean defequals(ASTNode o, Env<ASTType> sigma, AlphaEnv alpha) {
        return o instanceof ASTLFunc olfunc && argtype.defequals(olfunc.getArgtype(), sigma, alpha)
            && body.defequals(olfunc.getBody(), sigma, alpha.extend(id, olfunc.getId()));
    }

    @Override
    public String toString() {
        return String.format("lfn %s:%s => {%s}", id, argtype, body);
	}
}
