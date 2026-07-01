package proj.ast;

import proj.values.*;
import proj.types.*;
import proj.env.*;
import proj.env.EnvSet.ENV;
import proj.errors.*;

public class ASTLFunc extends ASTNode  {
    String id;
    ASTNode body;
    ASTType argtype;
    Env<ASTNode> normEnv;
    Env<ASTType> sig;

    public ASTLFunc(String i, ASTNode b, ASTType t) {
        id = i;
        body = b;
        argtype = t;
        normEnv = null;
        sig = null;
    }

    public ASTLFunc(String i, ASTNode b, ASTType t, Env<ASTNode> sub, Env<ASTType> sigma) {
        id = i;
        body = b;
        argtype = t;
        normEnv = sub;
        sig = sigma;
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

    public Env<ASTType> getSig() {
        return sig;
    }

    public void setBody(ASTNode b) {
        body = b;
    }

    public void setNormEnv(Env<ASTNode> e) {
        normEnv = e;
    }

    public void setSig(Env<ASTType> s) {
        sig = s;
    }

    public IValue eval(Env<IValue> e) {
        return new VClos(e, id, body, true);
    }

    public ASTType typecheck(EnvSet e, ASTType target) throws TypeCheckError {
        argtype.check(e.getSigma(), e.getPhi());
        ASTType targetdom = null, targetcodom = null;
        if (target != null) {
            ASTType tt = e.unfold(target);
            String tid;
            if (tt instanceof ASTTLollipop lolli) { targetdom = lolli.getDom(); targetcodom = lolli.getCodom(); tid = lolli.getId(); }
            else throw new TypeCheckError(ErrorMessages.typeMismatch("lollipop", target));
        }

        ASTType targtype = e.unfold(argtype);
        ENV env = (targtype instanceof ASTLinType) ? ENV.DELTA : ENV.GAMMA;
        e.openEnvScope(ENV.SIGMA);
        e.openEnvScope(env);

        if (targetdom != null && !targetdom.isSubtypeOf(targtype, e.getSigma(), e.getPhi(), new AlphaEnv()))
            throw new TypeCheckError(ErrorMessages.notSubtypeFunc(targetdom, targtype));

        e.bindToEnv(env, id, targtype);
        e.bindToEnv(ENV.SIGMA, id, targtype);
        body.setSig(e.getSigma());

        ASTType tb = body.typecheck(e, targetcodom);

        if (!e.getUnusedScopeLinears().isEmpty()) throw new TypeCheckError(ErrorMessages.unusedLinearValues(e.getUnusedLinears()));
        e.closeEnvScope(env);
        e.closeEnvScope(ENV.SIGMA);
        return new ASTTLollipop(targtype, tb, id);
    }

    public ASTType puretypecheck(Env<ASTType> sigma, Env<ASTType> phi, ASTType target) throws TypeCheckError {
        argtype.check(sigma, phi);
        ASTType targetdom = null, targetcodom = null;
        if (target != null) {
            ASTType tt = phi.unfold(target);
            String tid;
            if (tt instanceof ASTTLollipop lolli) { targetdom = lolli.getDom(); targetcodom = lolli.getCodom(); tid = lolli.getId(); }
            else throw new TypeCheckError(ErrorMessages.typeMismatch("arrow or lollipop", target));
        }

        ASTType targtype = phi.unfold(argtype);
        Env<ASTType> env = sigma.beginScope();
        if (targetdom != null && !targetdom.isSubtypeOf(targtype, sigma, phi, new AlphaEnv()))
            throw new TypeCheckError(ErrorMessages.notSubtypeFunc(targetdom, targtype));
        env.assoc(id, targtype);
        body.setSig(env);

        ASTType tb = body.puretypecheck(env, phi, targetcodom);
        return new ASTTArrow(targtype, tb, id);
    }

    public ASTNode weaknorm(Env<ASTNode> sub) {
        if (normEnv == null) setNormEnv(sub);
        return new ASTLFunc(id, body, argtype, getNormEnv(), getSig());
    }

    public ASTNode subs(String subsId, ASTNode node) {
        return new ASTLFunc(id, body.subs(subsId, node), argtype, normEnv, sig);
    }

    @Override
    public String toString() {
        return String.format("lfn %s:%s => {%s}", id, argtype, body);
	}
}
