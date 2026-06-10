package proj.ast;

import proj.values.*;
import proj.types.*;
import proj.env.*;
import proj.env.EnvSet.ENV;
import proj.errors.*;

public class ASTRec extends ASTNode  {
    String fid;
    ASTNode body;
    ASTType functype;

    public ASTRec(String f, ASTNode b, ASTType t) {
        fid = f;
        body = b;
        functype = t;
    }

    public String getFid() {
        return fid;
    }

    public ASTNode getBody() {
        return body;
    }

    public void setBody(ASTNode b) {
        body = b;
    }

    public IValue eval(Env<IValue> e) {
        return new VRec(e, fid, body, false);
    }

    public ASTType typeinfer(EnvSet e) throws TypeCheckError, EnvironmentError {
        ASTType tfunctype = e.unfold(functype);
        if (!(tfunctype instanceof ASTTArrow || tfunctype instanceof ASTTLollipop))
            throw new TypeCheckError(ErrorMessages.illegalTypeToUnary("rec", tfunctype));

        e.openEnvScope(ENV.GAMMA);
        e.openEnvScope(ENV.SIGMA);
        e.bindToEnv(ENV.GAMMA, fid, tfunctype);
        e.bindToEnv(ENV.SIGMA, fid, tfunctype);
        
        Env<LinearBinding> prevDelta = e.popDelta();
        ASTType tb = body.typeinfer(e);
        if (!tb.isSubtypeOf(tfunctype, e.getSigma(), e.getPhi(), new AlphaEnv()))
            throw new TypeCheckError(ErrorMessages.notSubtype(tb, tfunctype));
        e.pushDelta(prevDelta);
        return tfunctype;
	}

    public ASTType typecheck(EnvSet e, ASTType target) throws TypeCheckError, EnvironmentError {
        // TODO
        ASTType tt = e.unfold(target);
        ASTType tdom, tcodom;
        String tid;

        if (tt instanceof ASTTArrow arrow) { tdom = arrow.getDom(); tcodom = arrow.getCodom(); tid = arrow.getId(); }
        else if (tt instanceof ASTTLollipop lolli) { tdom = lolli.getDom(); tcodom = lolli.getCodom(); tid = lolli.getId(); }
        else throw new TypeCheckError(ErrorMessages.typeMismatch("arrow or lollipop", target));

        ASTType targtype = e.unfold(functype);
        Env<LinearBinding> prevDelta = e.popDelta();
        e.openEnvScope(ENV.SIGMA);
        e.openEnvScope(ENV.GAMMA);

        if (!tdom.isSubtypeOf(targtype, e.getSigma(), e.getPhi(), new AlphaEnv())) throw new TypeCheckError(ErrorMessages.notSubtypeFunc(tdom, targtype));

        e.bindToEnv(ENV.GAMMA, fid, targtype);
        e.bindToEnv(ENV.SIGMA, fid, targtype);

        ASTType tb = body.typecheck(e, tcodom);

        e.pushDelta(prevDelta);
        e.closeEnvScope(ENV.GAMMA);
        e.closeEnvScope(ENV.SIGMA);

        return new ASTTArrow(targtype, tb, fid);
    }

    public ASTNode weaknorm(Env<ASTNode> sub) {
        return new ASTRec(fid, body, functype);
    }

    public ASTNode subs(String subsId, ASTNode node) {
        return new ASTRec(fid, body.subs(subsId, node), functype);
    }

    @Override
    public String toString() {
        return String.format("rec $s:%s => {%s}", fid, functype, body);
	}
}
