package proj.src.ast;

import proj.src.values.*;
import proj.src.types.*;
import proj.src.env.*;
import proj.src.env.EnvSet.ENV;
import proj.src.env.PureEnvSet.PENV;
import proj.src.errors.*;

public class ASTFunc extends ASTNode  {
    private final String id;
    private ASTNode body;
    private final ASTType argtype;
    private Env<ASTNode> normEnv;
    private final boolean lin;

    public ASTFunc(String i, ASTNode b, ASTType t, boolean l) {
        id = i; body = b; argtype = t; lin = l; normEnv = null;
    }

    public ASTFunc(String i, ASTNode b, ASTType t, boolean l, Env<ASTNode> sub) {
        id = i; body = b; argtype = t; lin = l; normEnv = sub;
    }

    public String getId() { return id; }

    public ASTNode getBody() { return body; }

    public ASTType getArgtype() { return argtype; }

    public Env<ASTNode> getNormEnv() { return normEnv; }

    public boolean isLinear() { return lin; }

    public void setBody(ASTNode b) { body = b; }

    public void setNormEnv(Env<ASTNode> e) { normEnv = e; }

    public IValue eval(Env<IValue> e) {
        return new VClos(e, id, body, lin);
    }

    public ASTType typecheck(EnvSet e, ASTType target) throws TypeCheckError {
        argtype.check(new PureEnvSet(e));
        ASTType tgtdom = null, tgtcodom = null;
        String tgtbid = null;
        if (target != null) {
            ASTType tt = e.unfold(target);
            if (tt instanceof ASTTArrow arrow && (!lin || arrow.isLinear())) {
                tgtdom = arrow.getDom(); tgtcodom = arrow.getCodom(); tgtbid = arrow.getBid(); }
            else if (lin) throw new TypeCheckError(ErrorMessages.typeMismatch("lollipop", target));
            else throw new TypeCheckError(ErrorMessages.typeMismatch("arrow or lollipop", target));
        }

        ResourceManager<ASTType> prevDelta = null;
        if (!lin) prevDelta = e.popDelta();

        ASTType targtype = e.unfold(argtype);
        if (tgtdom != null && !tgtdom.isSubtypeOf(targtype, new PureEnvSet(e)))
            throw new TypeCheckError(ErrorMessages.notSubtypeFunc(tgtdom, targtype));
        
        ENV env = (lin && targtype.isLinear()) ? ENV.DELTA : ENV.GAMMA;
        e.openEnvScope(env);
        Binder<ASTType> b = e.bindToEnv(env, id, targtype);
        e.openEnvScope(ENV.SIGMA);
        e.bindToEnv(ENV.SIGMA, id, b);

        if (tgtcodom != null && tgtbid != null) tgtcodom = tgtcodom.inst(tgtbid, new ASTId(id, b.getId()));
        ASTType tb = body.typecheck(e, tgtcodom);
        if (tgtcodom != null && !tb.isSubtypeOf(tgtcodom, new PureEnvSet(e)))
            throw new TypeCheckError(ErrorMessages.notSubtype(tb, tgtcodom));

        if (!lin) e.pushDelta(prevDelta);
        if (lin && !e.getUnusedScopeLinears().isEmpty())
            throw new TypeCheckError(ErrorMessages.unusedLinearValues(e.getUnusedLinears()));

        e.closeEnvScope(env);
        e.closeEnvScope(ENV.SIGMA);
        return target == null ? new ASTTArrow(targtype, tb, new ASTId(id, b.getId()), lin) : target;
    }

    public ASTType puretypecheck(PureEnvSet pe, ASTType target) throws TypeCheckError {
        argtype.check(pe);
        ASTType tgtdom = null, tgtcodom = null;
        String tgtbid = null;
        if (target != null) {
            ASTType tt = pe.unfold(target);
            if (tt instanceof ASTTArrow arrow && (!lin || arrow.isLinear())) {
                tgtdom = arrow.getDom(); tgtcodom = arrow.getCodom(); tgtbid = arrow.getBid(); }
            else if (lin) throw new TypeCheckError(ErrorMessages.typeMismatch("lollipop", target));
            else throw new TypeCheckError(ErrorMessages.typeMismatch("arrow or lollipop", target));
        }

        ASTType targtype = pe.unfold(argtype);
        if (tgtdom != null && !tgtdom.isSubtypeOf(targtype, pe))
            throw new TypeCheckError(ErrorMessages.notSubtypeFunc(tgtdom, targtype));

        pe.openEnvScope(PENV.SIGMA);
        Binder<ASTType> b = pe.bindToEnv(PENV.SIGMA, id, targtype);

        if (tgtcodom != null && tgtbid != null) tgtcodom = tgtcodom.inst(tgtbid, new ASTId(id, b.getId()));
        ASTType tb = body.puretypecheck(pe, tgtcodom);
        if (tgtcodom != null && !tb.isSubtypeOf(tgtcodom, pe))
            throw new TypeCheckError(ErrorMessages.notSubtype(tb, tgtcodom));

        pe.closeEnvScope(PENV.SIGMA);
        return target == null ? new ASTTArrow(targtype, tb, new ASTId(id, b.getId()), lin) : target;
    }

    public ASTNode weaknorm(Env<ASTNode> sub) {
        return new ASTFunc(id, body, argtype, lin, sub);
    }

    public ASTNode subs(String subsId, ASTNode node) {
        return new ASTFunc(id, body.subs(subsId, node), argtype, lin, normEnv);
    }

    @Override
    public String toString() {
        return String.format("%sfn %s:%s =%s> {%s}", lin ? "l" : "", id, argtype, lin ? "o" : "", body);
	}
}
