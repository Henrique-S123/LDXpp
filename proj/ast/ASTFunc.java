package proj.ast;

import proj.values.*;
import proj.types.*;
import proj.env.*;
import proj.env.EnvSet.ENV;
import proj.errors.*;

public class ASTFunc extends ASTNode  {
    private final String id;
    private ASTNode body;
    private final ASTType argtype;
    private Env<ASTNode> normEnv;

    public ASTFunc(String i, ASTNode b, ASTType t) {
        id = i;
        body = b;
        argtype = t;
        normEnv = null;
    }

    public ASTFunc(String i, ASTNode b, ASTType t, Env<ASTNode> sub) {
        id = i;
        body = b;
        argtype = t;
        normEnv = sub;
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

    public void setBody(ASTNode b) {
        body = b;
    }

    public void setNormEnv(Env<ASTNode> e) {
        normEnv = e;
    }

    public IValue eval(Env<IValue> e) {
        return new VClos(e, id, body, false);
    }

    public ASTType typecheck(EnvSet e, ASTType target) throws TypeCheckError {
        argtype.check(e.getSigma(), e.getPhi(), e.getAlpha());
        ASTType targetdom = null, targetcodom = null;
        String tid = null;
        if (target != null) {
            ASTType tt = e.unfold(target);
            if (tt instanceof ASTTArrow arrow) { targetdom = arrow.getDom(); targetcodom = arrow.getCodom(); tid = arrow.getId(); }
            else if (tt instanceof ASTTLollipop lolli) { targetdom = lolli.getDom(); targetcodom = lolli.getCodom(); tid = lolli.getId(); }
            else throw new TypeCheckError(ErrorMessages.typeMismatch("arrow or lollipop", target));
        }

        ASTType targtype = e.unfold(argtype);
        Env<LinearBinding> prevDelta = e.popDelta();
        e.openEnvScope(ENV.SIGMA);
        e.openEnvScope(ENV.GAMMA);

        if (targetdom != null && !targetdom.isSubtypeOf(targtype, e.getSigma(), e.getPhi(), e.getAlpha()))
            throw new TypeCheckError(ErrorMessages.notSubtypeFunc(targetdom, targtype));

        Binder<ASTType> b = new Binder<ASTType>(targtype);
        e.bindToEnv(ENV.GAMMA, id, b);
        e.bindToEnv(ENV.SIGMA, id, b);
        body.setSig(e.getSigma());
        if (tid != null) e.extendAlpha(id, tid);

        ASTType tb = body.typecheck(e, targetcodom);

        e.pushDelta(prevDelta);
        e.closeEnvScope(ENV.GAMMA);
        e.closeEnvScope(ENV.SIGMA);
        return new ASTTArrow(targtype, tb, id);
    }

    public ASTType puretypecheck(Env<ASTType> sigma, Env<ASTType> phi, AlphaEnv alpha, ASTType target) throws TypeCheckError {
        argtype.check(sigma, phi, alpha);
        ASTType targetdom = null, targetcodom = null;
        String tid = null;
        if (target != null) {
            ASTType tt = phi.unfold(target);
            if (tt instanceof ASTTArrow arrow) { targetdom = arrow.getDom(); targetcodom = arrow.getCodom(); tid = arrow.getId(); }
            else if (tt instanceof ASTTLollipop lolli) { targetdom = lolli.getDom(); targetcodom = lolli.getCodom(); tid = lolli.getId(); }
            else throw new TypeCheckError(ErrorMessages.typeMismatch("arrow or lollipop", target));
        }

        ASTType targtype = phi.unfold(argtype);
        Env<ASTType> env = sigma.beginScope();
        if (targetdom != null && !targetdom.isSubtypeOf(targtype, sigma, phi, alpha))
            throw new TypeCheckError(ErrorMessages.notSubtypeFunc(targetdom, targtype));
        env.assoc(id, targtype);
        body.setSig(env);
        if (tid != null) alpha.extend(id, tid);

        ASTType tb = body.puretypecheck(env, phi, alpha, targetcodom);
        return new ASTTArrow(targtype, tb, id);
    }

    public ASTNode weaknorm(Env<ASTNode> sub) {
        return new ASTFunc(id, body, argtype, sub);
    }

    public ASTNode subs(String subsId, ASTNode node) {
        return new ASTFunc(id, body.subs(subsId, node), argtype, normEnv);
    }

    @Override
    public String toString() {
        return String.format("fn %s:%s => {%s}", id, argtype, body);
	}
}
