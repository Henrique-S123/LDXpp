package proj.ast;

import proj.values.*;
import proj.types.*;
import proj.env.*;
import proj.errors.*;

import java.util.List;

public class ASTLet implements ASTNode {
    List<Bind> decls;
    ASTNode body;

    public IValue eval(Environment<IValue> e) throws InterpreterError {
        Environment<IValue> en = e.beginScope();
        for (Bind b : decls) {
            en.assoc(b.getId(), b.getExp().eval(en));
        }
        return body.eval(en);
    }

    public ASTLet(List<Bind> d, ASTNode b) {
        decls = d;
        body = b;
    }

    public ASTType typecheck(Environment<ASTType> e) throws TypeCheckError, InterpreterError {
        Environment<ASTType> en = e.beginScope();
        for (Bind b : decls) {
            ASTType tt = b.getType();
            if (tt != null) {
                tt = e.unfold(tt);
                // premptive type binding
                en.assoc(b.getId(), tt);
                ASTType valuetype = b.getExp().typecheck(en);
                if (!(valuetype.isSubtypeOf(tt, e))) {
                    throw new TypeCheckError("types to bind are not subtypes: " + valuetype.toStr() + " and " + tt.toStr());
                }
            } else {
                ASTType t = b.getExp().typecheck(en);
                t = e.unfold(t);
                en.assoc(b.getId(), t);
            }
        }
        ASTType rt = body.typecheck(en);
        if (!(en.getLinears().isEmpty()))
            throw new TypeCheckError("there are unused linear values: " + en.deltaToStr());
        return rt;
	}
}
