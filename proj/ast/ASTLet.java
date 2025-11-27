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

    public ASTType typecheck(EnvSet e) throws TypeCheckError, InterpreterError {
        for (Bind b : decls) {
            ASTType tt = b.getType();
            if (tt != null) {
                tt = e.getPhi().unfold(tt);
                // premptive type binding
                e.assocVar(b.getId(), tt);
                ASTType valuetype = b.getExp().typecheck(e);
                if (!(valuetype.isSubtypeOf(tt, e.getPhi()))) {
                    throw new TypeCheckError("types to bind are not subtypes: " + valuetype.toStr() + " and " + tt.toStr());
                }
            } else {
                ASTType t = b.getExp().typecheck(e);
                t = e.getPhi().unfold(t);
                e.assocVar(b.getId(), t);
            }
        }
        ASTType rt = body.typecheck(e);
        if (!(e.getDelta().isEmpty()))
            throw new TypeCheckError("there are unused linear values: " + e.getDelta().toStr());
        return rt;
	}
}
