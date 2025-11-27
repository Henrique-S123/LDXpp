package proj.ast;

import proj.values.*;
import proj.types.*;
import proj.env.*;
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

    public ASTType typecheck(EnvSet e) throws TypeCheckError, InterpreterError {
        ASTType targtype = e.getPhi().unfold(argtype);
        e.assocGamma(id, targtype);
        e.clearDelta();
        ASTType tb = body.typecheck(e);
        return new ASTTArrow(targtype, tb);
	}
}
