package proj.ast;

import proj.values.*;
import proj.types.*;
import proj.env.*;
import proj.errors.*;

public class ASTLFunc implements ASTNode  {
    String id;
    ASTNode body;
    ASTType argtype;

    public ASTLFunc(String i, ASTNode b, ASTType t) {
        id = i;
        body = b;
        argtype = t;
    }

    public void setBody(ASTNode b) {
        body = b;
    }

    public IValue eval(Environment<IValue> e) throws InterpreterError {
        return new VClos(e, id, body, true);
    }

    public ASTType typecheck(Environment<ASTType> e) throws TypeCheckError, InterpreterError {
        ASTType targtype = e.unfold(argtype);
        Environment<ASTType> en = e.beginScope();
        en.assoc(id, targtype);
        ASTType tb = body.typecheck(en);
        if (!(en.getLinears().isEmpty()))
            throw new TypeCheckError("there are unused linear values: " + en.deltaToStr());
        return new ASTTLollipop(targtype, tb);
	}
}
