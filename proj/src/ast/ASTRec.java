package proj.src.ast;

import proj.src.values.*;
import proj.src.types.*;
import proj.src.env.*;
import proj.src.env.EnvSet.ENV;
import proj.src.errors.*;

public class ASTRec extends ASTNode  {
    private final String fid;
    private final ASTType functype;
    private ASTNode body;

    public ASTRec(String f, ASTType t, ASTNode b) {
        fid = f; functype = t; body = b;
    }

    public String getFid() { return fid; }

    public ASTNode getBody() { return body; }

    public void setBody(ASTNode b) { body = b; }

    public IValue eval(Env<IValue> e) {
        return new VRec(e, fid, body, false);
    }

    public ASTType typecheck(EnvSet e, ASTType target) throws TypeCheckError {
        functype.check(e.getSigma(), e.getPhi(), e.getAlpha());
        ASTType targetcodom = null;
        if (target != null) {
            ASTType tt = e.unfold(target);
            if (tt instanceof ASTTArrow arrow) { targetcodom = arrow.getCodom(); }
            else throw new TypeCheckError(ErrorMessages.typeMismatch("arrow or lollipop", target));
        }

        ASTType tfunctype = e.unfold(functype);
        if (!(tfunctype instanceof ASTTArrow))
            throw new TypeCheckError(ErrorMessages.illegalTypeToUnary("rec", tfunctype));

        e.openEnvScope(ENV.SIGMA);
        e.openEnvScope(ENV.GAMMA);
        Binder<ASTType> b = new Binder<ASTType>(tfunctype);
        e.bindToEnv(ENV.GAMMA, fid, b);
        e.bindToEnv(ENV.SIGMA, fid, b);
        
        ResourceManager<ASTType> prevDelta = e.popDelta();
        ASTType tb = body.typecheck(e, tfunctype);
        if (targetcodom != null && !tb.isSubtypeOf(targetcodom, e.getSigma(), e.getPhi(), e.getAlpha()))
            throw new TypeCheckError(ErrorMessages.notSubtype(tb, targetcodom));

        e.pushDelta(prevDelta);
        e.closeEnvScope(ENV.GAMMA);
        e.closeEnvScope(ENV.SIGMA);
        return tfunctype;
    }

    public ASTType puretypecheck(Env<ASTType> sigma, Env<ASTType> phi, AlphaEnv alpha, ASTType target) throws TypeCheckError {
        functype.check(sigma, phi, alpha);
        ASTType targetcodom = null;
        if (target != null) {
            ASTType tt = phi.unfold(target);
            if (tt instanceof ASTTArrow arrow) { targetcodom = arrow.getCodom(); }
            else throw new TypeCheckError(ErrorMessages.typeMismatch("arrow or lollipop", target));
        }
        ASTType tfunctype = phi.unfold(functype);
        if (!(tfunctype instanceof ASTTArrow))
            throw new TypeCheckError(ErrorMessages.illegalTypeToUnary("rec", tfunctype));

        Env<ASTType> env = sigma.beginScope();
        env.assoc(fid, tfunctype);
        
        ASTType tb = body.puretypecheck(env, phi, alpha, targetcodom);
        if (targetcodom != null && !tb.isSubtypeOf(targetcodom, sigma, phi, alpha))
            throw new TypeCheckError(ErrorMessages.notSubtype(tb, targetcodom));
        return tfunctype;
    }

    public ASTNode weaknorm(Env<ASTNode> sub) {
        return new ASTRec(fid, functype, body);
    }

    public ASTNode subs(String subsId, ASTNode node) {
        return new ASTRec(fid, functype, body.subs(subsId, node));
    }

    @Override
    public String toString() {
        return String.format("rec $s:%s => {%s}", fid, functype, body);
	}
}
