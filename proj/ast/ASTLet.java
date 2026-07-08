package proj.ast;

import proj.values.*;
import proj.types.*;
import proj.env.*;
import proj.env.EnvSet.ENV;
import proj.errors.*;

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
        if (declType != null) declType.check(e.getSigma(), e.getPhi());

        ASTType tt = (declType != null) ? declType : expr.typecheck(e, null);
        tt = e.unfold(tt);

        ENV env = (tt instanceof ASTLinType) ? ENV.DELTA : ENV.GAMMA;
        e.openEnvScope(env);
        e.openEnvScope(ENV.SIGMA);

        e.bindToEnv(env, id, tt);

        if (declType != null) {
            ASTType exprType = expr.typecheck(e, tt);
            if (!(exprType.isSubtypeOf(tt, e.getSigma(), e.getPhi(), e.getAlpha()))) throw new TypeCheckError(ErrorMessages.notSubtype(exprType, tt));
        }

        expr.setSig(e.getSigma());
        e.addEq(new ASTTEq(new ASTId(id), expr, tt));
        e.bindToEnv(ENV.SIGMA, id, tt);

        ASTType rt = body.typecheck(e, target);
        if (!e.getUnusedScopeLinears().isEmpty()) throw new TypeCheckError(ErrorMessages.unusedLinearValues(e.getUnusedLinears()));
        e.closeEnvScope(env);
        e.closeEnvScope(ENV.SIGMA);

        return rt;
	}

    public ASTType puretypecheck(Env<ASTType> sigma, Env<ASTType> phi, ASTType target) throws TypeCheckError {
        if (declType != null) declType.check(sigma, phi);

        ASTType tt = (declType != null) ? declType : expr.puretypecheck(sigma, phi, null);
        tt = phi.unfold(tt);

        Env<ASTType> env = sigma.beginScope();
        sigma.addEq(new ASTTEq(new ASTId(id), expr, tt));
        env.assoc(id, tt);

        if (declType != null) {
            ASTType exprType = expr.puretypecheck(env, phi, tt);
            if (!(exprType.isSubtypeOf(tt, env, phi, new AlphaEnv())))
                throw new TypeCheckError(ErrorMessages.notSubtype(exprType, tt));
        }

        return body.puretypecheck(env, phi, target);
    }

    public ASTNode weaknorm(Env<ASTNode> sub) {
        Env<ASTNode> e = sub;
        if (declType != null) {
            e = sub.beginScope();
            if (expr instanceof ASTFunc f && f.getNormEnv() == null) f.setNormEnv(e);
            if (expr instanceof ASTLFunc lf && lf.getNormEnv() == null) lf.setNormEnv(e);
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
