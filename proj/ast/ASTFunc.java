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

    public ASTType typecheck(Environment<ASTType> e) throws TypeCheckError, InterpreterError {
        ASTType targtype = e.unfold(argtype);
        Environment<ASTType> en = e.beginScope();
        en.assocUnrestricted(id, targtype);
        ASTType tb = body.typecheck(en);
        if (!en.getUsedLinears().isEmpty()) {
            throw new TypeCheckError("nonlinear functions must not use external linear values");
        }
        return new ASTTArrow(targtype, tb);
	}
}
