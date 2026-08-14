package proj.src.ast;

import proj.src.values.*;
import proj.src.types.*;
import proj.src.env.*;
import proj.src.env.EnvSet.ENV;
import proj.src.env.PureEnvSet.PENV;
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
        functype.check(new PureEnvSet(e));
        ASTType tfunctype = e.unfold(functype);
        if (!(tfunctype instanceof ASTTArrow))
            throw new TypeCheckError(ErrorMessages.illegalTypeToUnary("rec", tfunctype));

        e.openEnvScope(ENV.GAMMA);
        Binder<ASTType> b = e.bindToEnv(ENV.GAMMA, fid, tfunctype);
        e.openEnvScope(ENV.SIGMA);
        e.bindToEnv(ENV.SIGMA, fid, b);
        ResourceManager<ASTType> prevDelta = e.popDelta();
        ASTType tfb = funcbody.typecheck(e, tfunctype);
        if (!tfb.isSubtypeOf(tfunctype, new PureEnvSet(e)))
            throw new TypeCheckError(ErrorMessages.notSubtype(tfb, tfunctype));
        e.pushDelta(prevDelta);
        e.closeEnvScope(ENV.GAMMA);

        ENV env = tfunctype.isLinear() ? ENV.DELTA : ENV.GAMMA;
        e.openEnvScope(env);
        e.bindToEnv(env, fid, b);
        e.bindToEnv(ENV.SIGMA, e.getFreshId(), new ASTTEq(new ASTId(fid, b.getId()), funcbody, tfunctype));
        ASTType tb = body.typecheck(e, target);

        e.closeEnvScope(env);
        e.closeEnvScope(ENV.SIGMA);
        return tb;
    }

    public ASTType puretypecheck(PureEnvSet pe, ASTType target) throws TypeCheckError {
        functype.check(pe);
        ASTType tfunctype = pe.unfold(functype);
        if (!(tfunctype instanceof ASTTArrow))
            throw new TypeCheckError(ErrorMessages.illegalTypeToUnary("rec", tfunctype));

        pe.openEnvScope(PENV.SIGMA);
        Binder<ASTType> b = pe.bindToEnv(PENV.SIGMA, fid, tfunctype);
        
        ASTType tfb = funcbody.puretypecheck(pe, tfunctype);
        if (!tfb.isSubtypeOf(tfunctype, pe))
            throw new TypeCheckError(ErrorMessages.notSubtype(tfb, tfunctype));

        pe.bindToEnv(PENV.SIGMA, pe.getFreshId(), new ASTTEq(new ASTId(fid, b.getId()), funcbody, tfunctype));
        ASTType tb = body.puretypecheck(pe, target);

        pe.closeEnvScope(PENV.SIGMA);
        return tb;
    }

    public ASTNode weaknorm(Env<ASTNode> sub) {
        Env<ASTNode> env = sub.beginScope();
        if (funcbody instanceof ASTFunc f && f.getNormEnv() == null) f.setNormEnv(env);
        env.assoc(fid, funcbody);
        return body.weaknorm(env);
    }

    public ASTNode subs(String subsId, ASTNode node) {
        return new ASTRec(fid, functype, funcbody, body.subs(subsId, node));
    }

    @Override
    public String toString() {
        return String.format("letrec %s:%s {%s}; %s", fid, functype, funcbody, body);
	}
}
