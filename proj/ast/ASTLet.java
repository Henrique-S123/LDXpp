package proj.ast;

import proj.values.*;
import proj.types.*;
import proj.env.*;
import proj.env.EnvSet.ENV;
import proj.errors.*;

public class ASTLet implements ASTNode {
    Bind bind;
    ASTNode body;

    public IValue eval(Environment<IValue> e) throws InterpreterError {
        Environment<IValue> en = e.beginScope();
        en.assoc(bind.getId(), bind.getExp().eval(en));
        return body.eval(en);
    }

    public ASTLet(Bind bi, ASTNode b) {
        bind = bi;
        body = b;
    }

    public Bind getBind() {
        return bind;
    }

    public ASTNode getBody() {
        return body;
    } 

    public ASTType typecheck(EnvSet e) throws TypeCheckError, EnvironmentError {
        String id = bind.getId();
        ASTType declType = bind.getType();
        ASTNode expr = bind.getExp();

        ASTType tt = (declType != null) ? declType : expr.typecheck(e);
        tt = e.unfold(tt);

        ENV env = (tt instanceof ASTLinType) ? ENV.DELTA : ENV.GAMMA;
        e.openEnvScope(env);
        e.openEnvScope(ENV.SIGMA);

        e.bindToEnv(env, id, tt);
        if (declType != null) {
            ASTType exprType = expr.typecheck(e, tt);
            if (!(exprType.isSubtypeOf(tt, e))) {
                throw new TypeCheckError("types to bind are not subtypes: " + exprType + " and " + tt);
            }
        }

        e.addEq(new ASTTEq(new ASTId(id), expr, tt));
        e.bindToEnv(ENV.SIGMA, id, tt);

        ASTType rt = body.typecheck(e);
        if (!(e.getEnv(ENV.DELTA).isEmpty()))
            throw new TypeCheckError("there are unused linear values: " + e.getEnv(ENV.DELTA));

        e.closeEnvScope(env);
        e.closeEnvScope(ENV.SIGMA);

        return rt;
	}

    public ASTType typecheck(EnvSet e, ASTType t) throws TypeCheckError, EnvironmentError {
        return typecheck(e);
    }

    public ASTNode normalize(Environment<ASTType> sigma, Environment<ASTNode> sub) {
        ASTNode normExp = bind.getExp().normalize(sigma, sub);
        Environment<ASTNode> env = sub.beginScope();
        env.assoc(bind.getId(), normExp);
        return body.normalize(sigma, env);
    }

    public boolean defequals(ASTNode o, Environment<ASTType> sigma) {
        return o instanceof ASTLet olet && olet.getBind().getId().equals(bind.getId())
            && olet.getBind().getExp().defequals(bind.getExp(), sigma)
            && olet.getBind().getType().defequals(bind.getType(), sigma)
            && olet.getBody().defequals(body, sigma);
    }

    @Override
    public String toString() {
        ASTType tt = bind.getType();
        String typeString = (tt == null ? "" : String.format(" %s,", tt));
		return String.format("let(%s,%s %s, %s)", bind.getId(), typeString, bind.getExp(), body);
	}
}
