package proj.ast;

import proj.values.*;
import proj.types.*;
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
        if (!(ve instanceof ASTRefl)) throw new InterpreterError(ErrorMessages.wrongValueToUnary("let", ve));
        en.assoc(id, ve);
        return body.eval(en);
    }
	
    public ASTType typecheck(EnvSet e) throws TypeCheckError, EnvironmentError {
        ASTType t = expr.typecheck(e);
        t = e.unfold(t);

        if (!(t instanceof ASTTEq)) throw new TypeCheckError("leteq: expected equality type");
        e.openEnvScope(ENV.SIGMA);
        e.bindToEnv(ENV.SIGMA, id, t);

        ASTType rt = body.typecheck(e);
        if (!(e.getEnv(ENV.DELTA).isEmpty()))
            throw new TypeCheckError("there are unused linear values: " + e.getEnv(ENV.DELTA));
        e.closeEnvScope(ENV.SIGMA);
        return rt;
    }

    public ASTNode weaknorm(Env<ASTType> sigma, Env<ASTNode> sub) {
        ASTNode normExpr = expr.weaknorm(sigma, sub);
        if (!(normExpr instanceof ASTRefl))
            return new ASTLeteq(id, normExpr, body.weaknorm(sigma, sub));
        Env<ASTNode> esub = sub.beginScope();
        esub.assoc(id, normExpr);
        return body.weaknorm(sigma, esub);
    }

    public ASTNode solve(Env<ASTType> sigma) {
        ASTNode nexpr = expr.solve(sigma);
        if (nexpr != null) return new ASTLeteq(id, nexpr, body);
        ASTNode nbody = body.solve(sigma);
        if (nbody != null) return new ASTLeteq(id, expr, nbody);
        return null;
    }

    public ASTNode subs(String subsId, ASTNode node) {
		return new ASTLeteq(id, expr.subs(subsId, node), body.subs(subsId, node));
	}

    public boolean defequals(ASTNode o, Env<ASTType> sigma, AlphaEnv alpha) {
        return o instanceof ASTLeteq oleteq && expr.defequals(oleteq.getExpr(), sigma, alpha)
            && body.defequals(oleteq.getBody(), sigma, alpha.extend(id, oleteq.getId()));
    }
}
