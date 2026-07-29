package proj.src.ast;

import proj.src.values.*;
import proj.src.types.*;
import proj.src.env.*;
import proj.src.env.EnvSet.ENV;
import proj.src.errors.*;

public class ASTLet extends ASTNode {
    private final String id;
    private final ASTNode expr, body;
    private final ASTType declType;

    public ASTLet(String i, ASTNode e, ASTType t, ASTNode b) {
        id = i;
        expr = e;
        declType = t;
        body = b;
    }

    public String getId() {
        return id;
    }

    public ASTNode getExpr() {
        return expr;
    }

    public ASTType getDeclType() {
        return declType;
    }

    public ASTNode getBody() {
        return body;
    }

    public IValue eval(Env<IValue> e) throws InterpreterError {
        Env<IValue> en = e.beginScope();
        en.assoc(id, expr.eval(en));
        return body.eval(en);
    }

    public ASTType typecheck(EnvSet e, ASTType target) throws TypeCheckError {
        if (declType != null) declType.check(e.getSigma(), e.getPhi(), e.getAlpha());

        ASTType texp = expr.typecheck(e, declType);
        texp = e.unfold(texp);
        if (declType != null && !texp.isSubtypeOf(declType, e.getSigma(), e.getPhi(), e.getAlpha()))
            throw new TypeCheckError(ErrorMessages.notSubtype(texp, declType));
        ASTType tt = (declType != null) ? e.unfold(declType) : texp;

        ENV env = tt.isLinear() ? ENV.DELTA : ENV.GAMMA;
        e.openEnvScope(env);
        e.openEnvScope(ENV.SIGMA);
        Binder<ASTType> b = new Binder<ASTType>(tt);
        e.bindToEnv(env, id, b);
        e.bindToEnv(ENV.SIGMA, id, b);
        e.bindToEnv(ENV.SIGMA, e.getFreshId(), new ASTTEq(new ASTId(id, b.getId()), expr, tt));

        ASTType rt = body.typecheck(e, target);
        if (!e.getUnusedScopeLinears().isEmpty()) throw new TypeCheckError(ErrorMessages.unusedLinearValues(e.getUnusedLinears()));
        e.closeEnvScope(env);
        e.closeEnvScope(ENV.SIGMA);

        return rt;
	}

    public ASTType puretypecheck(Env<ASTType> sigma, Env<ASTType> phi, AlphaEnv alpha, ASTType target) throws TypeCheckError {
        if (declType != null) declType.check(sigma, phi, alpha);

        ASTType texp = expr.puretypecheck(sigma, phi, alpha, declType);
        texp = phi.unfold(texp);
        if (declType != null && !texp.isSubtypeOf(declType, sigma, phi, alpha))
            throw new TypeCheckError(ErrorMessages.notSubtype(texp, declType));
        ASTType tt = (declType != null) ? phi.unfold(declType) : texp;

        Binder<ASTType> b = new Binder<ASTType>(tt);
        Env<ASTType> env = sigma.beginScope();
        env.assoc(id, b);
        env.assoc(env.getFreshId(), new ASTTEq(new ASTId(id, b.getId()), expr, tt));
        return body.puretypecheck(env, phi, alpha, target);
    }

    public ASTNode weaknorm(Env<ASTNode> sub) {
        Env<ASTNode> e = sub;
        if (declType != null) {
            e = sub.beginScope();
            if (expr instanceof ASTFunc f && f.getNormEnv() == null) f.setNormEnv(e);
            e.assoc(id, expr);
        }
        ASTNode normExpr = expr.weaknorm(e);
        Env<ASTNode> env = sub.beginScope();
        env.assoc(id, normExpr);
        return body.weaknorm(env);
    }

    public ASTNode subs(String subsId, ASTNode node) {
		return new ASTLet(id, expr.subs(subsId, node), declType, body.subs(subsId, node));
	}

    @Override
    public String toString() {
        String typeString = (declType == null ? "" : String.format(" %s,", declType));
		return String.format("let(%s,%s %s, %s)", id, typeString, expr, body);
	}
}
