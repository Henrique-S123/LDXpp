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

    public void setBody(ASTNode b) {
        body = b;
    }

    public IValue eval(Environment<IValue> e) throws InterpreterError {
        return new VClos(e, id, body, false);
    }

    public ASTType typecheck(EnvSet e) throws TypeCheckError, EnvironmentError {
        ASTType targtype = e.unfold(argtype);
        e.openEnvScope(ENV.GAMMA);
        e.assocGamma(id, targtype);
        Environment<ASTType> prevDelta = e.popDelta();
        ASTType tb = body.typecheck(e);
        e.setDelta(prevDelta);
        e.closeGammaScope();
        return new ASTTArrow(targtype, tb);
	}

    public ASTNode normalize(Environment<ASTType> sigma) {
        return this;
    }

    public boolean defequals(ASTNode o) {
        // TODO
        return false;
    }
}
