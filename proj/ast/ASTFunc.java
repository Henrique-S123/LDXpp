package proj.ast;

import proj.values.*;
import proj.types.*;
import proj.env.*;
import proj.env.EnvSet.ENV;
import proj.errors.*;

import java.util.UUID;

public class ASTFunc implements ASTNode  {
    String id;
    ASTNode body;
    ASTType argtype;
    Env<ASTNode> normEnv;
    Env<ASTType> normSigma;

    public ASTFunc(String i, ASTNode b, ASTType t) {
        id = i;
        body = b;
        argtype = t;
        normEnv = null;
        normSigma = null;
    }

    public ASTFunc(String i, ASTNode b, ASTType t, Env<ASTNode> sub, Env<ASTType> sigma) {
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
        return new VClos(e, id, body, false);
    }

    public ASTType typecheck(EnvSet e) throws TypeCheckError, EnvironmentError {
        ASTType targtype = e.unfold(argtype);
        e.openEnvScope(ENV.GAMMA);
        e.openEnvScope(ENV.SIGMA);
        e.bindToEnv(ENV.GAMMA, id, targtype);
        e.bindToEnv(ENV.SIGMA, id, targtype);
        setNormSigma(e.getEnv(ENV.SIGMA));
        Env<ASTType> prevDelta = e.popDelta();

        ASTType tb = body.typecheck(e);

        e.setEnv(ENV.DELTA, prevDelta);
        e.closeEnvScope(ENV.GAMMA);
        e.closeEnvScope(ENV.SIGMA);
        return new ASTTArrow(targtype, tb, id);
	}

    public ASTType typecheck(EnvSet e, ASTType t) throws TypeCheckError, EnvironmentError {
        ASTType tt = e.unfold(t);
        ASTType tdom, tcodom;
        String tid;

        if (tt instanceof ASTTArrow arrow) { tdom = arrow.getDom(); tcodom = arrow.getCodom(); tid = arrow.getId(); }
        else if (tt instanceof ASTTLollipop lolli) { tdom = lolli.getCodom(); tcodom = lolli.getCodom(); tid = lolli.getId(); }
        else throw new TypeCheckError("func: expected arrow type");

        Env<ASTType> prevDelta = e.popDelta();
        e.openEnvScope(ENV.SIGMA);
        e.openEnvScope(ENV.GAMMA);

        ASTType targtype = e.unfold(argtype);
        if (!tdom.isSubtypeOf(targtype, e))
            throw new TypeCheckError(String.format("func: dom type %s is not subtype of arg type %s", tdom, targtype));

        e.bindToEnv(ENV.GAMMA, id, targtype);
        e.bindToEnv(ENV.SIGMA, id, targtype);
        setNormSigma(e.getEnv(ENV.SIGMA));

        ASTType tb = body.typecheck(e, tcodom);

        e.setEnv(ENV.DELTA, prevDelta);
        e.closeEnvScope(ENV.GAMMA);
        e.closeEnvScope(ENV.SIGMA);

        return new ASTTArrow(targtype, tb, id);
    }

    public ASTNode normalize(Env<ASTType> sigma, Env<ASTNode> sub) {
        ASTNode n = sub.find(id, false);
        String newid = (n instanceof ASTId idn) ? idn.getId() : id;
        return new ASTFunc(newid, body.normalize(getNormSigma(), sub), argtype, sub, getNormSigma());
    }

    public boolean defequals(ASTNode o, Env<ASTType> sigma) {
        return o instanceof ASTFunc ofunc && ofunc.getArgtype().defequals(argtype, sigma, new AlphaEnv())
            && alphaequiv(ofunc, sigma);
    }

    public boolean alphaequiv(ASTFunc t2, Env<ASTType> sigma) {
        String newid = UUID.randomUUID().toString();
        Env<ASTNode> e = new Env<ASTNode>();
        Env<ASTNode> lenv = e.beginScope();
        Env<ASTNode> renv = e.beginScope();
        lenv.assoc(id, new ASTId(newid));
        renv.assoc(t2.getId(), new ASTId(newid));

        ASTNode bn = body.normalize(sigma, lenv);
        ASTNode obn = t2.getBody().normalize(sigma, renv);
        return bn.defequals(obn, sigma);
    }

    @Override
    public String toString() {
        return String.format("fn %s:%s => {%s}", id, argtype, body);
	}
}
