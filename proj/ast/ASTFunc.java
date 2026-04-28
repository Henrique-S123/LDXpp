package proj.ast;

import proj.values.*;
import proj.types.*;
import proj.env.*;
import proj.env.EnvSet.ENV;
import proj.errors.*;

public class ASTFunc implements ASTNode  {
    String id;
    ASTNode body;
    ASTType argtype;

    public ASTFunc(String i, ASTNode b, ASTType t) {
        id = i;
        body = b;
        argtype = t;
    }

    public String getId() {
        return id;
    }

    public ASTNode getBody() {
        return body;
    }

    public ASTType getArgtype() {
        return argtype;
    }

    public void setBody(ASTNode b) {
        body = b;
    }

    public IValue eval(Environment<IValue> e) throws InterpreterError {
        return new VClos(e, id, body, false);
    }

    public ASTType typecheck(EnvSet e) throws TypeCheckError, EnvironmentError {
        ASTType targtype = e.unfold(argtype);
        e.openEnvScope(ENV.GAMMA);
        e.openEnvScope(ENV.SIGMA);
        e.bindToEnv(ENV.GAMMA, id, targtype);
        e.bindToEnv(ENV.SIGMA, id, targtype);
        Environment<ASTType> prevDelta = e.popDelta();

        ASTType tb = body.typecheck(e);

        e.setEnv(ENV.DELTA, prevDelta);
        e.closeEnvScope(ENV.GAMMA);
        e.closeEnvScope(ENV.SIGMA);
        return new ASTTArrow(targtype, tb, id);
	}

    public ASTType typecheck(EnvSet e, ASTType t) throws TypeCheckError, EnvironmentError {
        ASTType tt = e.unfold(t);
        ASTType tdom, tcodom;
        String tid;

        if (tt instanceof ASTTArrow arrow) { tdom = arrow.getDom(); tcodom = arrow.getCodom(); tid = arrow.getId(); }
        else if (tt instanceof ASTTLollipop lolli) { tdom = lolli.getCodom(); tcodom = lolli.getCodom(); tid = lolli.getId(); }
        else throw new TypeCheckError("func: expected arrow type");

        Environment<ASTType> prevDelta = e.popDelta();
        e.openEnvScope(ENV.SIGMA);
        e.openEnvScope(ENV.GAMMA);

        ASTType targtype = e.unfold(argtype);
        if (!tdom.isSubtypeOf(targtype, e))
            throw new TypeCheckError(String.format("func: dom type %s is not subtype of arg type %s", tdom.toStr(), targtype.toStr()));

        e.bindToEnv(ENV.GAMMA, id, targtype);
        e.bindToEnv(ENV.SIGMA, id, targtype);

        ASTType tb = body.typecheck(e, tcodom);

        e.setEnv(ENV.DELTA, prevDelta);
        e.closeEnvScope(ENV.GAMMA);
        e.closeEnvScope(ENV.SIGMA);

        return new ASTTArrow(targtype, tb, id);
    }

    public ASTNode normalize(Environment<ASTType> sigma) {
        return new ASTFunc(id, body.normalize(sigma), argtype);
    }

    public boolean defequals(ASTNode o, Environment<ASTType> sigma) {
        return o instanceof ASTFunc && ((ASTFunc) o).getId().equals(id)
            && ((ASTFunc) o).getBody().defequals(body, sigma) && ((ASTFunc) o).getArgtype().defequals(argtype, sigma);
    }

    @Override
    public String toString() {
        return String.format("fn %s:%s => {%s}", id, argtype.toStr(), body.toString());
	}
}
