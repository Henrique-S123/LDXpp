package proj.ast;

import proj.values.*;
import proj.types.*;
import proj.env.*;
import proj.env.EnvSet.ENV;
import proj.errors.*;

public class ASTLeteq extends ASTNode {
    private final String id;
    private final ASTNode expr, body;

    public ASTLeteq(String i, ASTNode e, ASTNode b) {
        id = i;
        expr = e;
        body = b;
    }

    public String getId() {
        return id;
    }

    public ASTNode getExpr() {
        return expr;
    }

    public ASTNode getBody() {
        return body;
    }

    public IValue eval(Env<IValue> e) throws InterpreterError {
        Env<IValue> en = e.beginScope();
        IValue ve = expr.eval(e);
        if (!(ve instanceof VRefl)) throw new InterpreterError(ErrorMessages.wrongValueToUnary("leteq", ve));
        en.assoc(id, ve);
        return body.eval(en);
    }

    public ASTType typecheck(EnvSet e, ASTType target) throws TypeCheckError {
        this.setSig(e.getSigma());
        ASTType t = expr.typecheck(e, null);
        t = e.unfold(t);

        if (!(t instanceof ASTTEq)) throw new TypeCheckError(ErrorMessages.illegalTypeToUnary("leteq", t));
        e.openEnvScope(ENV.SIGMA);
        e.bindToEnv(ENV.SIGMA, id, t);

        ASTType rt = body.typecheck(e, target);
        if (!e.getUnusedScopeLinears().isEmpty()) throw new TypeCheckError(ErrorMessages.unusedLinearValues(e.getUnusedLinears()));
        e.closeEnvScope(ENV.SIGMA);
        return rt;
    }

    public ASTType puretypecheck(Env<ASTType> sigma, Env<ASTType> phi, AlphaEnv alpha, ASTType target) throws TypeCheckError {
        this.setSig(sigma);
        ASTType t = expr.puretypecheck(sigma, phi, alpha, null);
        t = phi.unfold(t);
        if (!(t instanceof ASTTEq)) throw new TypeCheckError(ErrorMessages.illegalTypeToUnary("leteq", t));
        Env<ASTType> env = sigma.beginScope();
        env.assoc(id, t);
        return body.puretypecheck(env, phi, alpha, target);
    }

    public ASTNode weaknorm(Env<ASTNode> sub) {
        ASTNode normExpr = expr.weaknorm(sub);
        // TODO: mention proof irrelevance
        // if (!(normExpr instanceof ASTRefl))
        //    return new ASTLeteq(id, normExpr, body.weaknorm(sub));
        Env<ASTNode> esub = sub.beginScope();
        esub.assoc(id, normExpr);
        return body.weaknorm(esub);
    }

    public ASTLeteq solve(Env<ASTType> sigma) {
        ASTNode nexpr = expr.solve(sigma);
        return nexpr == null ? null : new ASTLeteq(id, nexpr, body);
    }

    public ASTLeteq subs(String subsId, ASTNode node) {
		return new ASTLeteq(id, expr.subs(subsId, node), body.subs(subsId, node));
	}

    @Override
    public String toString() {
		return String.format("leteq(%s, %s, %s)", id, expr, body);
	}
}
