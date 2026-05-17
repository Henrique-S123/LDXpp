package proj.ast;

import proj.values.*;
import proj.types.*;
import proj.env.*;
import proj.env.EnvSet.ENV;
import proj.errors.*;

public class ASTLet extends ASTNode {
    String id;
    ASTNode expr;
    ASTType declType;
    ASTNode body;

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

    public ASTType typecheck(EnvSet e) throws TypeCheckError, EnvironmentError {
        ASTType tt = (declType != null) ? declType : expr.typecheck(e);
        tt = e.unfold(tt);

        ENV env = (tt instanceof ASTLinType) ? ENV.DELTA : ENV.GAMMA;
        e.openEnvScope(env);
        e.openEnvScope(ENV.SIGMA);

        e.bindToEnv(env, id, tt);
        if (declType != null) {
            ASTType exprType = expr.typecheck(e, tt);
            if (!(exprType.isSubtypeOf(tt, e))) throw new TypeCheckError(ErrorMessages.notSubtype(exprType, tt));
        }

        e.addEq(new ASTTEq(new ASTId(id), expr, tt));
        e.bindToEnv(ENV.SIGMA, id, tt);

        ASTType rt = body.typecheck(e);
        if (!(e.getEnv(ENV.DELTA).isEmpty()))
            throw new TypeCheckError(ErrorMessages.unusedLinearValues(e.getEnv(ENV.DELTA)));

        e.closeEnvScope(env);
        e.closeEnvScope(ENV.SIGMA);

        return rt;
	}

    public ASTNode weaknorm(Env<ASTNode> sub) {
        ASTNode normExpr = expr.weaknorm(sub);
        Env<ASTNode> env = sub.beginScope();
        env.assoc(id, normExpr);
        return body.weaknorm(env);
    }

    public ASTNode solve(Env<ASTType> sigma) {
        ASTNode nexpr = expr.solve(sigma);
        if (nexpr != null) return new ASTLet(id, nexpr, declType, body);
        ASTNode nbody = body.solve(sigma);
        if (nbody != null) return new ASTLet(id, expr, declType, nbody);
        return null;
    }

    public ASTNode subs(String subsId, ASTNode node) {
		return new ASTLet(id, expr.subs(subsId, node), declType, body.subs(subsId, node));
	}

    public boolean defequals(ASTNode o, Env<ASTType> sigma, AlphaEnv alpha) {
        return o instanceof ASTLet olet && expr.defequals(olet.getExpr(), sigma, alpha)
            && declType.defequals(olet.getDeclType(), sigma, alpha)
            && body.defequals(olet.getBody(), sigma, alpha.extend(id, olet.getId()));
    }

    @Override
    public String toString() {
        String typeString = (declType == null ? "" : String.format(" %s,", declType));
		return String.format("let(%s,%s %s, %s)", id, typeString, expr, body);
	}
}
