package proj.ast;

import proj.values.*;
import proj.types.*;
import proj.defeq.TermClosure;
import proj.env.*;
import proj.env.EnvSet.ENV;
import proj.errors.*;

public class ASTLeteq extends ASTNode {
    String id;
    ASTNode expr, body;

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
        if (!(ve instanceof VRefl)) throw new InterpreterError(ErrorMessages.wrongValueToUnary("let", ve));
        en.assoc(id, ve);
        return body.eval(en);
    }

    public ASTType typecheck(EnvSet e, ASTType target) throws TypeCheckError {
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

    public ASTType puretypecheck(Env<ASTType> sigma, Env<ASTType> phi, ASTType target) throws TypeCheckError {
        ASTType t = expr.puretypecheck(sigma, phi, null);
        t = phi.unfold(t);
        if (!(t instanceof ASTTEq)) throw new TypeCheckError(ErrorMessages.illegalTypeToUnary("leteq", t));
        Env<ASTType> env = sigma.beginScope();
        env.assoc(id, t);
        return body.puretypecheck(env, phi, target);
    }

    public ASTNode weaknorm(Env<ASTNode> sub) {
        ASTNode normExpr = expr.weaknorm(sub);
        if (!(normExpr instanceof ASTRefl))
            return new ASTLeteq(id, normExpr, body.weaknorm(sub));
        Env<ASTNode> esub = sub.beginScope();
        esub.assoc(id, normExpr);
        return body.weaknorm(esub);
    }

    public TermClosure solve(Env<ASTType> sigma) {
        TermClosure nexpr = expr.solve(sigma);
        return nexpr == null ? null : new TermClosure(new ASTLeteq(id, nexpr.term(), body), sigma);
    }

    public ASTNode subs(String subsId, ASTNode node) {
		return new ASTLeteq(id, expr.subs(subsId, node), body.subs(subsId, node));
	}
}
