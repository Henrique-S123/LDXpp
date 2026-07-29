package proj.src.ast;

import proj.src.values.*;
import proj.src.types.*;
import proj.src.env.*;
import proj.src.env.EnvSet.ENV;
import proj.src.errors.*;

public class ASTRec extends ASTNode  {
    private final String fid;
    private final ASTType functype;
    private final ASTNode funcbody, body;

    public ASTRec(String f, ASTType t, ASTNode fb, ASTNode b) {
        fid = f; functype = t; funcbody = fb; body = b;
    }

    public String getFuncid() { return fid; }

    public ASTType getFunctype() { return functype; }

    public ASTNode getFuncbody() { return funcbody; }

    public ASTNode getBody() { return body; }

    public IValue eval(Env<IValue> e) throws InterpreterError {
        VRec v = new VRec(e, fid, funcbody, false);
        Env<IValue> en = e.beginScope();
        en.assoc(fid, v);
        return body.eval(en);
    }

    public ASTType typecheck(EnvSet e, ASTType target) throws TypeCheckError {
        functype.check(e.getSigma(), e.getPhi(), e.getAlpha());
        ASTType tfunctype = e.unfold(functype);
        if (!(tfunctype instanceof ASTTArrow))
            throw new TypeCheckError(ErrorMessages.illegalTypeToUnary("rec", tfunctype));

        e.openEnvScope(ENV.GAMMA);
        e.openEnvScope(ENV.SIGMA);
        Binder<ASTType> b = new Binder<ASTType>(tfunctype);
        e.bindToEnv(ENV.GAMMA, fid, b);
        e.bindToEnv(ENV.SIGMA, fid, b);
        ResourceManager<ASTType> prevDelta = e.popDelta();
        funcbody.setSig(e.getSigma());
        ASTType tfb = funcbody.typecheck(e, tfunctype);
        if (!tfb.isSubtypeOf(tfunctype, e.getSigma(), e.getPhi(), e.getAlpha()))
            throw new TypeCheckError(ErrorMessages.notSubtype(tfb, tfunctype));
        e.pushDelta(prevDelta);
        e.closeEnvScope(ENV.GAMMA);

        ENV env = tfunctype.isLinear() ? ENV.DELTA : ENV.GAMMA;
        e.openEnvScope(env);
        e.bindToEnv(env, fid, b);
        e.bindToEnv(ENV.SIGMA, e.getFreshId(), new ASTTEq(new ASTId(fid, b.getId()), funcbody, tfunctype));
        ASTType tb = body.typecheck(e, target);
        return tb;
    }

    public ASTType puretypecheck(Env<ASTType> sigma, Env<ASTType> phi, AlphaEnv alpha, ASTType target) throws TypeCheckError {
        functype.check(sigma, phi, alpha);
        ASTType tfunctype = phi.unfold(functype);
        if (!(tfunctype instanceof ASTTArrow))
            throw new TypeCheckError(ErrorMessages.illegalTypeToUnary("rec", tfunctype));

        Binder<ASTType> b = new Binder<ASTType>(tfunctype);
        Env<ASTType> env = sigma.beginScope();
        env.assoc(fid, b);
        funcbody.setSig(sigma);
        
        ASTType tfb = funcbody.puretypecheck(env, phi, alpha, tfunctype);
        if (!tfb.isSubtypeOf(tfunctype, sigma, phi, alpha))
            throw new TypeCheckError(ErrorMessages.notSubtype(tfb, tfunctype));

        env.assoc(env.getFreshId(), new ASTTEq(new ASTId(fid, b.getId()), funcbody, tfunctype));
        ASTType tb = body.puretypecheck(env, phi, alpha, target);
        return tb;
    }

    public ASTNode weaknorm(Env<ASTNode> sub) {
        Env<ASTNode> env = sub.beginScope();
        if (funcbody instanceof ASTFunc f && f.getNormEnv() == null) f.setNormEnv(env);
        env.assoc(fid, funcbody);
        return body.weaknorm(env);
    }

    public ASTNode subs(String subsId, ASTNode node) {
        return new ASTRec(fid, functype, funcbody.subs(subsId, node), body.subs(subsId, node));
    }

    @Override
    public String toString() {
        return String.format("letrec %s:%s {%s}; %s", fid, functype, funcbody, body);
	}
}
