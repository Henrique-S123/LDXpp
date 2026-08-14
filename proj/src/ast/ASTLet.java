package proj.src.ast;

import proj.src.values.*;
import proj.src.types.*;
import proj.src.env.*;
import proj.src.env.EnvSet.ENV;
import proj.src.env.PureEnvSet.PENV;
import proj.src.errors.*;

public class ASTLet extends ASTNode {
    private final String id;
    private final ASTNode expr, body;
    private final ASTType declType;

    public ASTLet(String i, ASTNode e, ASTType t, ASTNode b) {
        id = i; expr = e; declType = t; body = b;
    }

    public String getId() { return id; }

    public ASTNode getExpr() { return expr; }

    public ASTType getDeclType() { return declType; }

    public ASTNode getBody() { return body; }

    public IValue eval(Env<IValue> e) throws InterpreterError {
        Env<IValue> en = e.beginScope();
        en.assoc(id, expr.eval(en));
        return body.eval(en);
    }

    public ASTType typecheck(EnvSet e, ASTType target) throws TypeCheckError {
        if (declType != null) declType.check(new PureEnvSet(e));

        ASTType texp = expr.typecheck(e, declType);
        texp = e.unfold(texp);
        if (declType != null && !texp.isSubtypeOf(declType, new PureEnvSet(e)))
            throw new TypeCheckError(ErrorMessages.notSubtype(texp, declType));
        ASTType tt = (declType != null) ? e.unfold(declType) : texp;

        ENV env = tt.isLinear() ? ENV.DELTA : ENV.GAMMA;
        e.openEnvScope(env);
        Binder<ASTType> b = e.bindToEnv(env, id, tt);
        e.openEnvScope(ENV.SIGMA);
        e.bindToEnv(ENV.SIGMA, id, b);
        e.bindToEnv(ENV.SIGMA, e.getFreshId(), new ASTTEq(new ASTId(id, b.getId()), expr, tt));

        ASTType rt = body.typecheck(e, target);
        if (!e.getUnusedScopeLinears().isEmpty()) throw new TypeCheckError(ErrorMessages.unusedLinearValues(e.getUnusedLinears()));

        e.closeEnvScope(env);
        e.closeEnvScope(ENV.SIGMA);
        return rt;
	}

    public ASTType puretypecheck(PureEnvSet pe, ASTType target) throws TypeCheckError {
        if (declType != null) declType.check(pe);

        ASTType texp = expr.puretypecheck(pe, declType);
        texp = pe.unfold(texp);
        if (declType != null && !texp.isSubtypeOf(declType, pe))
            throw new TypeCheckError(ErrorMessages.notSubtype(texp, declType));
        ASTType tt = (declType != null) ? pe.unfold(declType) : texp;

        pe.openEnvScope(PENV.SIGMA);
        Binder<ASTType> b = pe.bindToEnv(PENV.SIGMA, id, tt);
        pe.bindToEnv(PENV.SIGMA, pe.getFreshId(), new ASTTEq(new ASTId(id, b.getId()), expr, tt));

        ASTType rt = body.puretypecheck(pe, target);
        
        pe.closeEnvScope(PENV.SIGMA);
        return rt;
    }

    public ASTNode weaknorm(Env<ASTNode> sub) {
        ASTNode normExpr = expr.weaknorm(sub);
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
