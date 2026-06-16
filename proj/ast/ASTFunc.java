package proj.ast;

import proj.values.*;
import proj.types.*;
import proj.env.*;
import proj.env.EnvSet.ENV;
import proj.errors.*;

public class ASTFunc extends ASTNode  {
    String id;
    ASTNode body;
    ASTType argtype;
    Env<ASTNode> normEnv;
    Env<ASTType> sig;

    public ASTFunc(String i, ASTNode b, ASTType t) {
        id = i;
        body = b;
        argtype = t;
        normEnv = null;
        sig = null;
    }

    public ASTFunc(String i, ASTNode b, ASTType t, Env<ASTNode> sub, Env<ASTType> sigma) {
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
        return new VClos(e, id, body, false);
    }

    public ASTType typecheck(EnvSet e, ASTType target) throws TypeCheckError {
        argtype.check(e.getSigma(), e.getPhi());
        ASTType targetdom = null, targetcodom = null;
        if (target != null) {
            ASTType tt = e.unfold(target);
            String tid;
            if (tt instanceof ASTTArrow arrow) { targetdom = arrow.getDom(); targetcodom = arrow.getCodom(); tid = arrow.getId(); }
            else if (tt instanceof ASTTLollipop lolli) { targetdom = lolli.getDom(); targetcodom = lolli.getCodom(); tid = lolli.getId(); }
            else throw new TypeCheckError(ErrorMessages.typeMismatch("arrow or lollipop", target));
        }

        ASTType targtype = e.unfold(argtype);
        Env<LinearBinding> prevDelta = e.popDelta();
        e.openEnvScope(ENV.SIGMA);
        e.openEnvScope(ENV.GAMMA);

        if (targetdom != null && !targetdom.isSubtypeOf(targtype, e.getSigma(), e.getPhi(), new AlphaEnv()))
            throw new TypeCheckError(ErrorMessages.notSubtypeFunc(targetdom, targtype));

        e.bindToEnv(ENV.GAMMA, id, targtype);
        e.bindToEnv(ENV.SIGMA, id, targtype);
        setSig(e.getSigma());

        ASTType tb = body.typecheck(e, targetcodom);

        e.pushDelta(prevDelta);
        e.closeEnvScope(ENV.GAMMA);
        e.closeEnvScope(ENV.SIGMA);
        return new ASTTArrow(targtype, tb, id);
    }

    public ASTNode weaknorm(Env<ASTNode> sub) {
        if (normEnv == null) setNormEnv(sub);
        return new ASTFunc(id, body, argtype, getNormEnv(), getSig());
    }

    public ASTNode subs(String subsId, ASTNode node) {
        return new ASTFunc(id, body.subs(subsId, node), argtype, normEnv, sig);
    }

    @Override
    public String toString() {
        return String.format("fn %s:%s => {%s}", id, argtype, body);
	}
}
