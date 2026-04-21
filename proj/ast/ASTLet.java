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
        ASTType tt = (bind.getType() != null) ? bind.getType() : bind.getExp().typecheck(e);
        tt = e.unfold(tt);

        ENV env = (tt instanceof ASTLinType) ? ENV.DELTA : ENV.GAMMA;
        e.openEnvScope(env);
        e.openEnvScope(ENV.SIGMA);

        e.bindToEnv(env, bind.getId(), tt);
        if (bind.getType() != null) {
            ASTType exprType = bind.getExp().typecheck(e);
            if (!(exprType.isSubtypeOf(tt, e))) {
                throw new TypeCheckError("types to bind are not subtypes: " + exprType.toStr() + " and " + tt.toStr());
            }
        }

        e.addEq(new ASTTEq(new ASTId(bind.getId()), bind.getExp(), tt));
        e.bindToEnv(ENV.PHI, bind.getId(), tt);

        ASTType rt = body.typecheck(e);
        if (!(e.getEnv(ENV.DELTA).isEmpty()))
            throw new TypeCheckError("there are unused linear values: " + e.getEnv(ENV.DELTA).toStr());

        e.closeEnvScope(env);
        e.closeEnvScope(ENV.SIGMA);

        return rt;
	}

    public ASTNode normalize(Environment<ASTType> sigma) {
        ASTNode normExp = bind.getExp().normalize(sigma);
        Environment<ASTType> env = sigma.beginScope();
        env.addEq(new ASTTEq(new ASTId(bind.getId()), normExp, sigma.find(bind.getId(), false)));
        return body.normalize(env);
    }

    public boolean defequals(ASTNode o) {
        return o instanceof ASTLet && ((ASTLet) o).getBind().getId().equals(bind.getId())
            && ((ASTLet) o).getBind().getExp().defequals(bind.getExp())
            && ((ASTLet) o).getBind().getType().defequals(bind.getType(), null)
            && ((ASTLet) o).getBody().defequals(body);
    }

    @Override
    public String toString() {
        ASTType tt = bind.getType();
        String typeString = (tt == null ? "" : String.format(" %s,", tt.toStr()));
		return String.format("let(%s,%s %s, %s)", bind.getId(), typeString, bind.getExp().toString(), body.toString());
	}
}
